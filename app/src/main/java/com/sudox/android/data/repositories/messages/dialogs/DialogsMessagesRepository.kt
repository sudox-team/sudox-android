package com.sudox.android.data.repositories.messages.dialogs

import com.sudox.android.common.helpers.formatMessageText
import com.sudox.android.data.database.dao.messages.DialogMessagesDao
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.models.messages.dialogs.dto.DialogHistoryDTO
import com.sudox.android.data.models.messages.dialogs.dto.DialogMessageDTO
import com.sudox.android.data.models.messages.dialogs.dto.DialogSendMessageDTO
import com.sudox.android.data.models.messages.dialogs.dto.LastDialogsMessagesDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogsMessagesRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                                    private val authRepository: AuthRepository,
                                                    private val accountRepository: AccountRepository,
                                                    private val usersRepository: UsersRepository,
                                                    private val dialogMessagesDao: DialogMessagesDao) {

    // ID загруженных чатов ...
    private val loadedDialogsRecipientIds = hashMapOf<Long, Int>()
    private val messagesSendingThreadContext = newSingleThreadContext("Sudox Dialogs Messages Sending Queue")
    private val messagesLoadingThreadContext = newFixedThreadPoolContext(1, "Sudox Dialogs Messages Loading Queue")

    // PublishSubject для доставки новых сообщений.
    val globalNewMessagesChannel: BroadcastChannel<DialogMessage> = ConflatedBroadcastChannel()
    var globalSentMessageChannel: BroadcastChannel<DialogMessage> = ConflatedBroadcastChannel()
    var dialogDialogNewMessageChannel: BroadcastChannel<DialogMessage>? = null
    var dialogDialogHistoryChannel: BroadcastChannel<Pair<LoadingType, List<DialogMessage>>>? = null
    var dialogDialogSentMessageChannel: BroadcastChannel<DialogMessage>? = null
    var dialogRecipientUpdateChannel: BroadcastChannel<User>? = null
    var openedDialogRecipientId: Long = 0

    // Защита от десинхронизации данных при скролле (от двух одновременных запросов)
    private var dialogHistoryEnded: Boolean = false

    init {
        listenNewMessages()
        listenSessionStatus()
    }

    private fun listenSessionStatus() = GlobalScope.launch(Dispatchers.IO) {
        for (status in authRepository
                .accountSessionStateChannel
                .openSubscription()) {

            if (status) {
                dialogHistoryEnded = false
                loadedDialogsRecipientIds.clear() // Clean initial cache.

                // Reload initial copy
                if (openedDialogRecipientId != 0L) {
                    loadMessagesFromNetwork(openedDialogRecipientId).await()

                    // Грузим юзера для апдейта ...
                    val user = usersRepository
                            .loadUser(openedDialogRecipientId)
                            .await()

                    if (user != null) dialogRecipientUpdateChannel?.send(user)
                }
            } else {
                endDialog()
            }
        }
    }

    private fun listenNewMessages() {
        protocolClient.listenMessage<DialogMessageDTO>("updates.dialogs.new") {
            val accountId = accountRepository.cachedAccount?.id ?: return@listenMessage
            val recipientId = if (it.peer == accountId) it.sender else it.peer
            val direction = if (it.peer != accountId) MessageDirection.TO else MessageDirection.FROM
            val message = DialogMessage(
                    mid = it.id,
                    sender = it.sender,
                    peer = it.peer,
                    message = it.message,
                    date = it.date,
                    direction = direction,
                    status = MessageStatus.DELIVERED)

            // Insert into database
            dialogMessagesDao.insertOne(message)

            // Notify listeners about new message
            globalNewMessagesChannel.sendBlocking(message)

            // If current dialog active notify subscribers
            if (openedDialogRecipientId == recipientId) {
                dialogDialogNewMessageChannel?.sendBlocking(message)
            }
        }
    }

    fun openDialog(recipientId: Long) {
        openedDialogRecipientId = recipientId
        dialogDialogHistoryChannel = ConflatedBroadcastChannel()
        dialogDialogSentMessageChannel = ConflatedBroadcastChannel()
        dialogDialogNewMessageChannel = ConflatedBroadcastChannel()
        dialogRecipientUpdateChannel = ConflatedBroadcastChannel()
    }

    fun endDialog() {
        openedDialogRecipientId = 0
        dialogHistoryEnded = false

        // Close old channel
        if (dialogDialogNewMessageChannel != null && !dialogDialogNewMessageChannel!!.isClosedForSend) {
            dialogDialogNewMessageChannel!!.close()
        }

        if (dialogDialogHistoryChannel != null && !dialogDialogHistoryChannel!!.isClosedForSend) {
            dialogDialogHistoryChannel!!.close()
        }

        if (dialogDialogSentMessageChannel != null && !dialogDialogSentMessageChannel!!.isClosedForSend) {
            dialogDialogSentMessageChannel!!.close()
        }

        if (dialogRecipientUpdateChannel != null && !dialogRecipientUpdateChannel!!.isClosedForSend) {
            dialogRecipientUpdateChannel!!.close()
        }
    }

    fun loadInitialMessages(recipientId: Long) {
        loadMessagesFromDatabase(recipientId)

        if (!loadedDialogsRecipientIds.contains(recipientId) && protocolClient.isValid()) {
            loadMessagesFromNetwork(recipientId)
        }
    }

    fun loadPagedMessages(recipientId: Long, offset: Int) {
        if (offset <= 0 || dialogHistoryEnded) return

        // Get offset cache
        val cachedOffset = loadedDialogsRecipientIds[recipientId] ?: -1

        // Initial copy loaded from database
        if (!protocolClient.isValid() || cachedOffset != -1 && cachedOffset >= offset) {
            loadMessagesFromDatabase(recipientId, offset)
        } else if (cachedOffset == -1 && offset == 0 || offset - cachedOffset >= 20) {
            loadMessagesFromNetwork(recipientId, offset)
        }
    }

    private fun loadMessagesFromDatabase(recipientId: Long, offset: Int = 0): Deferred<Unit?> = GlobalScope.async(messagesLoadingThreadContext) {
        val messages = dialogMessagesDao.loadMessages(recipientId, offset, 20)

        // Remove from cache
        if (messages.isEmpty()) {
            if (offset == 0) {
                removeSavedMessages(recipientId, false)
                dialogDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, messages))
            } else {
                dialogHistoryEnded = true
            }
        } else {
            if (offset == 0) {
                dialogDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, messages))
            } else {
                dialogDialogHistoryChannel?.sendBlocking(Pair(LoadingType.PAGING, messages))
            }
        }
    }

    private fun loadMessagesFromNetwork(recipientId: Long, offset: Int = 0) = GlobalScope.async(messagesLoadingThreadContext) {
        val newOffset = if (offset > 0) recalculateNetworkOffset(recipientId, offset) else 0

        try {
            val dialogHistoryDTO = protocolClient.makeRequestWithControl<DialogHistoryDTO>("dialogs.getHistory", DialogHistoryDTO().apply {
                this.id = recipientId
                this.limit = 20
                this.offset = newOffset
            }).await()

            if (dialogHistoryDTO.isSuccess()) {
                val messages = toStorableMessages(dialogHistoryDTO.messages)

                // Save messages into database & update offset cache (for offline mode supporting)
                dialogMessagesDao.updateOrInsertMessages(messages)
                updateCachedOffset(recipientId, newOffset)

                // offset == 0 => first initializing
                if (newOffset == 0) {
                    dialogDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL,
                            dialogMessagesDao.buildInitialCopy(recipientId, messages)))
                } else {
                    dialogDialogHistoryChannel?.sendBlocking(Pair(LoadingType.PAGING, messages))
                }
            } else {
                if (newOffset == 0) {
                    removeSavedMessages(recipientId)

                    // newOffset == 0 => first initializing
                    dialogDialogHistoryChannel?.sendBlocking(Pair(LoadingType.INITIAL, dialogMessagesDao.loadDeliveringMessages(recipientId)))
                } else if (dialogHistoryDTO.error == Errors.INVALID_USER) {
                    dialogHistoryEnded = true
                } else null
            }
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    fun loadLastMessages(offset: Int, limit: Int) = GlobalScope.async(Dispatchers.IO) {
        if (protocolClient.isValid() && authRepository.sessionIsValid) {
            loadLastMessagesFromNetwork(offset, limit)
        } else {
            loadLastMessagesFromDatabase(offset, limit)
        }
    }

    private suspend fun loadLastMessagesFromNetwork(offset: Int, limit: Int): ArrayList<DialogMessage> {
        try {
            val lastDialogsMessages = protocolClient.makeRequestWithControl<LastDialogsMessagesDTO>("dialogs.get", LastDialogsMessagesDTO().apply {
                this.offset = offset
                this.limit = limit
            }).await()

            if (lastDialogsMessages.containsError()) {
                return arrayListOf()
            } else if (lastDialogsMessages.messages.isEmpty()) {
                return arrayListOf()
            }

            val storableMessages = toStorableMessages(lastDialogsMessages.messages)
            val savedMessages = dialogMessagesDao.insertAll(storableMessages)

            return loadLastMessagesFromDatabase(offset, limit)
        } catch (e: NetworkException) {
            return loadLastMessagesFromDatabase(offset, limit)
        }
    }

    private fun loadLastMessagesFromDatabase(offset: Int, limit: Int): ArrayList<DialogMessage> {
        return ArrayList(dialogMessagesDao.loadLastMessages(offset, limit))
    }

    // TODO: Переместить отправку сообщения в отдельный канал!
    @Suppress("NAME_SHADOWING")
    fun sendTextMessage(recipientId: Long, text: String) = GlobalScope.launch(Dispatchers.IO) {
        val accountId = accountRepository.cachedAccount?.id ?: return@launch
        val text = formatMessageText(text)

        // Запрет отправки пустого поля.
        if (text.isEmpty()) return@launch

        // Отправка ...
        sendMessage(DialogMessage(
                sender = accountId,
                peer = recipientId,
                message = text,
                date = System.currentTimeMillis(),
                direction = MessageDirection.TO,
                status = MessageStatus.IN_DELIVERY)).await()
    }

    private suspend fun sendMessage(message: DialogMessage) = GlobalScope.async(messagesSendingThreadContext) {
        // It's new message
        if (message.lid == 0) {
            message.lid = dialogMessagesDao.insertOne(message).toInt()
        }

        // Change status of message
        message.status = MessageStatus.IN_DELIVERY

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, message.peer)

        // Sending ...
        try {
            val sendDialogMessageDTO = protocolClient.makeRequestWithControl<DialogSendMessageDTO>("dialogs.send", DialogSendMessageDTO().apply {
                this.peerId = message.peer
                this.message = message.message
            }).await()

            if (sendDialogMessageDTO.isSuccess()) {
                message.mid = sendDialogMessageDTO.id
                message.date = sendDialogMessageDTO.date
                message.status = MessageStatus.DELIVERED
            } else {
                message.status = MessageStatus.NOT_DELIVERED
            }
        } catch (e: NetworkException) {
            message.status = MessageStatus.NOT_DELIVERED
        }

        // Update data ...
        dialogMessagesDao.updateOne(message)

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, message.peer)
    }

    private suspend fun notifyMessageStatus(message: DialogMessage, recipientId: Long) {
        // Notify subscribers, that message in delivery
        globalSentMessageChannel.send(message)

        // For current dialog
        if (openedDialogRecipientId == recipientId)
            dialogDialogSentMessageChannel?.send(message)
    }

    private fun recalculateNetworkOffset(recipientId: Long, initialOffset: Int): Int {
        return Math.max(initialOffset - dialogMessagesDao.countDeliveringMessages(recipientId), 0)
    }

    private fun updateCachedOffset(recipientId: Long, offset: Int) {
        loadedDialogsRecipientIds[recipientId] = offset // Allow load from this offset
    }

    private fun removeSavedMessages(recipientId: Long, removeFromDb: Boolean = true) {
        loadedDialogsRecipientIds.minusAssign(recipientId)

        // Экономим на запросах к БД.
        if (removeFromDb) dialogMessagesDao.removeAll(recipientId)
    }

    internal fun removeAllSavedMessages(removeFromDb: Boolean = true) {
        loadedDialogsRecipientIds.clear()

        // Экономим на запросах к БД.
        if (removeFromDb) dialogMessagesDao.removeAll()
    }

    internal fun toStorableMessages(messages: ArrayList<DialogMessageDTO>): ArrayList<DialogMessage> {
        val accountId = accountRepository.cachedAccount?.id ?: return arrayListOf()

        return ArrayList(messages.map {
            val direction = if (it.peer != accountId) MessageDirection.TO else MessageDirection.FROM

            DialogMessage(
                    mid = it.id,
                    sender = it.sender,
                    peer = it.peer,
                    message = it.message,
                    date = it.date,
                    direction = direction,
                    status = MessageStatus.DELIVERED)
        })
    }
}
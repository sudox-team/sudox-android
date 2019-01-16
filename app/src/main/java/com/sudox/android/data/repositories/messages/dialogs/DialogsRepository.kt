package com.sudox.android.data.repositories.messages.dialogs

import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val dialogsMessagesRepository: DialogsMessagesRepository,
                                            private val usersRepository: UsersRepository,
                                            private val authRepository: AuthRepository) {

    fun loadDialogs(offset: Int = 0, limit: Int = 20) = GlobalScope.async(Dispatchers.IO) {
        val lastMessages = dialogsMessagesRepository
                .loadLastMessages(offset, limit)
                .await()

        if (lastMessages.isEmpty()) {
            return@async arrayListOf<Dialog>()
        } else {
            val lastMessagesRecipientsIds = lastMessages.map { it.getRecipientId() }
            val lastMessagesRecipients = usersRepository
                    .loadUsers(lastMessagesRecipientsIds)
                    .await()
            val dialogs = ArrayList<Dialog>()

            // Find pair & build dialog
            lastMessages.forEach { message ->
                val user = lastMessagesRecipients.find { it.uid == message.getRecipientId() }

                if (user != null) dialogs.plusAssign(Dialog(user, message))
            }
            
            return@async dialogs
        }
    }
}
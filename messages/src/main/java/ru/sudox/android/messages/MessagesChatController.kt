package ru.sudox.android.messages

import android.content.Context
import android.os.Handler
import android.view.View
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.messages.adapters.MessagesAdapter
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.PeopleVO
import ru.sudox.android.people.common.vos.SimplePeopleVO
import ru.sudox.design.viewlist.ViewList

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : ViewListController<MessagesAdapter>(true, true) {

    override fun bindView(view: View) {
        super.bindView(view)

        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        val handler = Handler()

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "1"
                override val text: String = "Первое сообщение в Sudox! Ответь мне если оно у тебя отображается"
                override val attachments: ArrayList<MediaAttachmentVO>? = null
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = true
                override val isSentByMe = false
                override val sentTime: Long = 0L

                override fun getMessageStatus(context: Context) = null
            })
        }, 1000L)

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "2"
                override val text: String = "Да, сообщение отображается. Все отлично работает!"
                override val attachments: ArrayList<MediaAttachmentVO>? = null
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = true
                override val isSentByMe = true
                override val sentTime: Long = 1L

                override fun getMessageStatus(context: Context) = null
            })
        }, 3000L)

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "1"
                override val text: String = "Ну все, сейчас проверим отправку изображений"
                override val attachments: ArrayList<MediaAttachmentVO>? = null
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = true
                override val isSentByMe = false
                override val sentTime: Long = 2L

                override fun getMessageStatus(context: Context) = null
            })
        }, 5000L)

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "1"
                override val text: String? = null
                override val attachments: ArrayList<MediaAttachmentVO> = arrayListOf(ImageAttachmentVO(7L).apply {
                    height = 387
                    width = 620
                })
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = false
                override val isSentByMe = false
                override val sentTime: Long = 3L

                override fun getMessageStatus(context: Context) = null
            })
        }, 8000L)

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "1"
                override val text: String? = "Отпиши если картинка дошла до тебя и отображается нормально."
                override val attachments: ArrayList<MediaAttachmentVO>? = null
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = false
                override val isSentByMe = false
                override val sentTime: Long = 4L

                override fun getMessageStatus(context: Context) = null
            })
        }, 10000L)

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "1"
                override val text: String? = null
                override val attachments: ArrayList<MediaAttachmentVO> = arrayListOf(ImageAttachmentVO(7L).apply {
                    height = 387
                    width = 620
                })
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = false
                override val isSentByMe = true
                override val sentTime: Long = 5L

                override fun getMessageStatus(context: Context) = null
            })
        }, 12000L)

        handler.postDelayed({
            adapter!!.messageVOs.add(object : MessageVO {
                override val id: String = ""
                override val senderId: String = "2"
                override val text: String = "Сейчас я отправил картинку!"
                override val attachments: ArrayList<MediaAttachmentVO>? = null
                override val likes: ArrayList<PeopleVO>? = null
                override val isFirstMessage = true
                override val isSentByMe = true
                override val sentTime: Long = 6L

                override fun getMessageStatus(context: Context) = null
            })
        }, 12000L)
    }

    override fun getAdapter(viewList: ViewList): MessagesAdapter {
        return MessagesAdapter(glide, activity!!)
    }
}
package ru.sudox.android.messages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import ru.sudox.android.core.CoreController
import ru.sudox.android.media.vos.MediaAttachmentVO
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.messages.views.MessageItemView
import ru.sudox.android.messages.vos.MessageVO
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.PeopleVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        return MessageItemView(activity!!).apply {
            updatePadding(32, 32, 32, 32)

            setVO(object : MessageVO {
                override val id: String = ""
                override val text: String? = null
                override val attachments: ArrayList<MediaAttachmentVO>? = arrayListOf(ImageAttachmentVO(7L).apply {
                    height = 387
                    width = 620
                })
                override val likes: ArrayList<PeopleVO>? = null
                override val sentByMe: Boolean = true
                override val sentTime: Long = 0L
            }, glide)
        }
    }
}
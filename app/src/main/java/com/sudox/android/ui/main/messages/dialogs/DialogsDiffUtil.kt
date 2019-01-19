package com.sudox.android.ui.main.messages.dialogs

import android.support.v7.util.DiffUtil
import com.sudox.android.data.models.messages.dialogs.Dialog

class DialogsDiffUtil(val newDialogs: ArrayList<Dialog>,
                      val oldDialogs: ArrayList<Dialog>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newDialogs[newItemPosition].recipient.uid == oldDialogs[oldItemPosition].recipient.uid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newDialogs[newItemPosition].lastMessage.message == oldDialogs[oldItemPosition].lastMessage.message
    }

    override fun getOldListSize(): Int = oldDialogs.size
    override fun getNewListSize(): Int = newDialogs.size
}
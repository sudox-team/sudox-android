package com.sudox.android.ui.diffutil

import android.support.v7.util.DiffUtil
import com.sudox.android.data.database.model.UserChatMessage

class UserChatMessagesDiffUtil(private val oldMessagesList: List<UserChatMessage>,
                               private val newMessagesList: List<UserChatMessage>) : DiffUtil.Callback(){

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newMessage = newMessagesList[newItemPosition]
        val oldMessage = oldMessagesList[oldItemPosition]

        return newMessage.mid == oldMessage.mid
    }


    override fun getOldListSize() = oldMessagesList.size
    override fun getNewListSize() = newMessagesList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newMessage = newMessagesList[newItemPosition]
        val oldMessage = oldMessagesList[oldItemPosition]

        return newMessage == oldMessage
    }
}
package com.sudox.android.data.database.model.messages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus

@Entity(tableName = "dialogs_messages")
data class DialogMessage(@PrimaryKey(autoGenerate = true) var lid: Long = 0,
                         @ColumnInfo var mid: Long = 0,
                         @ColumnInfo var sender: Long,
                         @ColumnInfo var peer: Long,
                         @ColumnInfo var message: String,
                         @ColumnInfo var date: Long,
                         @ColumnInfo var direction: MessageDirection,
                         @ColumnInfo var status: MessageStatus) {

    fun getRecipientId() = if (direction == MessageDirection.TO) peer else sender
}
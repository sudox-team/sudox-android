package com.sudox.android.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.sudox.android.data.models.users.UserType

@Entity(tableName = "users")
data class User(@PrimaryKey var uid: String,
                @ColumnInfo var name: String,
                @ColumnInfo var nickname: String,
                @ColumnInfo var photo: String,
                @ColumnInfo var phone: String? = null,
                @ColumnInfo var status: String? = null,
                @ColumnInfo var bio: String? = null,
                @ColumnInfo(index = true) var type: UserType)

//@Entity(tableName = "users")
//class User {
//
//    @PrimaryKey
//    lateinit var uid: String
//
//    @ColumnInfo
//    lateinit var name: String
//
//    @ColumnInfo
//    lateinit var nickname: String
//
//    @ColumnInfo
//    lateinit var avatar: String
//
//    @ColumnInfo
//    var phone: String? = null
//
//    @ColumnInfo
//    var status: String? = null
//
//    @ColumnInfo
//    var bio: String? = null
//
//    /**
//     * 1 - Профиль
//     * 2 - Контакт
//     * 3 - Неизвестный
//     */
//    @ColumnInfo
//    var type: Int = 3
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as User
//
//        if (uid != other.uid) return false
//        if (phone != other.phone) return false
//        if (name != other.name) return false
//        if (nickname != other.nickname) return false
//        if (avatar != other.avatar) return false
//        if (status != other.status) return false
//        if (bio != other.bio) return false
//        if (type != other.type) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = uid.hashCode()
//
//        result = 31 * result + name.hashCode()
//        result = 31 * result + nickname.hashCode()
//        result = 31 * result + avatar.hashCode()
//
//        if(phone != null)
//            result = 31 * result + phone!!.hashCode()
//
//        if(status != null)
//            result = 31 * result + status!!.hashCode()
//
//        if(bio != null)
//            result = 31 * result + bio!!.hashCode()
//
//        result = 31 * result + type.hashCode()
//        return result
//    }
//
//    companion object {
//        @Deprecated(message = "Removed in future builds")
//        val TRANSFORMATION_FROM_CONTACT_INFO_DTO: (ContactInfoDTO) -> (User) = {
//            User().apply {
//                uid = it.id
//                name = it.name
//                nickname = it.nickname
//                avatar = it.photo
//                status = it.status
//                bio = it.bio
//                type = 2
//            }
//        }
//
//        @Deprecated(message = "Removed in future builds")
//        val TRANSFORMATION_FROM_CONTACT_CHANGE_DTO: (ContactAddDTO) -> (User) = {
//            User().apply {
//                uid = it.id
//                name = it.nameS
//                nickname = it.nickname
//                avatar = it.photo
//                status = it.status
//                bio = it.bio
//                type = 2
//            }
//        }
//
//        @Deprecated(message = "Removed in future builds")
//        val TRANSFORMATION_TO_USER_CHAT_RECIPIENT: (User) -> (UserChatRecipient) = {
//            UserChatRecipient().apply {
//                uid = it.uid
//                name = it.name
//                nickname = it.nickname
//                photo = it.avatar
//            }
//        }
//    }
//}
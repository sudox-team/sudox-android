package com.sudox.android.data.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudox.android.data.database.model.User

@Dao
interface UserDao {

    @Query("DELETE FROM users")
    fun removeAll()

    @Query("DELETE FROM users WHERE uid = :id")
    fun removeOne(id: String)

    @Insert
    fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(user: User)

    @Query("SELECT * FROM users")
    fun loadAll(): LiveData<List<User>>

    /**
     * 1 - Профиль
     * 2 - Контакт
     * 3 - Неизвестный
     */
    @Query("SELECT * FROM users WHERE type = :type")
    fun getUserByType(type: Int): LiveData<List<User>>
}
package com.sudox.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.sudox.android.database.model.Contact

@Dao
interface ContactsDao {

    @Insert(onConflict = REPLACE)
    fun insertContact(contact: Contact)

    @Query("delete from contacts_table where cid=:id")
    fun deleteContactById(id: String)

    @Query("SELECT * FROM contacts_table")
    fun getContacts(): List<Contact>

    @Query("delete from contacts_table")
    fun deleteAllContacts()
}
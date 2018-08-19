package com.sudox.android.ui.main.contacts

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.main.ContactsRepository
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    fun contactsUpdateLiveData() = contactsRepository.contactsUpdateLiveData
    fun contactsLoadLiveData() = contactsRepository.contactsLoadLiveData
    fun requestContacts(fromDatabase: Boolean, offset: Int, count: Int)
            = contactsRepository.requestContacts(fromDatabase, offset, count)
    fun contactsSearchUserByNickname(nickname: String) = contactsRepository.findUserByNickname(nickname)
}
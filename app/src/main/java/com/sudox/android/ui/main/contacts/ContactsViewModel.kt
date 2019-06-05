package com.sudox.android.ui.main.contacts

import android.Manifest
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.repositories.users.ContactsRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    var contactsLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    var contactsActionLiveData: MutableLiveData<ContactsAction> = MutableLiveData()
    var subscriptionContainer = SubscriptionsContainer()

    companion object {
        const val CONTACT_SYNC_PERMISSION_REQUEST = 1
    }

    fun start() {
        listenContacts()

        // Load data ...
        contactsRepository.loadContacts()
    }

    private fun listenContacts() = GlobalScope.launch(Dispatchers.IO) {
        for (contacts in subscriptionContainer
                .addSubscription(contactsRepository
                        .contactsChannel
                        .openSubscription())) {

            // Сортируем по имени (от А до Я, от A до Z и т.п.)
            contactsLiveData.postValue(ArrayList(contacts.sortedBy { it.name }))
        }
    }

    private fun requestSyncContacts() = GlobalScope.launch {
        try {
            contactsActionLiveData.postValue(ContactsAction.INIT_SYNC)
            contactsRepository.syncContacts().await()
            contactsActionLiveData.postValue(ContactsAction.STOP_SYNC)
        } catch (e: InternalRequestException) {
            if (e.errorCode == InternalErrors.CONTACT_BOOK_IS_EMPTY) {
                contactsActionLiveData.postValue(ContactsAction.SHOW_NO_CONTACTS_IN_BOOK_DIALOG)
            } else if (e.errorCode == InternalErrors.NO_SYNCED_CONTACTS) {
                // TODO: Handle this error ...
            }
        } catch (e: RequestException) {
            // TODO: Handle unknown error ...
        }
    }

    fun syncContacts(activity: Activity, fragment: androidx.fragment.app.Fragment, permissionGranted: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val grant = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS)

            // Есть право на доступ к контактам.
            if (grant == PackageManager.PERMISSION_GRANTED) {
                requestSyncContacts()
            } else {
                fragment.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_SYNC_PERMISSION_REQUEST)
            }
        }

        if (permissionGranted) {
            requestSyncContacts()
        }
    }
}
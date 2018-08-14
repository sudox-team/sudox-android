package com.sudox.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.android.common.repository.main.ContactsRepository
import com.sudox.protocol.ProtocolClient
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val contactsRepository: ContactsRepository,
                                        private val accountRepository: AccountRepository,
                                        private val authRepository: AuthRepository) : ViewModel() {

    val accountLiveData = MutableLiveData<SudoxAccount?>()
    var connectLiveData = MutableLiveData<Data<ConnectState>>()


    private var accountDisposable: Disposable = accountRepository.getAccount()
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer {
                if (it == null)
                    accountRepository.deleteData()
                accountLiveData.postValue(it)

            })

    // Connection controller
    private var connectionDisposable: Disposable = protocolClient
            .connectionSubject
            .subscribe {
                connectLiveData.postValue(Data(it))
            }

    fun initContactsListeners() {
        contactsRepository.initContactsListeners()
    }

    fun removeAllAccounts() = accountRepository.removeAccounts()

    fun sendToken(sudoxAccount: SudoxAccount?) = authRepository.sendToken(sudoxAccount?.token)

    fun disconnect() = protocolClient.disconnect()

    fun loadContacts() {
        contactsRepository.requestAllContacts()
    }

    override fun onCleared() {
        connectionDisposable.dispose()
        super.onCleared()
    }
}
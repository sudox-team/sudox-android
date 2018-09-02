package com.sudox.android.ui.splash

import android.arch.lifecycle.ViewModel
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository,
                                          private val authRepository: AuthRepository) : ViewModel() {

    val connectLiveData = protocolClient.connectionStateLiveData

    fun getAccount() = accountRepository.getAccount()

    fun removeAllData() = accountRepository.deleteData()

    fun connect() = protocolClient.connect()

    fun setSecret(sudoxAccount: SudoxAccount?) = authRepository.setSecret(sudoxAccount?.secret, sudoxAccount?.id)

    fun disconnect() = protocolClient.disconnect()
}

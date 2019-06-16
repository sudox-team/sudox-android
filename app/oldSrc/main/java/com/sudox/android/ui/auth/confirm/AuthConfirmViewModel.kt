package com.sudox.android.ui.auth.confirm

import androidx.lifecycle.ViewModel
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.android.ui.auth.confirm.enums.AuthConfirmAction
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthConfirmViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Шина для уведомления View об нужных для выполнения ему действий
     * **/
    val authConfirmActionLiveData = SingleLiveEvent<AuthConfirmAction>()
    val authConfirmErrorsLiveData = SingleLiveEvent<Int>()

    /**
     * Метод, запрашивающий проверку кода на сервере ...
     *
     * Важно! Вызывать только если статус регистрации == 0
     * **/
    fun checkCode(phoneNumber: String, code: String, hash: String) = GlobalScope.launch(Dispatchers.IO) {
        authConfirmActionLiveData.postValue(AuthConfirmAction.FREEZE)

        try {
            if (authRepository.checkCode(phoneNumber, code, hash).await()) {
                authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_REGISTER_FRAGMENT)
            } else {
                authConfirmActionLiveData.postValue(AuthConfirmAction.UNFREEZE)
            }
        } catch (e: RequestException) {
            when (e.errorCode) {
                Errors.CODE_EXPIRED -> authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_PHONE_FRAGMENT_WITH_CODE_EXPIRED_ERROR)
                Errors.TOO_MANY_REQUESTS -> authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_PHONE_FRAGMENT_WITH_TOO_MANY_REQUESTS)
                else -> authConfirmErrorsLiveData.postValue(e.errorCode)
            }
        }
    }

    /**
     * Метод, запрашивающий проверку кода на сервере и выполняющий авторизацию ...
     *
     * Важно! Вызывать только если статус регистрации == 1
     * **/
    fun signIn(phoneNumber: String, code: String, hash: String) = GlobalScope.launch(Dispatchers.IO) {
        authConfirmActionLiveData.postValue(AuthConfirmAction.FREEZE)

        try {
            if (!authRepository.signIn(phoneNumber, code, hash).await()) {
                authConfirmActionLiveData.postValue(AuthConfirmAction.UNFREEZE)
            }
        } catch (e: RequestException) {
            when (e.errorCode) {
                Errors.CODE_EXPIRED -> authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_PHONE_FRAGMENT_WITH_CODE_EXPIRED_ERROR)
                Errors.TOO_MANY_REQUESTS -> authConfirmActionLiveData.postValue(AuthConfirmAction.SHOW_PHONE_FRAGMENT_WITH_TOO_MANY_REQUESTS)
                else -> authConfirmErrorsLiveData.postValue(e.errorCode)
            }
        }
    }
}
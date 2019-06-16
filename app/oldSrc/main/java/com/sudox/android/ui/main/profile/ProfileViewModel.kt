package com.sudox.android.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.repositories.users.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val usersRepository: UsersRepository) : ViewModel() {

    // LiveData с текущим пользователем (могут и обновления кстати прилетать)
    var currentUserSubscription: ReceiveChannel<User>? = null
    val userLiveData: MutableLiveData<User> = SingleLiveEvent()

    fun start() {
        listenUpdates()

        // Load data
//        usersRepository.loadCurrentUser(true)
    }

    private fun listenUpdates() = GlobalScope.launch(Dispatchers.IO) {
//        currentUserSubscription = usersRepository
//                .currentUserChannel
//                .openSubscription()
//
//        for (user in currentUserSubscription!!) {
//            userLiveData.postValue(user)
//        }
    }

    override fun onCleared() {
        currentUserSubscription?.cancel()
    }
}
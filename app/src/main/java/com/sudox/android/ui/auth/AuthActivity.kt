package com.sudox.android.ui.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.showSnackbar
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import com.sudox.android.ui.common.FreezableFragment
import com.sudox.android.ui.main.MainActivity
import com.sudox.protocol.models.enums.ConnectionState
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authViewModel: AuthViewModel
    var authSession: AuthSession? = null
    var authKey: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Get view model
        authViewModel = getViewModel(viewModelFactory)

        // Listen connection status ...
        authViewModel.connectionStateLiveData.observe(this, Observer {
            if (it == ConnectionState.CONNECTION_CLOSED || it == ConnectionState.CONNECTION_CLOSED) {
                showMessage(getString(R.string.lost_internet_connection))
                unfreezeCurrent()

                // Все равно код будет недействительным ...
                if (authSession != null) showAuthEmailFragment(authSession!!.email)
            } else if (it == ConnectionState.HANDSHAKE_SUCCEED) {
                showMessage(getString(R.string.connection_restored))
            }
        })

        // Слушаем сессию авторизации
        authViewModel.authSessionStateLiveData.observe(this, Observer {
            if (it?.status != -1) {
                // Сохраним сессию, потом пригодится :)
                authSession = it!!

                // Переключим фрагмент (новые данные он подхватит при подгрузке)
                showAuthConfirmFragment()
            }
        })

        // Слушаем сессию аккаунта (точнее статус её жизни)
        authViewModel.accountSessionLiveData.observe(this, Observer {
            if (it?.lived!!) showMainActivity()
        })

        showAuthEmailFragment(null, true)
    }

    private fun unfreezeCurrent() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)
                ?: return

        // Разморозим текущий фрагмент
        if (currentFragment is FreezableFragment) {
            currentFragment.unfreeze()
        }
    }

    fun showAuthEmailFragment(email: String? = null, isFirstStart: Boolean = false) {

        val authEmailFragment = AuthEmailFragment().apply {
            this.email = email
        }
        authEmailFragment.email = email

        // Build transaction for fragment change
        val transaction = supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentAuthContainer, authEmailFragment)

        if (isFirstStart) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        } else {
            transaction.setCustomAnimations(R.animator.fragment_slide_right_exit_anim, R.animator.fragment_slide_left_exit_anim)
        }

        transaction.commit()
    }

    private fun showAuthConfirmFragment() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_anim, R.animator.fragment_slide_right_anim)
                .replace(R.id.fragmentAuthContainer, AuthConfirmFragment())
                .commit()
    }

    fun showAuthRegisterFragment() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_anim, R.animator.fragment_slide_right_anim)
                .replace(R.id.fragmentAuthContainer, AuthRegisterFragment())
                .commit()
    }

    private fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun showMessage(message: String) = showSnackbar(this, fragmentAuthContainer, message, Snackbar.LENGTH_LONG)
}
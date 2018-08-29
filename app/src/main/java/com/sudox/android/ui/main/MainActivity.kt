package com.sudox.android.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androidadvance.topsnackbar.TSnackbar
import com.sudox.android.R
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.helpers.showTopSnackbar
import com.sudox.android.common.models.SecretData
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.settings.SettingsFragment
import com.sudox.android.ui.splash.SplashActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel

    private var isPause: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = getViewModel(viewModelFactory)
        mainViewModel.getAccount().observe(this, Observer { account ->
            mainViewModel.connectLiveData.observe(this, Observer {
                getConnectState(account, it)
            })
        })

        // init listeners
        mainViewModel.initContactsListeners()
        mainViewModel.initMessagesListener()
        loadContacts()

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_main_container, ContactsFragment())
                .commit()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.contacts_item -> goToContactsFragment()
                R.id.settings_item -> goToSettingsFragment()
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onPause() {
        isPause = true
        super.onPause()
    }

    override fun onResume() {
        isPause = false
        super.onResume()
    }

    private fun goToSettingsFragment() {
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_main_container, SettingsFragment())
                .commit()
    }

    private fun goToContactsFragment() {
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_main_container, ContactsFragment())
                .addToBackStack(null).commit()

    }

    fun goToChatActivity(intent: Intent) {
        startActivity(intent)
    }

    private fun getConnectState(account: SudoxAccount?, connectState: ConnectState) {
        if (connectState == ConnectState.RECONNECTED) {
            showMessage(getString(R.string.connection_restored))

            // Try to restore token
            mainViewModel
                    .sendSecret(account)
                    .observe(this, Observer(::getTokenState))
        } else if (connectState == ConnectState.DISCONNECTED) {
            showMessage(getString(R.string.lost_internet_connection))
        }
    }

    private fun getTokenState(secretData: SecretData) {
        if (secretData.tokenState == TokenState.WRONG) {
            mainViewModel.removeAllAccounts()
            mainViewModel.disconnect()
            showSplashActivity()
        } else if (secretData.tokenState == TokenState.CORRECT) {
            loadContacts()
        }
    }

    private fun showSplashActivity() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    private fun loadContacts() {
        if (mainViewModel.isConnected()) {
            mainViewModel.getAllContactsFromServer().observe(this, Observer {
                mainViewModel.getAllContactsFromDB()
            })
        } else {
            mainViewModel.getAllContactsFromDB()
        }
    }

    fun showMessage(message: String) {
        showTopSnackbar(this, fragment_main_container, message, TSnackbar.LENGTH_LONG)
    }

    override fun onBackPressed() {
        mainViewModel.disconnect()
        super.onBackPressed()
    }
}
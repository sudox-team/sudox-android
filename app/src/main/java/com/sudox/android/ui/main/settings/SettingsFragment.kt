package com.sudox.android.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.splash.SplashActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        settingsViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitButton.setOnClickListener {
            settingsViewModel.disconnect()
            settingsViewModel.removeAllAccounts()
            goToSplashActivity()
        }
    }

    private fun goToSplashActivity() {
        startActivity(Intent(activity as MainActivity, SplashActivity::class.java))
        (activity as MainActivity).finish()
    }
}
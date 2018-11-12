package com.sudox.android.ui.auth.common

import android.os.Bundle
import android.view.View
import com.sudox.android.ApplicationLoader
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.protocol.ProtocolClient
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Позволяет переносить фрагменты в ледниковый период по желанию царя Activity
 *
 * @author TheMax
 * **/
abstract class BaseAuthFragment : DaggerFragment() {

    @Inject
    lateinit var protocolClient: ProtocolClient

    abstract fun freeze()
    abstract fun unfreeze()
    abstract fun onConnectionRecovered()

    init {
        ApplicationLoader.component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Мы утилизируем фрагменты, поэтому при запуске нужна разморозка
        unfreeze()
    }

    override fun onResume() {
        super.onResume()

        if (!protocolClient.isValid()) {
            (activity as? AuthActivity)!!.showWaitForConnectStatus()
        }
    }
}
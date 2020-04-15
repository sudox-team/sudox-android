package ru.sudox.android.auth.ui.phone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.sudox.android.auth.ui.code.AuthCodeController
import ru.sudox.android.auth.ui.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.android.countries.COUNTRY_CHANGE_REQUEST_CODE
import ru.sudox.android.countries.COUNTRY_EXTRA_NAME
import ru.sudox.android.countries.CountrySelectController
import ru.sudox.api.getErrorText

class AuthPhoneController : ScrollableController() {

    private var authPhoneViewModel: AuthPhoneViewModel? = null
    private var screenVO: AuthPhoneScreenVO? = null

    init {
        appBarVO = AuthPhoneAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(container.context)
    }

    override fun bindView(view: View) {
        screenVO = AuthPhoneScreenVO().apply {
            (view as AuthScreenLayout).vo = this
        }

        authPhoneViewModel = getViewModel()
        authPhoneViewModel!!.errorsLiveData.observe(this, Observer {
            if (it != null) {
                screenVO!!.phoneEditTextLayout!!.errorText = getErrorText(activity!!, it)
            } else {
                screenVO!!.phoneEditTextLayout!!.errorText = null
            }
        })

        authPhoneViewModel!!.loadingLiveData.observe(this, Observer {
            screenVO!!.phoneEditTextLayout!!.isEnabled = !it
        })

        authPhoneViewModel!!.successLiveData.observe(this, Observer {
            navigationManager!!.showRootChild(AuthCodeController())
        })

        screenVO!!.phoneEditText!!.countrySelector.setOnClickListener {
            navigationManager!!.showRootChild(CountrySelectController().apply {
                targetController = this@AuthPhoneController
            })
        }
    }

    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)

        if (tag == AUTH_PHONE_NEXT_BUTTON_TAG) {
            authPhoneViewModel!!.createSession(screenVO!!.phoneEditText!!.phoneNumber!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == COUNTRY_CHANGE_REQUEST_CODE) {
            screenVO!!.phoneEditText!!.vo = data!!.getParcelableExtra(COUNTRY_EXTRA_NAME)
        }
    }
}
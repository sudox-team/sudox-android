package ru.sudox.android.auth.phone

import android.content.Context
import android.view.View
import ru.sudox.design.edittext.layout.EditTextLayout
import ru.sudox.android.auth.R
import ru.sudox.android.auth.vos.AuthScreenVO
import ru.sudox.android.countries.views.PhoneEditText

class AuthPhoneScreenVO : AuthScreenVO {

    var phoneEditTextLayout: EditTextLayout? = null
    var phoneEditText: PhoneEditText? = null

    override fun getTitle(context: Context): String {
        return context.getString(R.string.phone_verification)
    }

    override fun getDescription(context: Context): Triple<Int, Int, CharSequence> {
        return Triple(
                R.drawable.ic_smartphone,
                R.color.authscreenlayout_icon_tint_color,
                context.getString(R.string.enter_your_phone_and_we_will_send_a_confirmation_code_for_you)
        )
    }

    override fun getChildViews(context: Context): Array<View> {
        if (phoneEditTextLayout == null) {
            phoneEditText = PhoneEditText(context)
            phoneEditTextLayout = EditTextLayout(context).apply {
                childView = phoneEditText
            }

            phoneEditText!!.useDefaultCountry()
        }

        return arrayOf(phoneEditTextLayout as View)
    }
}
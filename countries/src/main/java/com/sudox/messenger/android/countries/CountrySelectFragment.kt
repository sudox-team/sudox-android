package com.sudox.messenger.android.countries

import android.content.Intent
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.core.fragments.ViewListFragment

const val COUNTRY_EXTRA_NAME = "country"
const val COUNTRY_CHANGE_REQUEST_CODE = 1

class CountrySelectFragment : ViewListFragment<CountrySelectAdapter>() {

    init {
        appBarVO = CountrySelectAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): CountrySelectAdapter {
        return CountrySelectAdapter(context!!) {
            navigationManager!!.popBackstack()

            targetFragment!!.onActivityResult(COUNTRY_CHANGE_REQUEST_CODE, 0, Intent().apply {
                putExtra(COUNTRY_EXTRA_NAME, it)
            })
        }
    }
}
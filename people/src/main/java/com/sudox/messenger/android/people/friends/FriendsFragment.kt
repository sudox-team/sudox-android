package com.sudox.messenger.android.people.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.viewPager.ViewPagerFragment
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.people.R

class FriendsFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onButtonClicked(tag: Int) {
    }

    override fun onPageSelected(position: Int) {
        val coreActivity = activity as CoreActivity

        coreActivity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getPageTitle(): CharSequence? {
        return getString(R.string.friends)
    }
}
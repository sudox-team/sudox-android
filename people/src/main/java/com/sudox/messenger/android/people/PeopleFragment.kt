package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.tabLayout.TabLayout
import com.sudox.messenger.android.core.viewPager.ViewPagerAdapter
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.people.activity.ActivityFragment
import com.sudox.messenger.android.people.friends.FriendsFragment
import kotlinx.android.synthetic.main.fragment_people.peopleViewPager

class PeopleFragment : CoreFragment() {

    private var tabLayout: TabLayout? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter = ViewPagerAdapter(context!!, activity as CoreActivity, peopleViewPager, childFragmentManager, arrayOf(
                ActivityFragment(),
                FriendsFragment()
        ))

        peopleViewPager.adapter = viewPagerAdapter
        peopleViewPager.addOnPageChangeListener(viewPagerAdapter!!)

        tabLayout = TabLayout(context!!).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )

            setViewPager(peopleViewPager)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            val activity = activity as CoreActivity

            activity.getScreenManager().reset()
            activity.getApplicationBarManager().let {
                it.reset(true)
                it.setContentView(tabLayout)
            }

            viewPagerAdapter!!.selectCurrentFragment()
        }
    }
}
package com.sudox.messenger.android.activity.vos

import android.content.Context
import android.view.View
import com.sudox.design.appbar.AppBarVO
import com.sudox.design.appbar.NOT_USED_PARAMETER
import com.sudox.messenger.android.activity.R

const val NOTIFICATION_BUTTON_TAG = 2
const val SEARCH_BUTTON_TAG = 3

class ActivityTabAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(NOTIFICATION_BUTTON_TAG, R.drawable.ic_notifications_none, NOT_USED_PARAMETER))
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER))
    }

    override fun getViewAtLeft(context: Context): View? {
        return null
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return R.string.people
    }
}
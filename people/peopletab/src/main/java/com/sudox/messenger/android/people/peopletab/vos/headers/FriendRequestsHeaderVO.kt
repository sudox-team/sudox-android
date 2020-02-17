package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithoutIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R

class FriendRequestsHeaderVO(
        override var isItemsHidden: Boolean = false,
        override var isContentLoading: Boolean = false
) : ViewListHeaderVO {

    override var selectedFunctionButtonToggleTags: HashMap<Int, Int>? = null
    override var selectedToggleTag = 0

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(PopupItemWithoutIconVO(0, context.getString(R.string.friend_requests), selectedToggleTag == 0))
    }

    override fun getFunctionButtonIcon(context: Context): Drawable? {
        return null
    }

    override fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>? {
        return null
    }

    override fun canHideItems(): Boolean {
        return true
    }

    override fun canSortItems(): Boolean {
        return false
    }
}
package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG
import com.sudox.messenger.android.people.peopletab.vos.SubscriptionVO
import com.sudox.messenger.android.people.peopletab.vos.headers.FAVORITE_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.POPULAR_OPTION_TAG

class SubscriptionsSortingCallback(
        viewListAdapter: ViewListAdapter<*>,
        var sortingType: Int
) : ViewListCallback<SubscriptionVO>(viewListAdapter, ADDED_FRIENDS_AND_SUBSCRIPTIONS_TAG) {

    override fun compare(first: SubscriptionVO, second: SubscriptionVO): Int {
        return if (sortingType == FAVORITE_OPTION_TAG) {
            -first.favorite.compareTo(second.favorite)
        } else if (sortingType == POPULAR_OPTION_TAG) {
            -first.popular.compareTo(second.popular)
        } else {
            first.userName.compareTo(second.userName)
        }
    }
}
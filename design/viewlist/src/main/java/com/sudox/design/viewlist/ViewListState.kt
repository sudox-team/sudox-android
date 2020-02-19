package com.sudox.design.viewlist

import android.os.Parcel
import android.os.Parcelable
import com.sudox.design.saveableview.SaveableViewState
import com.sudox.design.viewlist.vos.ViewListHeaderVO

class ViewListState : SaveableViewState<ViewList> {

    var headersVOs: Array<ViewListHeaderVO>? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        headersVOs = source.createTypedArray(ViewListHeaderVO.CREATOR)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeTypedArray(headersVOs, 0)
    }

    override fun readFromView(view: ViewList) {
        headersVOs = (view.adapter as? ViewListAdapter<*>)?.headersVOs
    }

    override fun writeToView(view: ViewList) {
        (view.adapter as? ViewListAdapter<*>)?.headersVOs = headersVOs
    }
}
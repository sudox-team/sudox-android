package com.sudox.android.ui.views.toolbar.expanded

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import javax.inject.Inject

class ErrorExpandedView : ExpandedView {

    @Inject
    lateinit var protocolClient: ProtocolClient

    // Observer for connection state data
    private var observer: Observer<ConnectionState> = Observer {

    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        turnBlackOverlay = false

        // Inflate view
        inflate(context, R.layout.error_text_view, this)

        // Inject all dependencies
        ApplicationLoader.component.inject(this)
    }

    override fun clear() {
        // Ignore
    }
}
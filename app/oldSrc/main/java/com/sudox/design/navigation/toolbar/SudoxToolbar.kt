package com.sudox.design.navigation.toolbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.design.helpers.FontsHelper.Companion.SANS_SERIF_LIGHT
import com.sudox.design.widgets.PrecomputedTextView
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject

class SudoxToolbar : Toolbar {

    // Mark for better performance
    private val navigationButtonSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56F, resources.displayMetrics).toInt()
    private val featureButtonHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42F, resources.displayMetrics).toInt()
    private var titleTextView: PrecomputedTextView? = null
    private var actionMenuView: ActionMenuView? = null
    private var navigationButtonView: ImageButton? = null
    private var featureTextButton: PrecomputedTextView? = null
    private var featureButtonText: String? = null
    private var normalTitleText: String? = null
    private var connectionStateSubscription: ReceiveChannel<ConnectionState>? = null
    private val selectableItemBackgroundRes by lazy {
        TypedValue().apply {
            context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
        }
    }

    private val maxButtonHeight by lazy {
        Toolbar::class.java
                .getDeclaredField("mMaxButtonHeight")
                .apply { isAccessible = true }
                .get(this)
    }

    private val toolbarMeasuredHeight by lazy {
        View.MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56F, resources.displayMetrics).toInt(),
                View.MeasureSpec.EXACTLY)
    }

    @Inject
    @JvmField
    var protocolClient: ProtocolClient? = null

    companion object {
        @JvmStatic
        val NAV_BUTTON_VIEW_FIELD by lazy {
            Toolbar::class.java
                    .getDeclaredField("mNavButtonView")
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val ADD_SYSTEM_VIEW_METHOD by lazy {
            Toolbar::class.java
                    .getDeclaredMethod("addSystemView", View::class.java, Boolean::class.java)
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val ENSURE_MENU_VIEW_METHOD by lazy {
            Toolbar::class.java
                    .getDeclaredMethod("ensureMenuView")
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val MENU_VIEW_FIELD by lazy {
            Toolbar::class.java
                    .getDeclaredField("mMenuView")
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val ACTION_MENU_ITEM_ICON_FIELD by lazy {
            ActionMenuItemView::class.java
                    .getDeclaredField("mIcon")
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val TITLE_TEXT_VIEW_FIELD by lazy {
            Toolbar::class.java
                    .getDeclaredField("mTitleTextView")
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val SHOULD_LAYOUT_FIELD by lazy {
            Toolbar::class.java
                    .getDeclaredMethod("shouldLayout", View::class.java)
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val MEASURE_CHILD_CONTRAINED by lazy {
            Toolbar::class.java
                    .getDeclaredMethod("measureChildConstrained", View::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java)
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val TEMP_MARGINS_FIELD by lazy {
            Toolbar::class.java
                    .getDeclaredField("mTempMargins")
                    .apply { isAccessible = true }
        }

        @JvmStatic
        val LAYOUT_CHILD_RIGHT_METHOD by lazy {
            Toolbar::class.java
                    .getDeclaredMethod("layoutChildRight", View::class.java, Int::class.java, IntArray::class.java, Int::class.java)
                    .apply { isAccessible = true }
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) readAttrs(attrs)

        // Initialization
        resetInsets()

        // Configuring toolbar
        configureBasic()
        configureNavigationButton()
        configureMenu()
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SudoxToolbar)

        // Read params
        featureButtonText = array.getString(R.styleable.SudoxToolbar_featureButtonText)

        // Recycle array & clean memory
        array.recycle()
    }

    override fun setNavigationOnClickListener(listener: OnClickListener) {
        super.setNavigationOnClickListener(listener)

        if (navigationButtonView == null) configureNavigationButton()
        if (navigationButtonView!!.visibility != View.VISIBLE) {
            navigationButtonView!!.visibility = View.VISIBLE
        }
    }

    private fun configureNavigationButton() {
        if (navigationButtonView == null) {
            setNavigationIcon(R.drawable.ic_arrow_back)
        }

        navigationButtonView = NAV_BUTTON_VIEW_FIELD.get(this) as? ImageButton
        navigationButtonView?.visibility = View.GONE
    }

    fun setFeatureText(resId: Int) {
        setFeatureText(context.getString(resId))
    }

    fun setFeatureEnabled(enabled: Boolean) {
        featureTextButton!!.isEnabled = enabled
    }

    fun setFeatureText(text: String) {
        featureButtonText = text

        if (featureTextButton == null) {
            configureFeatureButton()
        }

        if (!TextUtils.isEmpty(text)) {
            if (featureTextButton!!.visibility != View.VISIBLE) {
                featureTextButton!!.visibility = View.VISIBLE
            }

            featureTextButton!!.installText(text)
        } else if (featureTextButton!!.visibility != View.GONE) {
            featureTextButton!!.visibility = View.GONE
        }
    }

    private fun configureFeatureButton() {
        featureTextButton = PrecomputedTextView(context)
                .apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                    setTextColor(Color.WHITE)
                    layoutParams = generateDefaultLayoutParams()
                            .apply {
                                gravity = GravityCompat.END or Gravity.CENTER_VERTICAL
                                height = toolbarMeasuredHeight
                            }

                    gravity = Gravity.CENTER_VERTICAL
                    setBackgroundResource(selectableItemBackgroundRes.resourceId)
                    isClickable = true
                    isFocusable = true
                }

        // Draw text button
        featureTextButton!!.installText(featureButtonText!!)
        addSystemView(featureTextButton!!, false)
    }

    private fun addSystemView(view: View, allowHide: Boolean) {
        ADD_SYSTEM_VIEW_METHOD.invoke(this, view, allowHide)
    }

    private fun configureBasic() {
        setBackgroundResource(R.color.colorPrimary)

        // Remove shadow
        elevation = 0F
    }

    private fun configureMenu() {
        if (featureButtonText != null) return

        // Initialize the menu view
        ENSURE_MENU_VIEW_METHOD.invoke(this)
        actionMenuView = MENU_VIEW_FIELD.get(this) as ActionMenuView
    }

    private fun configurePaddings() {
        val menuItemsCount = actionMenuView?.childCount ?: 0
        val initialStartPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25F, resources.displayMetrics)
        val initialEndPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25F, resources.displayMetrics)
        val startPadding = calculateStartPadding(initialStartPadding)
        val endPadding = calculateEndPadding(menuItemsCount, initialEndPadding.toInt())

        setPaddingRelative(startPadding, 0, endPadding, 0)
    }

    private fun calculateStartPadding(initialStartPadding: Float): Int {
        if (navigationButtonView != null && navigationButtonView!!.visibility == View.VISIBLE) {
            return (initialStartPadding - navigationButtonView!!.drawable.intrinsicWidth).toInt()
        }

        return initialStartPadding.toInt()
    }

    private fun calculateEndPadding(menuItemsCount: Int, initialEndPadding: Int): Int {
        if (menuItemsCount > 0) {
            val lastItem = actionMenuView!!.getChildAt(menuItemsCount - 1) as ActionMenuItemView
            val lastItemEndPadding = if (lastItem.paddingEnd > 0) lastItem.paddingEnd else lastItem.paddingRight
            val iconWidth = (ACTION_MENU_ITEM_ICON_FIELD.get(lastItem) as Drawable).intrinsicWidth

            // Recalculate end padding
            return initialEndPadding - lastItemEndPadding - (iconWidth / 2)
        }

        return initialEndPadding
    }

    override fun inflateMenu(resId: Int) {
        menu.clear()

        // Super!
        super.inflateMenu(resId)
    }

    override fun setTitle(resId: Int) {
        title = context.getString(resId)
    }

    override fun setTitle(title: CharSequence?) {
        val isFirstInstalling = titleTextView == null

        if (titleTextView == null) {
            titleTextView = PrecomputedTextView(context).apply {
                includeFontPadding = false
                typeface = SANS_SERIF_LIGHT
                setPaddingRelative(0, 0, 0, 0)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 19F)
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
            }

            // Для сброса ожидания соединения ...
            normalTitleText = title.toString()

            // Отправляем на отрисовку единожды, дальше будем работать только с видимостью
            // Кстати, одна из оптимизаций гугловского тулбара
            TITLE_TEXT_VIEW_FIELD.set(this, titleTextView)
            ADD_SYSTEM_VIEW_METHOD.invoke(this, titleTextView, false)
        }

        // Update normal text
        normalTitleText = title.toString()

        if (!TextUtils.isEmpty(title)) {
            if (titleTextView!!.visibility != View.VISIBLE) {
                titleTextView!!.visibility = View.VISIBLE
            }

            if (protocolClient == null) ApplicationLoader.component.inject(this@SudoxToolbar)
            if (protocolClient!!.isValid()) {
                titleTextView!!.installText(title!!)
            }

            // Слушаем статус соединения
            if (isFirstInstalling) listenConnectionState()
        } else if (titleTextView!!.visibility != View.GONE) {
            titleTextView!!.visibility = View.GONE
        }
    }

    private fun listenConnectionState() {
        if (connectionStateSubscription != null) return
        if (!protocolClient!!.isValid())
            titleTextView!!.setText(resources.getString(R.string.wait_for_connect))

        // Listen ...
        GlobalScope.launch(Dispatchers.IO) {
            connectionStateSubscription = protocolClient!!
                    .connectionStateChannel
                    .openSubscription()

            for (state in connectionStateSubscription!!) {
                if (state == ConnectionState.CONNECTION_CLOSED) {
                    withContext(Dispatchers.Main) { titleTextView!!.setText(resources.getString(R.string.wait_for_connect)) }
                } else if (state == ConnectionState.HANDSHAKE_SUCCEED) {
                    withContext(Dispatchers.Main) { titleTextView!!.setText(normalTitleText) }
                }
            }
        }
    }

    fun setFeatureButtonOnClickListener(listener: View.OnClickListener) {
        featureTextButton?.setOnClickListener(listener)
    }

    private fun resetInsets() {
        setContentInsetsAbsolute(0, 0)
        setContentInsetsRelative(0, 0)
        contentInsetStartWithNavigation = 0
        contentInsetEndWithActions = 0
    }

    fun reset() {
        navigationButtonView?.visibility = View.GONE
        featureTextButton?.visibility = View.GONE
        titleTextView?.visibility = View.GONE
        actionMenuView?.removeAllViews()
        actionMenuView?.menu?.clear()
        featureTextButton?.isEnabled = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (featureTextButton != null
                && SHOULD_LAYOUT_FIELD.invoke(this, featureTextButton) as Boolean) {

            MEASURE_CHILD_CONTRAINED
                    .invoke(this, featureTextButton, widthMeasureSpec, 0, heightMeasureSpec, 0, maxButtonHeight)
        }

        // Set new size
        setMeasuredDimension(measuredWidthAndState, toolbarMeasuredHeight)

        // Configure paddings
        configurePaddings()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (actionMenuView != null) {
            if ((actionMenuView!!.layoutParams as Toolbar.LayoutParams).gravity != Gravity.CENTER_VERTICAL) {
                actionMenuView!!.layoutParams = (actionMenuView!!.layoutParams as Toolbar.LayoutParams)
                        .apply { gravity = Gravity.CENTER_VERTICAL }
            }
        }

        if (navigationButtonView != null) {
            navigationButtonView!!.minimumHeight = navigationButtonSize
            navigationButtonView!!.minimumWidth = navigationButtonSize

            if ((navigationButtonView!!.layoutParams as Toolbar.LayoutParams).gravity != Gravity.CENTER_VERTICAL) {
                navigationButtonView!!.layoutParams = (navigationButtonView!!.layoutParams as Toolbar.LayoutParams)
                        .apply { gravity = Gravity.CENTER_VERTICAL }
            }
        }

        if (SHOULD_LAYOUT_FIELD.invoke(this, featureTextButton) as Boolean) {
            val minHeight = ViewCompat.getMinimumHeight(this)
            val alignmentHeight = if (minHeight >= 0) Math.min(minHeight, b - t) else 0
            val collapsingMargins = TEMP_MARGINS_FIELD.get(this) as IntArray

            collapsingMargins[1] = 0
            collapsingMargins[0] = 0

            LAYOUT_CHILD_RIGHT_METHOD.invoke(this, featureTextButton, width - paddingRight, collapsingMargins, alignmentHeight)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Unsubscribe!
        connectionStateSubscription?.cancel()
    }
}
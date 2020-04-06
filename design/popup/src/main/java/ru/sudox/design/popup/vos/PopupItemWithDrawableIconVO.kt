package ru.sudox.design.popup.vos

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import ru.sudox.design.popup.views.PopupItemView

/**
 * ViewObject для элемента списка Popup-диалога с иконкой в виде Drawable
 */
class PopupItemWithDrawableIconVO : PopupItemVO<ImageView> {

    private var iconDrawable: Drawable? = null
    private var iconDrawableRes = 0

    override var tag: Int = 0
    override var isActive: Boolean = false
    override val title: String

    /**
     * Создает ViewObject с уже созданным заранее Drawable
     * Информацию по другим полям смотрите в классе PopupItemVO
     *
     * @param iconDrawable Drawable иконки
     */
    constructor(tag: Int, title: String, iconDrawable: Drawable, isActive: Boolean) {
        this.tag = tag
        this.title = title
        this.isActive = isActive
        this.iconDrawable = iconDrawable
    }

    /**
     * Создает ViewObject с Drawable, который нужно достать из ресурсов
     * Информацию по другим полям смотрите в классе PopupItemVO
     *
     * @param iconDrawableRes ID Drawable с иконкой
     */
    constructor(tag: Int, title: String, iconDrawableRes: Int, isActive: Boolean) {
        this.tag = tag
        this.title = title
        this.isActive = isActive
        this.iconDrawableRes = iconDrawableRes
    }

    override fun getIconView(context: Context): ImageView? {
        return ImageView(context)
    }

    override fun <T : View> configureIconView(item: PopupItemView, view: T) {
        if (view is ImageView) {
            view.imageTintList = ColorStateList.valueOf(if (isActive) {
                item.activeTitleColor
            } else {
                item.inactiveTitleColor
            })

            if (iconDrawable != null) {
                view.setImageDrawable(iconDrawable)
            } else {
                view.setImageResource(iconDrawableRes)
            }
        }
    }

    override fun <T : View> detachIconView(view: T) {
    }
}
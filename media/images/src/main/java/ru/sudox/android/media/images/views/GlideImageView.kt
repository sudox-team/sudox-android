package ru.sudox.android.media.images.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.R
import ru.sudox.android.media.images.entries.GlideImageRequest
import ru.sudox.design.roundedview.RoundedImageView

@Suppress("unused")
const val NOT_SHOWING_IMAGE_ID = -1L

/**
 * ImageView с возможность загрузки изображения по ID с сервера
 */
open class GlideImageView : RoundedImageView {

    private var placeholderDrawable: Drawable? = null
    private var crossFadeDuration = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.glideImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.GlideImageView, defStyleAttr, 0).use {
            placeholderDrawable = it.getDrawableOrThrow(R.styleable.GlideImageView_placeholderDrawable)
            crossFadeDuration = it.getIntegerOrThrow(R.styleable.GlideImageView_crossFadeDuration)
        }
    }

    /**
     * Загружает изображение по ID
     * Дополнительно выполняет нужные преобразования.
     *
     * @param id ID изображения.
     * @param glide Менеджер запросов Glide
     * @param width Ширина картинки
     * @param height Высота картинки
     */
    fun loadImage(id: Long, glide: GlideRequests, width: Int = 0, height: Int = 0) {
        cancelLoading(glide)

        var request = glide.load(GlideImageRequest(id))

        if (width != 0 && height != 0) {
            request = request.override(width, height)
        }

        request.placeholder(this.drawable ?: placeholderDrawable)
                .transition(withCrossFade(crossFadeDuration))
                .centerCrop()
                .into(this)
    }

    /**
     * Загружает Drawable как изображение
     * Дополнительно выполняет нужные преобразования.
     *
     * @param glide Менеджер запросов Glide
     * @param drawable Drawable для загрузки
     * @param width Ширина картинки
     * @param height Высота картинки
     */
    fun loadDrawable(glide: GlideRequests, drawable: Drawable, width: Int = 0, height: Int = 0) {
        cancelLoading(glide)

        var request = glide.load(drawable)

        if (width != 0 && height != 0) {
            request = request.override(width, height)
        }

        request.placeholder(this.drawable ?: placeholderDrawable)
                .transition(withCrossFade(crossFadeDuration))
                .centerCrop()
                .into(this)
    }

    /**
     * Приостанавливает загрузку текущего изображения
     *
     * @param glide Менеджер запросов Glide
     */
    fun cancelLoading(glide: GlideRequests) {
        glide.clear(this)
    }
}
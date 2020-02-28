package com.sudox.messenger.android.images.views

import android.util.Log
import com.sudox.design.imageview.ImageView
import com.sudox.messenger.android.images.providers.ImagesProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi

const val IMAGE_NOT_SHOWING_ID = -1L

/**
 * Абстракция ImageView с динамически-загружаемой картинкой.
 */
@ObsoleteCoroutinesApi
interface LoadableImageView {

    var showingImageId: Long
    var loadingJob: Job?

    /**
     * Загружает изображение с хранилища.
     *
     * @param id ID изображения, которое нужно загрузить с хранилища. Если равен IMAGE_NOT_SHOWING_ID,
     * то будет установлено изображение по-умолчанию.
     */
    fun loadImage(id: Long) {
        showingImageId = id
        loadingJob?.cancel()
        loadingJob = null

        if (id != IMAGE_NOT_SHOWING_ID) {
            loadingJob = ImagesProvider.loadImage(this, id)
        } else {
            getInstance().setBitmap(null, false)
        }
    }

    fun getInstance(): ImageView
}
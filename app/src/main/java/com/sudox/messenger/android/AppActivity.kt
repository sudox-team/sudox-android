package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.sudox.api.SudoxApi
import com.sudox.design.appbar.vos.AppBarLayoutVO
import com.sudox.design.appbar.vos.AppBarVO
import com.sudox.messenger.android.AppLoader.Companion.loaderComponent
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.inject.CoreActivityModule
import com.sudox.messenger.android.core.inject.CoreLoaderComponent
import com.sudox.messenger.android.inject.ActivityComponent
import com.sudox.messenger.android.layouts.AppLayout
import com.sudox.messenger.android.managers.AppNavigationManager
import com.sudox.messenger.android.managers.AppScreenManager
import javax.inject.Inject

/**
 * Главная Activity данного приложения.
 *
 * Отвечает за:
 * 1) Скрытие Splash-icon'а (да, он реализован с помощью windowBackground)
 * 2) Инициализацию основной View (т.е. AppLayout)
 * 3) Восстановление состояния
 * 4) Поставку Core-компонента Dagger.
 */
class AppActivity : AppCompatActivity(), CoreActivity {

    private var navigationManager: AppNavigationManager? = null
    private var activityComponent: ActivityComponent? = null
    private var appLayout: AppLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        appLayout = AppLayout(this).apply {
            // Почему-то фрагменты не восстанавливаются если восстанавливать ID FrameLayout'а в View.onRestoreState()
            init(savedInstanceState)

            navigationManager = AppNavigationManager(
                    supportFragmentManager,
                    contentLayout.frameLayout.id,
                    bottomNavigationView
            )

            setContentView(this)
        }

        activityComponent = loaderComponent!!.activityComponent(CoreActivityModule(navigationManager!!, AppScreenManager(this))).apply {
            inject(this@AppActivity)
        }

        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            navigationManager!!.restoreState(savedInstanceState)
        } else {
            navigationManager!!.configureNavigationBar()
            navigationManager!!.showAuthPart()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!navigationManager!!.popBackstack()) {
                super.onBackPressed()
            }

            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            appLayout!!.saveIds(this)
            navigationManager!!.saveState(this)
        })
    }

    override fun setAppBarViewObject(appBarVO: AppBarVO?, callback: ((Int) -> (Unit))?) {
        appLayout!!.contentLayout.appBarLayout.appBar!!.let {
            it.callback = callback
            it.vo = appBarVO
        }
    }

    override fun setAppBarLayoutViewObject(appBarLayoutVO: AppBarLayoutVO?) {
        appLayout!!.contentLayout.appBarLayout.vo = appBarLayoutVO
    }

    override fun onDestroy() {
        loaderComponent = null
        super.onDestroy()
    }

    override fun getLoaderComponent(): CoreLoaderComponent {
        return loaderComponent!!
    }

    override fun getActivityComponent(): com.sudox.messenger.android.core.inject.CoreActivityComponent {
        return activityComponent!!
    }

    override fun onBackPressed() {
        // Перенесен в onKeyDown()
    }
}
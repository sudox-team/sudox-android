package ru.sudox.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.android.AppLoader.Companion.loaderComponent
import ru.sudox.android.core.CoreActivity
import ru.sudox.android.core.inject.CoreActivityModule
import ru.sudox.android.core.inject.CoreLoaderComponent
import ru.sudox.android.inject.ActivityComponent
import ru.sudox.android.layouts.AppLayout
import ru.sudox.android.managers.AppNavigationManager
import ru.sudox.android.managers.AppScreenManager
import ru.sudox.android.vos.ConnectAppBarVO
import ru.sudox.api.common.SudoxApi
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

    private var apiStatusDisposable: Disposable? = null
    private var navigationManager: AppNavigationManager? = null
    private var activityComponent: ActivityComponent? = null
    private var appLayout: AppLayout? = null

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

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

        activityComponent = loaderComponent!!
                .activityComponent(CoreActivityModule(navigationManager!!, AppScreenManager(this)))
                .apply {
                    inject(this@AppActivity)
                }

        apiStatusDisposable = sudoxApi!!
                .statusSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    appLayout!!.contentLayout.appBarLayout.appBar!!.let {
                        setAppBarViewObject(if (it.vo is ConnectAppBarVO) {
                            (it.vo as ConnectAppBarVO).originalAppBarVO
                        } else {
                            it.vo
                        }, it.callback)
                    }
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

            if (appBarVO != null) {
                if (it.vo == null) {
                    it.vo = ConnectAppBarVO(appBarVO)
                } else if (it.vo is ConnectAppBarVO) {
                    (it.vo as ConnectAppBarVO).originalAppBarVO = appBarVO
                    it.vo = it.vo // Updating ...
                }
            } else {
                it.vo = null
            }
        }
    }

    override fun setAppBarLayoutViewObject(appBarLayoutVO: AppBarLayoutVO?) {
        appLayout!!.contentLayout.appBarLayout.vo = appBarLayoutVO
    }

    override fun onDestroy() {
        apiStatusDisposable?.dispose()
        activityComponent = null
        super.onDestroy()
    }

    override fun getLoaderComponent(): CoreLoaderComponent {
        return loaderComponent!!
    }

    override fun getActivityComponent(): ru.sudox.android.core.inject.CoreActivityComponent {
        return activityComponent!!
    }

    override fun onBackPressed() {
        // Перенесен в onKeyDown()
    }
}
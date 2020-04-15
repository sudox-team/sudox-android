package ru.sudox.android.core.controllers.tabs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import ru.sudox.android.core.CoreController

@Suppress("LeakingThis")
abstract class TabsRootController : CoreController() {

    internal val loadedControllers = arrayOfNulls<CoreController>(getControllersCount())

    @Suppress("LeakingThis")
    private val pagerAdapter = object : RouterPagerAdapter(this) {
        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                var controller = loadedControllers[position]

                if (controller == null) {
                    controller = createController(position)
                    loadedControllers[position] = controller
                }

                router.setRoot(RouterTransaction.with(controller))
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getControllerTitle(position)
        }

        override fun getCount(): Int {
            return getControllersCount()
        }
    }

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        if (appBarLayoutVO !is TabsAppBarLayoutVO) {
            appBarLayoutVO = TabsAppBarLayoutVO(appBarLayoutVO)
        }

        return ViewPager(activity!!).apply {
            adapter = pagerAdapter
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        (appBarLayoutVO as TabsAppBarLayoutVO)
                .tabLayout!!
                .setupWithViewPager(view as ViewPager)
    }

    abstract fun getControllersCount(): Int
    abstract fun getControllerTitle(position: Int): String
    abstract fun createController(position: Int): CoreController
}
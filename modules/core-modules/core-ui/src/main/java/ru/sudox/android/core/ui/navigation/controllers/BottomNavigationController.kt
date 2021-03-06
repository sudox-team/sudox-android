package ru.sudox.android.core.ui.navigation.controllers

import androidx.annotation.IdRes
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.core.ui.navigation.popBackstack
import java.util.*

/**
 * Контроллер навигации BottomNavigationView
 *
 * @param containerId ID контейнера
 * @param fragmentManager Менеджер фрагментов
 * @param createFragment Функция для создания фрагмента.
 */
class BottomNavigationController(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager,
    private val createFragment: (Int) -> (Fragment)
) {

    private var isCallbackBlocked = false
    private var bottomNavigationView: BottomNavigationView? = null

    @VisibleForTesting
    val backstack = Stack<Int>()

    /**
     * Связывает BottomNavigationView и контроллер
     * Запускает стартовый фрагмент если ни один фрагмент не запущен.
     *
     * @param bottomNavigationView BottomNavigationView для связки.
     */
    fun setup(bottomNavigationView: BottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView

        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager
                .beginTransaction()
                .add(containerId, createFragment(bottomNavigationView.selectedItemId), bottomNavigationView.selectedItemId.toString())
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (isCallbackBlocked) {
                return@setOnNavigationItemSelectedListener true
            }

            val previousItemId = bottomNavigationView.selectedItemId
            val selectedItemId = it.itemId

            if (previousItemId == selectedItemId) {
                // TODO: State resetting
                return@setOnNavigationItemSelectedListener true
            }

            val transaction = fragmentManager.beginTransaction()
            var selectedFragment = fragmentManager.findFragmentByTag(selectedItemId.toString())

            if (backstack.size > 0) {
                val backstackIterator = backstack.iterator()
                var backstackIndex = 0

                while (backstackIterator.hasNext()) {
                    val current = backstackIterator.next()

                    if (backstackIndex++ > 0 && current == selectedItemId) {
                        backstackIterator.remove()
                        backstackIndex--
                    }
                }
            }

            if (backstack.lastOrNull() != previousItemId) {
                backstack.push(previousItemId)
            }

            if (selectedFragment == null) {
                selectedFragment = createFragment(selectedItemId)
                transaction.add(containerId, selectedFragment, selectedItemId.toString())
            } else {
                transaction.attach(selectedFragment)
            }

            transaction
                .detach(fragmentManager.findFragmentByTag(previousItemId.toString())!!)
                .commit()

            true
        }
    }

    /**
     * Обрабатывает Backstack
     *
     * @return True - если было выполнено действие, False -
     * если не было выполнено действие.
     */
    fun pop(): Boolean {
        if (backstack.isEmpty()) {
            return false
        }

        val currentItemId = bottomNavigationView!!.selectedItemId
        val currentFragment = fragmentManager.findFragmentByTag(currentItemId.toString())

        if (!popBackstack(currentFragment!!.childFragmentManager)) {
            val previousItemId = backstack.pop()
            val previousFragment = fragmentManager.findFragmentByTag(previousItemId.toString())
            val transaction = fragmentManager.beginTransaction()

            if (backstack.contains(currentItemId)) {
                transaction.detach(currentFragment)
            } else {
                transaction.remove(currentFragment)
            }

            transaction
                .attach(previousFragment!!)
                .commit()

            isCallbackBlocked = true
            bottomNavigationView!!.selectedItemId = previousItemId
            isCallbackBlocked = false
        }

        return true
    }
}
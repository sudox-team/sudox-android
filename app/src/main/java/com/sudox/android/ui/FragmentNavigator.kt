package com.sudox.android.ui

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.sudox.android.R
import com.sudox.design.helpers.hideKeyboard
import java.util.*

class FragmentNavigator(val activity: Activity,
                        val fragmentManager: FragmentManager,
                        val rootFragments: List<Fragment>,
                        val containerId: Int) {

    private var isFirstStart: Boolean = false
    private val fragmentsPath: LinkedList<Fragment> = LinkedList()

    init {
        startRootFragments()
    }

    private fun startRootFragments() {
        val transaction = fragmentManager.beginTransaction()

        // Добавляем в контейнер и скрываем ...
        rootFragments.forEach { transaction.add(containerId, it) }
        rootFragments.forEach { transaction.hide(it) }

        // Отрисовываем
        transaction.commitAllowingStateLoss()
    }

    // Показывает основной фрагмент, постоянно держит его в памяти для более быстрой отрисовки
    fun showRootFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()

        // Чистим Backstack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // Анимация
        if (isFirstStart) {
            isFirstStart = false

            // Просто Fade эффект
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        } else {
            transaction.setCustomAnimations(R.animator.animator_fragment_change, 0)
        }

        // Родительские фрагменты скрываются, дочерние - удаляются из ОЗУ
        fragmentManager
                .fragments
                .filter { it != fragment }
                .forEach {
                    if (rootFragments.contains(it)) {
                        transaction.hide(it)
                    } else {
                        transaction.remove(it)
                    }
                }

        // Отображаем нужный фрагмент
        transaction.show(fragment)
        transaction.commit()

        // Ставим начальную точку маршрута )
        fragmentsPath.clear()
        fragmentsPath.add(fragment)
    }

    // Показывает дочерний фрагмент
    fun showChildFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()

        // Анимация
        transaction.setCustomAnimations(R.animator.animator_fragment_change, 0)

        // Скрываем все фрагменты, кроме нужного (бывают и такие случаи) ...
        fragmentManager
                .fragments
                .filter { it != fragment }
                .forEach { transaction.hide(it) }

        // Отображаем нужный фрагмент
        transaction.add(containerId, fragment)
        transaction.commitNow()

        // Прокладываем путь
        fragmentsPath.add(fragment)
    }

    fun popBackstack(): Boolean {
        if (fragmentsPath.size <= 1) return false

        // Текущий фрагмент - последний в списке, значит он дочерний и его можно, а то и нужно убрать из ОЗУ и отображения
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(0, R.animator.animator_fragment_back)
                .remove(fragmentsPath[fragmentsPath.size - 1])
                .show(fragmentsPath[fragmentsPath.size - 2])
                .runOnCommit { hideKeyboard(activity, activity.currentFocus) }
                .commit()

        // Удалим последний фрагмент из ОЗУ.
        fragmentsPath.removeAt(fragmentsPath.size - 1)

        return true
    }
}
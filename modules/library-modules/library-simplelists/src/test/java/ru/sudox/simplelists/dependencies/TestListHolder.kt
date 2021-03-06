package ru.sudox.simplelists.dependencies

import android.widget.TextView
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.CHANGE_PAYLOADS
import ru.sudox.simplelists.model.BasicListItem

/**
 * Тестовый Holder
 * Забивает текст из модели в TextView
 *
 * @param view TextView, на котором будет отображена информация.
 */
class TestListHolder(
    private val view: TextView
) : BasicListHolder<TestListViewObject>(view) {

    override fun bind(item: BasicListItem<TestListViewObject>, changePayload: List<Any>?) {
        if (changePayload != null) {
            // Копируем, т.к. список флагов чистится после завершения onBindViewHolder()
            CHANGE_PAYLOADS[item] = ArrayList(changePayload)
        }

        view.text = item.viewObject!!.title
    }

    override fun cancelAnimations() {
        view.clearAnimation()
    }
}
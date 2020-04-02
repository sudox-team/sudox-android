package com.sudox.design.edittext.layout

interface EditTextLayoutChild {

    /**
     * Задает цвет обводке.
     *
     * @param layout EditTextLayout, который запросил смену цвета
     * @param width Толщина, которую нужно присвоить обводке
     * @param color Цвет, который нужно присвоить обводке
     */
    fun changeStrokeColor(layout: EditTextLayout, width: Int, color: Int)
}
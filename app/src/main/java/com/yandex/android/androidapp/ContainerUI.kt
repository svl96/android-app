package com.yandex.android.androidapp


interface ContainerUI {
    fun setActivityTitle(titleId: Int)

    fun closeKeyboard()

    fun updateData()

    fun updateDataCallback(items: Array<Note>)

    fun getNotesContainer() : NotesContainer

}
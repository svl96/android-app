package com.yandex.android.androidapp

interface AsyncNotesContainerUI {

    fun deleteNoteAsync(note: Note)

    fun updateNoteAsync(note: Note)

    fun addNoteAsync(note: Note)

    fun addNotesAsync(notes: Array<Note>)

    fun refreshDataAsync()
}
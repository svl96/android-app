package com.yandex.android.androidapp

interface NotesContainerUI {

    fun getAllNotes() : Array<Note>

    fun addNotes(notes: Array<Note>)

    fun getNotes() : Array<Note>

    fun deleteNote(note: Note) : Boolean

    fun updateNote(note: Note) : Boolean

    fun addNote(note: Note)

    fun refreshData()

}
package com.yandex.android.androidapp

interface NotesContainer {

    fun getNotes() : Array<Note>

    fun deleteNote(note: Note) : Boolean

    fun updateNote(note: Note) : Boolean

    fun addNote(note: Note)

}
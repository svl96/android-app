package com.yandex.android.androidapp

import android.content.Context
import java.util.*


class NotesContainer(private val databaseHelper: NotesDatabaseHelper) : NotesContainerUI{
    private var _notes : Array<Note> = arrayOf()

    private var startDate : Calendar? = null
    private var endDate : Calendar? = null
    private var filterBy : String = NotesDatabaseHelper.DEFAULT_FILTER_COLUMN
    private var sortBy : String = NotesDatabaseHelper.DEFAULT_SORT_COLUMN
    private var sortOrder : String = NotesDatabaseHelper.DEFAULT_SORT_ORDER


    override fun getAllNotes() : Array<Note> {
        return databaseHelper?.getAllNotes() ?: arrayOf()
    }

    override fun getNotes(): Array<Note> {
        return _notes
    }

    override fun deleteNote(note: Note): Boolean {
        val deleteRes = databaseHelper?.deleteNote(note)
        refreshData()

        return deleteRes != 0
    }

    override fun updateNote(note: Note): Boolean {
        val updateRes = databaseHelper.updateNote(note)
        refreshData()

        return  updateRes != 0
    }

    override fun addNotes(notes: Array<Note>) {
        databaseHelper?.importNotes(notes.asList())
        refreshData()
    }

    override fun addNote(note: Note) {
        databaseHelper?.addNotes(listOf(note))
        refreshData()
    }

    override fun refreshData() {
        _notes = databaseHelper.
                getFilterByDateNotes(
                        date = startDate,
                        sortColumn = sortBy,
                        filterColumn = filterBy,
                        sortOrder = sortOrder)
    }

    fun setSelectParams(startDate: Calendar?, endDate: Calendar?,
                        sortBy : String = NotesDatabaseHelper.DEFAULT_SORT_COLUMN,
                        filterBy: String = NotesDatabaseHelper.DEFAULT_FILTER_COLUMN,
                        sortOrder : String = NotesDatabaseHelper.DEFAULT_SORT_ORDER) {
        this.startDate = startDate
        this.endDate = endDate
        this.sortBy = sortBy
        this.filterBy = filterBy
        this.sortOrder = sortOrder
    }

}
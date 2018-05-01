package com.yandex.android.androidapp

import android.util.Log
import com.yandex.android.androidapp.fragments.DatabaseFragment
import java.util.*
import kotlin.collections.HashMap


class NotesContainer(private val databaseHelper: NotesDatabaseHelper,
                     private val databaseFragment: DatabaseFragment) : NotesContainerUI, AsyncNotesContainerUI {

    private var _notes : Array<Note> = arrayOf()
    private var offset = 20
    private var startDate : Calendar? = null
    private var endDate : Calendar? = null
    private var filterBy : String = NotesDatabaseHelper.DEFAULT_FILTER_COLUMN
    private var sortBy : String = NotesDatabaseHelper.DEFAULT_SORT_COLUMN
    private var sortOrder : String = NotesDatabaseHelper.DEFAULT_SORT_ORDER


    override fun deleteNoteAsync(note: Note) {
        databaseFragment.deleteDataAsync(databaseHelper, note)
    }

    override fun updateNoteAsync(note: Note) {
        databaseFragment.updateDataAsync(databaseHelper, note)
    }

    override fun addNoteAsync(note: Note) {
        addNotesAsync(arrayOf(note))
    }

    override fun addNotesAsync(notes: Array<Note>) {
        databaseFragment.addDataAsync(databaseHelper, notes)
    }

    override fun refreshDataAsync() {
        val params = getParams()
        databaseFragment.getDataAsync(databaseHelper, params)
    }

    fun loadNextPageAsync(offset: Int) {
        this.offset = offset
        refreshDataAsync()
    }

    fun setNotes(notes: Array<Note>) {
        _notes = notes
    }

    override fun getAllNotes() : Array<Note> {
        return databaseHelper.getAllNotes()
    }

    override fun getNotes(): Array<Note> {
        return _notes
    }

    override fun deleteNote(note: Note): Boolean {
        val deleteRes = databaseHelper.deleteNote(note)
        refreshData()

        return deleteRes != 0
    }

    override fun updateNote(note: Note): Boolean {
        val updateRes = databaseHelper.updateNote(note)
        refreshData()

        return  updateRes != 0
    }

    override fun addNotes(notes: Array<Note>) {
        databaseHelper.importNotes(notes.asList())
        refreshData()
    }

    override fun addNote(note: Note) {
        databaseHelper.addNotes(listOf(note))
        refreshData()
    }

    private fun getParams() : Map<String, String> {
        val params : HashMap<String, String> = HashMap()
        params[NotesDatabaseHelper.SORT_ORDER_PARAM_KEY] = sortOrder
        params[NotesDatabaseHelper.FILTER_COLUMN_PARAM_KEY] = filterBy
        params[NotesDatabaseHelper.SORT_COLUMN_PARAM_KEY] = sortBy
        params[NotesDatabaseHelper.LIMIT_KEY] = (offset).toString()
        if (startDate != null)
            params[NotesDatabaseHelper.START_DATE_PARAM_KEY] = Note.formatDate(startDate?.time)!!

        if (endDate != null)
            params[NotesDatabaseHelper.END_DATE_PARM_KEY] = Note.formatDate(endDate?.time)!!

        return params
    }

    override fun refreshData() {
        val params = getParams()
        _notes = databaseHelper.getFilteredNotes(params)
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
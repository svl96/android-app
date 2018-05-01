package com.yandex.android.androidapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.provider.BaseColumns
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesDatabaseHelper(context: Context?)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {

        // database
        private const val DATABASE_NAME: String = "notes.db"
        private const val VERSION_X: Int = 1
        private const val VERSION: Int = VERSION_X

        // tables
        private const val TABLE_NOTES: String = "notes"

        //  columns
        const val COLUMN_ID: String = BaseColumns._ID
        const val COLUMN_NOTE_ID : String = "note_id"
        const val COLUMN_TITLE: String = "title"
        const val COLUMN_DESCRIPTION: String = "description"
        const val COLUMN_COLOR: String = "color"
        const val COLUMN_CREATE_TIME: String = "create_time"
        const val COLUMN_EDIT_TIME: String = "edit_time"
        const val COLUMN_VIEW_TIME: String = "view_time"


        const val DEFAULT_SORT_COLUMN = COLUMN_EDIT_TIME
        const val DEFAULT_FILTER_COLUMN = COLUMN_EDIT_TIME
        const val DESCENT_SORT_ORDER = "DESC"
        const val ASCENT_SORT_ORDER = "ASC"
        const val DEFAULT_LIMIT = "100"

        const val DEFAULT_SORT_ORDER = DESCENT_SORT_ORDER

        // params keys
        const val SORT_COLUMN_PARAM_KEY = "sort_column"
        const val FILTER_COLUMN_PARAM_KEY = "filter_column"
        const val SORT_ORDER_PARAM_KEY = "sort_order"
        const val START_DATE_PARAM_KEY = "start_date"
        const val END_DATE_PARM_KEY = "end_date"
        const val LIMIT_KEY = "limit"

        // queries
        private const val SQL_CREATE_TABLE_NOTES = "CREATE TABLE $TABLE_NOTES " +
                "( $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NOTE_ID TEXT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$COLUMN_COLOR TEXT," +
                "$COLUMN_CREATE_TIME INTEGER," +
                "$COLUMN_EDIT_TIME INTEGER," +
                "$COLUMN_VIEW_TIME INTEGER )"

        private const val SQL_DROP_TABLE_NOTES = "DROP TABLE IF EXISTS $TABLE_NOTES"

        // formats
        private const val dateFormatString : String = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_NOTES)
        fillData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        onCreate(db)
    }

    fun importNotes(notes: List<Note>) {
        val db = writableDatabase
        db?.execSQL(SQL_DROP_TABLE_NOTES)
        db?.execSQL(SQL_CREATE_TABLE_NOTES)
        addNotes(notes)
    }

    private fun fillData(db: SQLiteDatabase?) {
        // добавление данных, возможно из assets
        addNote(db, Note("testid1", "Note 1", "Descr 1", Color.RED,
                Note.parseDate("2017-04-24T12:00:00.000+05:00")!!,
                Note.parseDate("2017-04-24T12:20:00.000+05:00")!!,
                Note.parseDate("2017-04-24T12:20:00.000+05:00")!!)
                )

        addNote(db,Note("testid2", "Note 2", "Descr 2", Color.BLUE,
                Note.parseDate("2017-04-26T15:00:00.000+05:00")!!,
                Note.parseDate("2017-04-26T15:20:00.000+05:00")!!,
                Note.parseDate("2017-04-26T15:20:00.000+05:00")!!)
        )
    }

    private fun getContentValues(note: Note) : ContentValues {
        val contentValues = ContentValues(7)
        contentValues.put(COLUMN_NOTE_ID, note.id)
        contentValues.put(COLUMN_TITLE, note.title)
        contentValues.put(COLUMN_DESCRIPTION, note.description)
        contentValues.put(COLUMN_COLOR, getHEXColor(note.color))
        contentValues.put(COLUMN_CREATE_TIME, note.timeCreate.time)
        contentValues.put(COLUMN_EDIT_TIME, note.timeEdit.time)
        contentValues.put(COLUMN_VIEW_TIME, note.timeView.time)

        return contentValues
    }

    fun updateNote(note: Note) : Int {
        val database = writableDatabase
        val contentValues = getContentValues(note)

        val selection = "$COLUMN_NOTE_ID = ?"
        val selectionArgs = arrayOf(note.id)

        database.use {db ->
            return db.update(TABLE_NOTES, contentValues, selection, selectionArgs)
        }
    }

    // region Add Notes

    fun addNotes(notes: List<Note>) {
        val database = writableDatabase
        try {
            database.beginTransaction()
            for (note in notes)
                addNote(database, note)
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
            database.close()
        }
    }


    private fun addNote(db: SQLiteDatabase?, note: Note) {
        val contentValues = getContentValues(note)
        if (db != null) {
            db.insert(TABLE_NOTES, null, contentValues)
        } else {
            Log.e("DatabaseError", "NullPointException")
        }
    }

    // endregion

    // region Delete Note

    fun deleteNote(note: Note) : Int{
        return deleteNoteById(note.id)
    }

    fun deleteNoteById(noteId: String) : Int {
        val database =  writableDatabase
        val selection = "$COLUMN_NOTE_ID=?"
        val selectionArgs = arrayOf(noteId)

        database.use { db ->
            return db.delete(TABLE_NOTES, selection, selectionArgs)
        }
    }

    fun deleteNoteByTitle(noteTitle: String) : Int {
        val database =  writableDatabase
        val selection = "$COLUMN_TITLE=?"
        val selectionArgs = arrayOf(noteTitle)

        database.use { db ->
            return db.delete(TABLE_NOTES, selection, selectionArgs)
        }
    }

    // endregion

    // region Select Notes

    fun getAllNotes(sortColumn : String = DEFAULT_SORT_COLUMN,
                    sortOrder : String = DEFAULT_SORT_ORDER ) : Array<Note> {
        return getFilterByDateRangeNotes(sortColumn = sortColumn, sortOrder = sortOrder,
                startDate = null, endDate = null, limit = null)
    }

    fun getFilteredNotes(params : Map<String, String> ) : Array<Note> {
        var startDate: Calendar? = null
        if (params.contains(START_DATE_PARAM_KEY)) {
            startDate = Calendar.getInstance()
            startDate.time = Note.parseDate(params[START_DATE_PARAM_KEY])
        }

        var endDate : Calendar? = null
        if (params.contains(END_DATE_PARM_KEY)) {
            endDate = Calendar.getInstance()
            endDate.time = Note.parseDate(params[END_DATE_PARM_KEY])
        }

        val sortColumn: String = params[SORT_COLUMN_PARAM_KEY] ?: DEFAULT_SORT_COLUMN
        val filterColumn : String = params[FILTER_COLUMN_PARAM_KEY] ?: DEFAULT_FILTER_COLUMN
        val sortOrder : String = params[SORT_ORDER_PARAM_KEY] ?: DEFAULT_SORT_ORDER
        val limit : String? = params[LIMIT_KEY]

        return getFilterByDateRangeNotes(startDate, endDate, sortColumn,
                filterColumn, sortOrder, limit)

    }

    fun getFilterByDateNotes( date : Calendar? = null,
                              sortColumn : String = DEFAULT_SORT_COLUMN,
                              filterColumn : String = DEFAULT_FILTER_COLUMN,
                              sortOrder : String = DEFAULT_SORT_ORDER) : Array<Note> {

        return getFilterByDateRangeNotes(date, date, sortColumn, filterColumn, sortOrder)
    }

    fun getFilterByDateRangeNotes(startDate : Calendar?, endDate : Calendar?,
                                  sortColumn : String = DEFAULT_SORT_COLUMN,
                                  filterColumn : String = DEFAULT_FILTER_COLUMN,
                                  sortOrder : String = DEFAULT_SORT_ORDER,
                                  limit : String? = null
    ) : Array<Note> {

        val notes = mutableListOf<Note>()

        val columns = arrayOf(COLUMN_ID, COLUMN_NOTE_ID, COLUMN_TITLE, COLUMN_DESCRIPTION,
                COLUMN_COLOR, COLUMN_CREATE_TIME, COLUMN_EDIT_TIME, COLUMN_VIEW_TIME)
        val sortArg = "$sortColumn $sortOrder"
        var selection : String? = null
        var selectionArgs : Array<String>? = null

        if (startDate != null && endDate != null) {
            selection = "$filterColumn > ? AND $filterColumn < ?"
            selectionArgs = getFormattedDateRange(startDate, endDate)
        }

        val db = readableDatabase
        var cursor : Cursor? = null

        try {
            cursor = db.query( TABLE_NOTES, columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortArg,
                    limit
            )
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(1)
                    val title = cursor.getString(2)
                    val description = cursor.getString(3)
                    val color = cursor.getString(4)
                    val timeCreate = cursor.getLong(5)
                    val timeEdit = cursor.getLong(6)
                    val timeView = cursor.getLong(7)
                    notes.add(Note(
                            id = id,
                            title = title,
                            description = description,
                            color = Color.parseColor(color),
                            timeCreate = Date(timeCreate),
                            timeEdit = Date(timeEdit),
                            timeView = Date(timeView)
                    ))
                } while (cursor.moveToNext())
            }
        } finally {
            if (cursor != null && !cursor.isClosed)
                cursor.close()
            db.close()
        }

        return notes.toTypedArray()
    }

    private fun getFormattedDateRange(startDate: Calendar, endDate : Calendar) : Array<String> {
        val outStartDate = truncateDateByDay(startDate)
        val outEndDate = truncateDateByDay(endDate)

        val timeZone = Calendar.getInstance().timeZone
        outStartDate.timeZone = timeZone
        outEndDate.timeZone = timeZone
        outEndDate.add(Calendar.DAY_OF_MONTH, 1)

        val startTime = outStartDate.time.time.toString()
        val endTime = outEndDate.time.time.toString()

        return arrayOf(startTime, endTime)
    }

    // endregion

    // region Extra Funcs

    private fun truncateDateByDay(date: Calendar) : Calendar {
        val outDate =  Calendar.getInstance()
        outDate.set(
                date[Calendar.YEAR],
                date[Calendar.MONTH],
                date[Calendar.DAY_OF_MONTH],
                0, 0, 0)

        return outDate
    }

    private fun getHEXColor(color: Int) : String{
        val red = (color shr 16) and 0xff
        val green = (color shr 8) and 0xff
        val blue = color and 0xff
        val hexColor = "#%02x%02x%02x".format(red, green, blue)
        return hexColor
    }

    // endregion

}
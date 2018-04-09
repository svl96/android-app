package com.yandex.android.androidapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.provider.BaseColumns
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

        const val DEFAULT_SORT_ORDER = DESCENT_SORT_ORDER

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
        addNote(db, "testid1", "Note 1", "Descr 1", "#ff0000",
                parseDate("2017-04-24T12:00:00.000+05:00"),
                parseDate("2017-04-24T12:20:00.000+05:00"),
                parseDate("2017-04-24T12:20:00.000+05:00")
                )

        addNote(db,"testid2", "Note 2", "Descr 2", "#00ff00",
                parseDate("2017-04-26T15:00:00.000+05:00"),
                parseDate("2017-04-26T15:20:00.000+05:00"),
                parseDate("2017-04-26T15:20:00.000+05:00")
        )
    }

    fun updateNote(note: Note) : Int {
        val database = writableDatabase
        val contentValues = ContentValues(7)
        contentValues.put(COLUMN_NOTE_ID, note.id)
        contentValues.put(COLUMN_TITLE, note.title)
        contentValues.put(COLUMN_DESCRIPTION, note.description)
        contentValues.put(COLUMN_COLOR, getHEXColor(note.color))
        contentValues.put(COLUMN_CREATE_TIME, note.timeCreate.time)
        contentValues.put(COLUMN_EDIT_TIME, note.timeEdit.time)
        contentValues.put(COLUMN_VIEW_TIME, note.timeView.time)

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

    private fun addNote(db: SQLiteDatabase?, noteId: String,
                        title: String, description: String, color: String,
                        timeCreate: Date, timeEdit: Date, timeView: Date
                        ) {
        val contentValues = ContentValues(7)
        contentValues.put(COLUMN_NOTE_ID, noteId)
        contentValues.put(COLUMN_TITLE, title)
        contentValues.put(COLUMN_DESCRIPTION, description)
        contentValues.put(COLUMN_COLOR, color)
        contentValues.put(COLUMN_CREATE_TIME, timeCreate.time)
        contentValues.put(COLUMN_EDIT_TIME, timeEdit.time)
        contentValues.put(COLUMN_VIEW_TIME, timeView.time)
        db?.insert(TABLE_NOTES, null, contentValues)
    }

    private fun addNote(db: SQLiteDatabase, note: Note) {
        addNote(db, note.id, note.title, note.description, getHEXColor(note.color),
                note.timeCreate, note.timeEdit, note.timeView)
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
                startDate = null, endDate = null)
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
                                  sortOrder : String = DEFAULT_SORT_ORDER) : Array<Note> {

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
                    sortArg
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

        val oldtimezone = outStartDate.timeZone

        val timeZone = Calendar.getInstance().timeZone
        outStartDate.timeZone = timeZone
        outEndDate.timeZone = timeZone
        outEndDate.add(Calendar.DAY_OF_MONTH, 1)

        val startYear = outStartDate[Calendar.YEAR]
        val startMonth = outStartDate[Calendar.MONTH]
        val startDay = outStartDate[Calendar.DAY_OF_MONTH]

        val EndYear = outEndDate[Calendar.YEAR]
        val EndMonth = outEndDate[Calendar.MONTH]
        val endDay = outEndDate[Calendar.DAY_OF_MONTH]



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

    private fun parseDate(string: String) : Date {
        val dateFormat = SimpleDateFormat(dateFormatString, Locale.getDefault())
        return dateFormat.parse(string)
    }

    // endregion

}
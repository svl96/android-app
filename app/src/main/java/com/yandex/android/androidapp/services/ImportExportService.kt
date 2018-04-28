package com.yandex.android.androidapp.services

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.yandex.android.androidapp.Note
import com.yandex.android.androidapp.NotesDatabaseHelper
import com.yandex.android.androidapp.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class ImportExportService : IntentService("ImportExportNotes") {

    companion object {
        const val EXTRA_IMPORT_EXPORT_MODE = "extra_mode"
        const val IMPORT_MODE = "import_mode"
        const val EXPORT_MODE = "export_mode"
    }

    private var databaseHelper : NotesDatabaseHelper? = null
    private val _tag = "ImportExportService"
    private val defaultFilename = "itemlist_new.ili"

    private var notifyManager : NotificationManager? = null
    private var notifyBuilder : NotificationCompat.Builder? = null

    private val _id = 513423


    override fun onHandleIntent(intent: Intent?) {
        val mode = intent?.getStringExtra(EXTRA_IMPORT_EXPORT_MODE)
        databaseHelper = NotesDatabaseHelper(this)

        notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyBuilder = NotificationCompat.Builder(this, "IMPORT_EXPORT_CHANEL_ID")

        when(mode) {
            IMPORT_MODE -> importNotes()
            EXPORT_MODE -> exportNotes()
        }

        notifyBuilder?.setContentText("Action complete")
                ?.setProgress(0,0,false)
        notifyManager?.notify(_id, notifyBuilder?.build())
    }

    // region Import Notes

    private fun importNotes() {
        notifyBuilder?.setContentTitle("Import Notes")
        notifyBuilder?.setContentText("Process of Importing Notes")
        notifyBuilder?.setSmallIcon(R.drawable.ic_import)
        notifyBuilder?.setProgress(0,0,true)
        notifyManager?.notify(_id, notifyBuilder?.build())

        val json = readFromFile(defaultFilename)
        if (json == "")
            return
        Thread.sleep(2000)
        val notes = parseJson(json)
        writeInDatabase(notes)
    }

    private fun writeInDatabase(notes : List<Note>?) {
        if (notes != null)
            databaseHelper?.importNotes(notes)
    }

    private fun readFromFile(filename: String) : String? {
        if (!isExternalStorageReadable()) {
            return null
        }
        val dir = Environment.getExternalStorageDirectory()
        Log.d(_tag, dir.absolutePath)
        val file = File(dir, filename)
        if (!file.exists()) {
            Log.d(_tag, "Empty")
            return null
        }

        val text = file.readText()
        Log.d(_tag, text)

        return if (text != "") text else null
    }

    // endregion

    // region Export Notes

    private fun exportNotes() {
        notifyBuilder?.setContentTitle("Import Notes")
        notifyBuilder?.setContentText("Process of Importing Notes")
        notifyBuilder?.setSmallIcon(R.drawable.ic_export)
        notifyBuilder?.setProgress(0,0,true)
        notifyManager?.notify(_id, notifyBuilder?.build())
        Thread.sleep(2000)
        val notes = readFromDatabase()
        val json = createJson(notes)
        writeInFile(defaultFilename, json)
    }

    private fun readFromDatabase() : Array<Note> {
        return arrayOf()
    }

    private fun writeInFile(filename: String, text: String) {
        if (!isExternalStorageWritable()) {
            return
        }
        val dir = Environment.getExternalStorageDirectory()
        Log.d(_tag, dir.absolutePath)
        val file = File(dir, filename)
        if (!file.exists())
            file.createNewFile()

        file.writeText(text)
    }

    // endregion

    // region Json Work

    private fun createJson(notes: Array<Note>) : String {
        val jsonArray = JSONArray()

        for (note in notes) {
            jsonArray.put(JSONObject(note.getJson()))
        }

        return jsonArray.toString(2)
    }

    private fun parseJson(json : String?) : List<Note>? {
        if (json == null)
            return null

        try {
            val listNotes = mutableListOf<Note>()
            val jsonArray = JSONArray(json)

            val size = jsonArray.length()
            for (i in 0..(size - 1)) {
                val jsonObject = jsonArray.getJSONObject(i)
                val uniqueId = UUID.randomUUID().toString()
                val note = Note.getNoteFromJson(uniqueId, jsonObject.toString())
                if (note != null)
                    listNotes.add(note)
            }

            return listNotes
        } catch (ex : JSONException) {
            Log.d(_tag, "JSON EXCEPTION")
            return null
        }
    }

    // endregion

    // region Check External Storage

    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(state)
    }

    /* Checks if external storage is available to at least read */
    private fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
    }

    // endregion


}
package com.yandex.android.androidapp.fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.yandex.android.androidapp.ContainerUI
import com.yandex.android.androidapp.Note
import com.yandex.android.androidapp.NotesContainer
import com.yandex.android.androidapp.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class ImportExportFragmet : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() : ImportExportFragmet {
            return ImportExportFragmet()
        }
    }

    private var _tag = "ImportExportFragment"
    private val defaultFilename = "itemlist.ili"

    private var containerUi: ContainerUI? = null
    private lateinit var notesContainer : NotesContainer

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")

        if (context is NotesContainer)
            notesContainer = context
        else
            throw IllegalStateException("Context should implement NotesContainer")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        containerUi?.setActivityTitle(R.string.import_export_title)
        val rootView = inflater?.inflate(R.layout.fragment_import_export, container, false)!!

        val exportButton = rootView.findViewById<Button>(R.id.export_button)
        val importButton = rootView.findViewById<Button>(R.id.import_button)

        importButton.setOnClickListener { importNotes() }

        exportButton.setOnClickListener { exportNotes() }


        return rootView
    }

    private fun exportNotes() {

        val notes = notesContainer.getAllNotes()
        val json = createJson(notes)
        writeInFile(defaultFilename, json)
    }

    private fun importNotes() {

        val json = readFromFile(defaultFilename)
        if (json == "")
            return

        val notes = parseJson(json)
        notesContainer.addNotes(notes)
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

    private fun readFromFile(filename: String) : String {
        if (!isExternalStorageReadable()) {
            return ""
        }
        val dir = Environment.getExternalStorageDirectory()
        Log.d(_tag, dir.absolutePath)
        val file = File(dir, filename)
        if (!file.exists()) {
            Log.d(_tag, "Empty")
            return ""
        }

        val text = file.readText()
        Log.d(_tag, text)

        return text
    }

    private fun createJson(notes: Array<Note>) : String {
        val jsonArray = JSONArray()

        for (note in notes) {
            jsonArray.put(JSONObject(note.getJson()))
        }

        return jsonArray.toString(2)
    }

    private fun parseJson(json : String) : Array<Note> {
        var notes = arrayOf<Note>()

        try {
            val listNotes = mutableListOf<Note>()
            val jsonArray = JSONArray(json)

            val size = jsonArray.length()
            for (i in 0..(size - 1)) {
                val jsonObject = jsonArray.getJSONObject(i)
                val uniqueId = UUID.randomUUID().toString()
                val note = Note.getNoteFromJson(uniqueId, jsonObject.toString())

                listNotes.add(note)
            }

            notes = listNotes.toTypedArray()
        } catch (ex : JSONException) {
            Log.d(_tag, "JSON EXCEPTION")
        }
        return notes
    }


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
}
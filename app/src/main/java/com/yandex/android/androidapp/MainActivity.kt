package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.icu.lang.UCharacterEnums
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.ListViewCompat
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*

const val EXTRA_NOTE : String = "com.yandex.android.EXTRA_NOTE"
const val EXTRA_EDIT_MODE : String = "com.yandex.android.EXTRA_EDIT_MODE"
const val EXTRA_NOTE_ID : String = "com.yandex.android.EXTRA_NOTE_ID"
const val GET_NOTE_REQUEST : Int = 1
const val EDIT_NOTE_REQUEST : Int = 2

class MainActivity : AppCompatActivity() {

    private var _notes : MutableList<Note> = mutableListOf()
    private var _note : Note? = null
    private var _notesAdapter : NotesAdapter? = null
    private var listView : ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListViewCompat>(R.id.notes_list)

        _notesAdapter = NotesAdapter(this, _notes.toTypedArray())
        listView?.adapter = _notesAdapter

        listView?.onItemClickListener = AdapterView.OnItemClickListener {adapterView, view, i, l ->
            editNote(_notes[i])
        }
    }

    fun createNote(v: View) {
        val noteId = _notes.size
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(EXTRA_NOTE_ID, noteId)
        }
        startActivityForResult(intent, GET_NOTE_REQUEST)
    }

    private fun editNote(note: Note) {
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(EXTRA_EDIT_MODE, true)
            putExtra(EXTRA_NOTE, note)
        }
        startActivityForResult(intent, EDIT_NOTE_REQUEST)
    }

    private fun addNoteItem(note: Note) {
        _notes.add(note)
        _notesAdapter = NotesAdapter(this, _notes.toTypedArray())
        listView?.adapter = _notesAdapter

    }

    private fun updateNoteItem(note: Note) {
        val position = note.id
        _notes[position] = note
        _notesAdapter = NotesAdapter(this, _notes.toTypedArray())
        listView?.adapter = _notesAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val note = data.getSerializableExtra(EXTRA_NOTE) as Note
            Log.d("onActivityResultTesting", note.toString())
            if (requestCode == GET_NOTE_REQUEST ) {
                addNoteItem(note)
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                updateNoteItem(note)
            }
        }
    }
}

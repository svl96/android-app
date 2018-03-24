package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.*
import java.util.ArrayList

const val EXTRA_NOTE : String = "com.yandex.android.EXTRA_NOTE"
const val EXTRA_EDIT_MODE : String = "com.yandex.android.EXTRA_EDIT_MODE"
const val EXTRA_NOTE_ID : String = "com.yandex.android.EXTRA_NOTE_ID"
const val EXTRA_COLOR : String = "com.yandex.android.EXTRA_COLOR"
const val GET_NOTE_REQUEST : Int = 1
const val EDIT_NOTE_REQUEST : Int = 2
const val GET_COLOR_REQUEST : Int = 3

class MainActivity : AppCompatActivity() {

    private var _notes : ArrayList<Note> = arrayListOf()
    private var _notesAdapter : NotesAdapter? = null
    private var _listView: ListView? = null
    private var _actionButton: FloatingActionButton? = null

    val tag = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val savedNotes = savedInstanceState?.getSerializable("NOTES") as ArrayList<*>?
        savedNotes?.mapTo(_notes, { s -> s as Note })

        Log.d(tag, "onCreate()")
        Log.d(tag, (savedNotes == null).toString())

        _listView = findViewById(R.id.notes_list)
        _actionButton = findViewById(R.id.FAB1)

        _notesAdapter = NotesAdapter(this, _notes)
        _listView?.adapter = _notesAdapter

        _listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            editNote(_notes[position])
        }

        _actionButton?.setOnClickListener { v -> createNote(v) }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(tag, "onSaveInstantState()")

        outState?.putSerializable("NOTES", _notes)
        super.onSaveInstanceState(outState)
    }

    // region CreateNote

    private fun createNote(v: View) {
        val noteId = _notes.size
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(EXTRA_NOTE_ID, noteId)
        }
        startActivityForResult(intent, GET_NOTE_REQUEST)
    }


    private fun addNoteItem(note: Note) {
        _notes.add(note)
        _notesAdapter?.notifyDataSetChanged()
    }

    // endregion

    // region EditNote

    private fun editNote(note: Note) {
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(EXTRA_EDIT_MODE, true)
            putExtra(EXTRA_NOTE, note)
        }
        startActivityForResult(intent, EDIT_NOTE_REQUEST)
    }

    private fun updateNoteItem(note: Note) {
        val position = note.id
        _notes[position] = note
        _notesAdapter?.notifyDataSetChanged()
    }

    // endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResultTesting", resultCode.toString())
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

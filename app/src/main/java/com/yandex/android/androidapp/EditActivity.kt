package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*


class EditActivity : AppCompatActivity() {

    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var saveButton : Button? = null

    private val colorSet : Array<Int> = arrayOf(
            Color.BLUE,
            Color.CYAN,
            Color.MAGENTA,
            Color.RED,
            Color.YELLOW
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        this.editTitle = findViewById(R.id.title_edit)
        this.editDescription = findViewById(R.id.description_edit)
        this.saveButton = findViewById(R.id.save_button)

        val editMode = this.intent.getBooleanExtra(EXTRA_EDIT_MODE, false)

        if (editMode) {
            onEditMode()
        }
        else onCreateMode()

    }

    private fun updateNote(note: Note){
        note.title = editTitle?.text.toString()
        note.description = editDescription?.text.toString()
        note.datetime = Calendar.getInstance().time
    }

    private fun onEditMode() {
        val note = this.intent.getSerializableExtra(EXTRA_NOTE) as Note

        this.editTitle?.setText(note.title, TextView.BufferType.EDITABLE)
        this.editDescription?.setText(note.description, TextView.BufferType.EDITABLE)

        saveButton?.setOnClickListener {
            updateNote(note)
            sendNote(note)
        }

    }

    private fun createNote(id: Int): Note {
        val title = editTitle?.text.toString()
        val description = editDescription?.text.toString()
        val date = Calendar.getInstance().time
        val color = colorSet[Random().nextInt(colorSet.size)]
        return Note(id, title, description , date, color)
    }

    private fun onCreateMode() {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)

        saveButton?.setOnClickListener {
            val note = createNote(noteId)
            sendNote(note)
        }
    }


    private fun sendNote(note: Note) {
        Log.d("send_note_test", note.toString())
        val intent = Intent().apply {
            putExtra(EXTRA_NOTE, note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
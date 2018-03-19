package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*


class EditActivity : AppCompatActivity() {

    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var saveButton : Button? = null

    private var colors: Array<Int> = arrayOf()

    private fun setColors() {
        val colorIds = listOf(
                R.string.noteColorRed,
                R.string.noteColorAmber,
                R.string.noteColorBlue,
                R.string.noteColorCyan,
                R.string.noteColorDeepOrange,
                R.string.noteColorLime,
                R.string.noteColorPink,
                R.string.noteColorPurple)

        colors = colorIds.map { id -> Color.parseColor(getString(id)) }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        setColors()

        this.editTitle = findViewById(R.id.title_edit)
        this.editDescription = findViewById(R.id.description_edit)
        this.saveButton = findViewById(R.id.save_button)

        val editMode = this.intent.getBooleanExtra(EXTRA_EDIT_MODE, false)

        if (editMode) {
            onEditMode()
        }
        else onCreateMode()
    }

    private fun sendNote(note: Note) {
        val intent = Intent().apply {
            putExtra(EXTRA_NOTE, note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // region EditNote

    private fun onEditMode() {
        setTitle(R.string.edit_title)
        val note = this.intent.getSerializableExtra(EXTRA_NOTE) as Note

        this.editTitle?.setText(note.title, TextView.BufferType.EDITABLE)
        this.editDescription?.setText(note.description, TextView.BufferType.EDITABLE)

        saveButton?.setOnClickListener {
            updateNote(note)
            sendNote(note)
        }
    }

    private fun updateNote(note: Note){
        note.title = editTitle?.text.toString()
        note.description = editDescription?.text.toString()
        note.datetime = Calendar.getInstance().time
    }

    // endregion

    // region CreateNote
    private fun onCreateMode() {
        setTitle(R.string.create_title)
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)

        saveButton?.setOnClickListener {
            val note = createNote(noteId)
            sendNote(note)
        }
    }

    private fun createNote(id: Int): Note {
        Log.d("colors", colors.contentDeepToString())
        val noteTitle = editTitle?.text.toString()
        val noteDescription = editDescription?.text.toString()
        val noteDate = Calendar.getInstance().time
        val noteColor = colors[Random().nextInt(colors.size)]
        return Note(id, noteTitle, noteDescription , noteDate, noteColor)
    }

    // endregion



}
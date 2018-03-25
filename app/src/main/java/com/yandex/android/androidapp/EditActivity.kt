package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*


class EditActivity : AppCompatActivity() {

    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var saveButton : Button? = null
    private var noteColor : Int = Color.RED
    private var currentColor : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        editTitle = findViewById(R.id.title_edit)
        editDescription = findViewById(R.id.description_edit)
        saveButton = findViewById(R.id.save_button)
        currentColor = findViewById(R.id.color_view)

        val editMode = intent.getBooleanExtra(EXTRA_EDIT_MODE, false)

        currentColor?.setOnClickListener {
            onChooseColor()
        }

        if (editMode) {
            onEditMode()
        }
        else onCreateMode()
        currentColor?.setBackgroundColor(noteColor)
    }

    private fun onChooseColor() {
        val outIntent = Intent(this, ColorPickerActivity::class.java)
        startActivityForResult(outIntent, GET_COLOR_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GET_COLOR_REQUEST &&
                 data != null) {
            noteColor = data.getIntExtra(EXTRA_COLOR, Color.RED)
            currentColor?.setBackgroundColor(noteColor)

        }
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

        noteColor = note.color

        saveButton?.setOnClickListener {
            updateNote(note)
            sendNote(note)
        }
    }

    private fun updateNote(note: Note){
        note.title = editTitle?.text.toString()
        note.description = editDescription?.text.toString()
        note.datetime = Calendar.getInstance().time
        note.color = noteColor
    }

    // endregion

    // region CreateNote
    private fun onCreateMode() {
        setTitle(R.string.create_title)
        saveButton?.setOnClickListener {
            val note = createNote()
            sendNote(note)
        }
    }

    private fun createNote(): Note {
        val uniqueID = UUID.randomUUID().toString()
        val noteTitle = editTitle?.text.toString()
        val noteDescription = editDescription?.text.toString()
        val noteDate = Calendar.getInstance().time
        val noteColor = noteColor
        return Note(uniqueID, noteTitle, noteDescription , noteDate, noteColor)
    }

    // endregion



}
package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*


class EditActivity : AppCompatActivity() {

    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var noteColor : Int = Color.RED
    private var currentColor : View? = null
    private var saveNote : () -> Unit = {}
    private var note : Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        editTitle = findViewById(R.id.title_edit)
        editDescription = findViewById(R.id.description_edit)
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

    private fun sendNote() {
        val intent = Intent().apply {
            putExtra(EXTRA_NOTE, note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // region Setup Menu

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.save_note_action) {
            saveNote()
            sendNote()
        }
        return true
    }

    // endregion

    // region Choose Color

    private fun onChooseColor() {
        val outIntent = Intent(this, ColorPickerActivity::class.java).apply {
            putExtra(EXTRA_COLOR, noteColor)
        }
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

    // endregion

    // region Edit Note

    private fun onEditMode() {
        setTitle(R.string.edit_title)
        note = this.intent.getSerializableExtra(EXTRA_NOTE) as Note

        this.editTitle?.setText(note?.title, TextView.BufferType.EDITABLE)
        this.editDescription?.setText(note?.description, TextView.BufferType.EDITABLE)

        noteColor = note?.color ?: Color.RED

        saveNote = {updateNote()}

    }

    private fun updateNote(){
        note?.title = editTitle?.text.toString()
        note?.description = editDescription?.text.toString()
        note?.datetime = Calendar.getInstance().time
        note?.color = noteColor
    }

    // endregion

    // region Create Note

    private fun onCreateMode() {
        setTitle(R.string.create_title)
        saveNote = {createNote()}
    }

    private fun createNote() {
        val uniqueID = UUID.randomUUID().toString()
        val noteTitle = editTitle?.text.toString()
        val noteDescription = editDescription?.text.toString()
        val noteDate = Calendar.getInstance().time
        val noteColor = noteColor
        note = Note(uniqueID, noteTitle, noteDescription , noteDate, noteColor)
    }

    // endregion



}
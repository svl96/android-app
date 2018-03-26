package com.yandex.android.androidapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.util.*


class EditActivity : AppCompatActivity() {

    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var noteColor : Int = DEFAULT_COLOR
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
        val outIntent = Intent()
        outIntent.putExtra(EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, outIntent)
        finish()
    }

    private fun createBackPressDialog() : AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Leave Without Save?")
                .setPositiveButton("Yes", { _, _ ->
                    super.onBackPressed()
                })
                .setNegativeButton("Cancel", {_, _ ->
                })
        return dialogBuilder.create()
    }

    private fun isDataChanged() : Boolean {
        val title = editTitle?.text
        val desc = editDescription?.text

        if (title.isNullOrEmpty() && desc.isNullOrEmpty() && note == null)
            return false
        return (title?.toString() != note?.title || desc.toString() != note?.description ||
                noteColor != note?.color)

    }

    override fun onBackPressed() {
        if (isDataChanged()) {
            val dialog = createBackPressDialog()
            dialog.show()
        } else
            super.onBackPressed()
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
        } else {
            onBackPressed()
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
            noteColor = data.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
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

        noteColor = note?.color ?: DEFAULT_COLOR

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
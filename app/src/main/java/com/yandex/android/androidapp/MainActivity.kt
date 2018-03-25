package com.yandex.android.androidapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.*
import java.util.ArrayList

const val EXTRA_NOTE : String = "com.yandex.android.EXTRA_NOTE"
const val EXTRA_EDIT_MODE : String = "com.yandex.android.EXTRA_EDIT_MODE"
const val EXTRA_COLOR : String = "com.yandex.android.EXTRA_COLOR"
const val DEFAULT_COLOR : Int = Color.RED
const val GET_NOTE_REQUEST : Int = 1
const val EDIT_NOTE_REQUEST : Int = 2
const val GET_COLOR_REQUEST : Int = 3

class MainActivity : AppCompatActivity() {

    private var _notes : ArrayList<Note> = arrayListOf()
    private var _notesAdapter : NotesAdapter? = null
    private var _listView: ListView? = null
    private var _actionButton: FloatingActionButton? = null
    private val tag = "MainActivity"

    // region Setup Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val savedNotes = savedInstanceState?.getSerializable("NOTES") as ArrayList<*>?
        savedNotes?.mapTo(_notes, { s -> s as Note })

        _listView = findViewById(R.id.notes_list)
        _actionButton = findViewById(R.id.FAB1)
        _actionButton?.setOnClickListener { v -> createNote(v) }

        setUpListView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(tag, "onSaveInstantState()")

        outState?.putSerializable("NOTES", _notes)
        super.onSaveInstanceState(outState)
    }

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

    private fun setUpListView() {
        _notesAdapter = NotesAdapter(this, _notes)
        _listView?.adapter = _notesAdapter

        _listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            editNote(_notes[position])
        }

        _listView?.setOnItemLongClickListener { _, _, position: Int, _ ->
            onItemLongClick(position)
        }
    }

    // endregion

    // region Create Note

    private fun createNote(v: View) {
        val intent = Intent(this, EditActivity::class.java)
        startActivityForResult(intent, GET_NOTE_REQUEST)
    }


    private fun addNoteItem(note: Note) {
        _notes.add(note)
        _notesAdapter?.notifyDataSetChanged()
    }

    // endregion

    // region Edit Note

    private fun editNote(note: Note) {
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(EXTRA_EDIT_MODE, true)
            putExtra(EXTRA_NOTE, note)
        }
        startActivityForResult(intent, EDIT_NOTE_REQUEST)
    }

    private fun updateNoteItem(editedNote: Note) {
        val index = _notes.indexOfFirst{ note -> note.id == editedNote.id }
        _notes[index] = editedNote
        _notesAdapter?.notifyDataSetChanged()
    }

    // endregion

    // region Delete Note

    private fun createDeleteDialog(position: Int) : AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Delete The Note?")
                .setPositiveButton("Yes", { _, _ ->
                    deleteNote(position)
                })
                .setNegativeButton("Cancel", {_, _ ->
                    Log.d("OnLongClick", "Cancel")
                })
        return dialogBuilder.create()
    }

    private fun deleteNote(position: Int) {
        Log.d("OnLongClick", "Yes: " + position.toString())
        _notes.removeAt(position)
        _notesAdapter?.notifyDataSetChanged()
    }

    private fun onItemLongClick(position: Int) : Boolean
    {
        val dialog = createDeleteDialog(position)
        dialog.show()
        return true
    }

    // endregion

}

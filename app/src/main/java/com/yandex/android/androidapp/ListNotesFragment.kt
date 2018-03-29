package com.yandex.android.androidapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import java.util.ArrayList


class ListNotesFragment : Fragment() {

    companion object {

        private const val NOTES_KEY = "NOTES"

        @JvmStatic
        fun newInstance() : ListNotesFragment {
            return ListNotesFragment()
        }
    }

    private var _notes : ArrayList<Note> = arrayListOf()

    private var _notesAdapter : NotesAdapter? = null
    private var _listView: ListView? = null
    private var _actionButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedNotes = savedInstanceState?.getSerializable("NOTES") as ArrayList<*>?
        savedNotes?.mapTo(_notes, { s -> s as Note })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater?.inflate(R.layout.fragment_list_notes, container, false)!!


        _actionButton = rootView.findViewById(R.id.FAB1)
        _listView = rootView.findViewById(R.id.notes_list_view)
        _actionButton?.setOnClickListener { v -> createNote(v) }

        setUpListView()

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(tag, "onSaveInstantState()")

        outState?.putSerializable("NOTES", _notes)
        super.onSaveInstanceState(outState)
    }

    private fun setUpListView() {
        _notesAdapter = NotesAdapter(context, _notes)
        _listView?.adapter = _notesAdapter

        _listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            editNote(_notes[position])
        }

        _listView?.setOnItemLongClickListener { _, _, position: Int, _ ->
            onItemLongClick(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivityResultTesting", resultCode.toString())
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

    // region Create Note

    private fun createNote(v: View) {
        val intent = Intent(context, EditActivity::class.java)
        startActivityForResult(intent, GET_NOTE_REQUEST)
    }


    private fun addNoteItem(note: Note) {
        _notes.add(note)
        _notesAdapter?.notifyDataSetChanged()
    }

    // endregion

    // region Edit Note

    private fun editNote(note: Note) {
        val intent = Intent(context, EditActivity::class.java).apply {
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
        val dialogBuilder = AlertDialog.Builder(context)
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
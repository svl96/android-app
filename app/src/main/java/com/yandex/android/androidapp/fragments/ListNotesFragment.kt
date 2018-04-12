package com.yandex.android.androidapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.android.androidapp.*
import com.yandex.android.androidapp.adapters.RecyclerViewAdapter


class ListNotesFragment : Fragment(), ItemsContainer<Note> {

    companion object {
        @JvmStatic
        fun newInstance() : ListNotesFragment {
            return ListNotesFragment()
        }
    }

    private var _tag : String = "ListNotesFragment"

    private var _recyclerView: RecyclerView? = null
    private var _actionButton: FloatingActionButton? = null
    private var containerUi : ContainerUI? = null

    private var notesContainer : NotesContainerUI? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(_tag, "onCreate()")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d(_tag, "onCreateView()")
        containerUi?.setActivityTitle(R.string.list_title)

        if (notesContainer == null) {
            notesContainer = containerUi?.getNotesContainer()
        }

        val rootView = inflater?.inflate(R.layout.fragment_list_notes, container, false)!!
        _actionButton = rootView.findViewById(R.id.FAB1)
        _recyclerView = rootView.findViewById(R.id.notes_list_view)
        _actionButton?.setOnClickListener { createItem() }

        setupRecyclerView()

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(tag, "onSaveInstantState()")
        super.onSaveInstanceState(outState)
    }

    private fun setupRecyclerView() {
        _recyclerView?.layoutManager = LinearLayoutManager(activity)
        _recyclerView?.adapter = RecyclerViewAdapter(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivityResultTesting", resultCode.toString())
        if (notesContainer == null) {
            notesContainer = containerUi?.getNotesContainer()
        }
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val note = data.getSerializableExtra(EXTRA_NOTE) as Note
            Log.d("onActivityResultTesting", note.toString())
            if (requestCode == GET_NOTE_REQUEST) {
                addNoteItem(note)
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                updateNoteItem(note)
            }
        }
    }

    override fun getItems(): Array<Note> {
        return notesContainer?.getNotes() ?: arrayOf()
    }

    // region Create Note

    override fun createItem() {
        val editFragment = EditFragment.newInstance(null)
        editFragment.setTargetFragment(this, GET_NOTE_REQUEST)
        Log.d(_tag, "CreateNote()")
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
    }


    private fun addNoteItem(note: Note) {
        if (notesContainer != null) {
            notesContainer?.addNote(note)
            _recyclerView?.adapter?.notifyItemInserted(getItems().size - 1)
        }
    }

    // endregion

    // region Edit Note

    override fun editItem(item: Note) {
        val editFragment = EditFragment.newInstance(item)
        editFragment.setTargetFragment(this, EDIT_NOTE_REQUEST)
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment, "EditFragment")
                .addToBackStack("ListNotes")
                .commit()
    }

    private fun updateNoteItem(editedNote: Note) {
        Log.d(_tag, "UpdateNoteItem")

        notesContainer?.updateNote(editedNote)
        val notes = getItems()
        val index = notes.indexOfFirst { note -> note.id == editedNote.id }
        _recyclerView?.adapter?.notifyItemChanged(index)
    }

    // endregion

    // region Delete Note

    private fun createDeleteDialog(position: Int) : AlertDialog {
        val dialogBuilder = AlertDialog.Builder(activity)
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
        val note = getItems()[position]
        if (notesContainer != null) {
            notesContainer?.deleteNote(note)
            _recyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    override fun deleteItem(position: Int) : Boolean {
        val dialog = createDeleteDialog(position)
        dialog.show()
        return true
    }

    // endregion

}
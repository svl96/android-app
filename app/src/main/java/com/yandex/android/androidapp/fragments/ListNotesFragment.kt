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
import kotlin.collections.ArrayList


class ListNotesFragment : Fragment(), ItemsContainer<Note> {

    companion object {
        @JvmStatic
        fun newInstance() : ListNotesFragment {
            return ListNotesFragment()
        }
    }

    private var _notes : ArrayList<Note> = arrayListOf()
    private var _tag : String = "ListNotesFragment"

    private var _recyclerView: RecyclerView? = null
    private var _actionButton: FloatingActionButton? = null
    private var containerUi : ContainerUI? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedNotes = savedInstanceState?.getSerializable("NOTES") as ArrayList<*>?
        savedNotes?.mapTo(_notes, { s -> s as Note })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        containerUi?.setActivityTitle(R.string.list_title)

        val rootView = inflater?.inflate(R.layout.fragment_list_notes, container, false)!!
        _actionButton = rootView.findViewById(R.id.FAB1)
        _recyclerView = rootView.findViewById(R.id.notes_list_view)
        _actionButton?.setOnClickListener { createItem() }

        setupRecyclerView()

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(tag, "onSaveInstantState()")

        outState?.putSerializable("NOTES", _notes)
        super.onSaveInstanceState(outState)
    }

    private fun setupRecyclerView() {
        _recyclerView?.layoutManager = LinearLayoutManager(activity)
        _recyclerView?.adapter = RecyclerViewAdapter(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivityResultTesting", resultCode.toString())
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

    override fun getItems(): ArrayList<Note> {
        return _notes
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
        _notes.add(note)
        _recyclerView?.adapter?.notifyItemInserted(_notes.size - 1)
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
        val index = _notes.indexOfFirst{ note -> note.id == editedNote.id }
        _notes[index] = editedNote
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
        _notes.removeAt(position)
        _recyclerView?.adapter?.notifyItemRemoved(position)
    }

    override fun deleteItem(position: Int) : Boolean {
        val dialog = createDeleteDialog(position)
        dialog.show()
        return true
    }

    // endregion

}
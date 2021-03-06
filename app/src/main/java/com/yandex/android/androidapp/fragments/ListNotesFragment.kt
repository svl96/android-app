package com.yandex.android.androidapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

        const val FRAGMENT_TAG : String = "ListNotesFragment"

        @JvmStatic
        fun newInstance() : ListNotesFragment {
            return ListNotesFragment()
        }
    }

    private var _tag : String = "ListNotesFragment"

    private var _recyclerView: RecyclerView? = null
    private var _actionButton: FloatingActionButton? = null
    private var containerUi : ContainerUI? = null
    private var updateBroadcastReceiver : UpdateBroadcastReceiver? = null
    private var position = 0
    private val offset = 20

    // region On Create

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

        val rootView = inflater?.inflate(R.layout.fragment_list_notes, container, false)!!
        _actionButton = rootView.findViewById(R.id.FAB1)
        _recyclerView = rootView.findViewById(R.id.notes_list_view)
        _actionButton?.setOnClickListener { createItem() }

        position = savedInstanceState?.getInt("position") ?: 0
        setupRecyclerView()
        setupBroadcastReceiver()

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(tag, "onSaveInstantState()")

        val manager : LinearLayoutManager = _recyclerView?.layoutManager as LinearLayoutManager
        val position = manager.findFirstVisibleItemPosition()
        outState?.putInt("position", position)

        super.onSaveInstanceState(outState)
    }

    // endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivityResultTesting", resultCode.toString())
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val note = data.getSerializableExtra(EditFragment.EXTRA_NOTE) as Note
            Log.d("onActivityResultTesting", note.toString())
            if (requestCode == GET_NOTE_REQUEST) {
                addNoteItem(note)
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                updateNoteItem(note)
            }
        }
    }

    private fun setupRecyclerView() {

        val layoutManager = LinearLayoutManager(activity)
        _recyclerView?.layoutManager = layoutManager
        _recyclerView?.adapter = RecyclerViewAdapter(this)
        _recyclerView?.layoutManager?.scrollToPosition(position)

        _recyclerView?.addOnScrollListener(
            object : RecyclerScrollListener(layoutManager, position) {
                override fun onLoad(currentPosition: Int, totalCount: Int, view: RecyclerView?) {
                    loadNextNotes(currentPosition)
                }
            }
        )
    }

    fun loadNextNotes(currentPosition: Int) {
        val limit =  currentPosition + offset
        position = currentPosition
        val notesContainer = containerUi?.getNotesContainer()
        notesContainer?.loadNextPageAsync(limit)
    }

    override fun getItems(): Array<Note> {
        val notesContainer = containerUi?.getNotesContainer()
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
        val notesContainer = containerUi?.getNotesContainer()
        notesContainer?.addNoteAsync(note)
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
        val notesContainer = containerUi?.getNotesContainer()
        notesContainer?.updateNoteAsync(editedNote)
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
        val notesContainer = containerUi?.getNotesContainer()
        notesContainer?.deleteNoteAsync(note)
    }

    override fun deleteItem(position: Int) : Boolean {
        val dialog = createDeleteDialog(position)
        dialog.show()
        return true
    }

    // endregion

    fun updateListNotes() {
        _recyclerView?.adapter?.notifyDataSetChanged()
        val totalCount = _recyclerView?.layoutManager?.itemCount ?: 0
        if (totalCount < position) {
            loadNextNotes(position)
        } else {
            _recyclerView?.layoutManager?.scrollToPosition(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context.unregisterReceiver(updateBroadcastReceiver)
    }

    // region Broadcast Receiver

    private fun setupBroadcastReceiver() {
        updateBroadcastReceiver = UpdateBroadcastReceiver()

        val newIntentFilter = IntentFilter(ACTION_UPDATE)
        newIntentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        context.registerReceiver(updateBroadcastReceiver, newIntentFilter)
    }

    inner class UpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val notesContainer = containerUi?.getNotesContainer()
            notesContainer?.refreshDataAsync()
        }
    }

    // endregion

}
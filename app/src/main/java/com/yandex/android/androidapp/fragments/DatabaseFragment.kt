package com.yandex.android.androidapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.android.androidapp.*


class DatabaseFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(request_type : String) : DatabaseFragment {
            val args = Bundle()

            args.putString("REQUEST_TYPE", request_type)

            val outFragment = DatabaseFragment()
            outFragment.arguments = args

            return outFragment
        }
    }

    private var containerUi: ContainerUI? = null
    private var notesContainer: NotesContainerUI? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (notesContainer == null) {
            notesContainer = containerUi?.getNotesContainer()
        }
    }

    fun deleteDataAsync(db: NotesDatabaseHelper, note: Note) {
        val task = DeleteDataTask(db)
        task.execute(note)
    }

    fun updateDataAsync(dbHelper: NotesDatabaseHelper, note: Note) {
        val task = UpdateDataTask(dbHelper)
        task.execute(note)
    }

    fun addDataAsync(dbHelper: NotesDatabaseHelper, notes: Array<Note>) {
        val task = InsertDataTask(dbHelper)
        task.execute(*notes)
    }

    fun getDataAsync(dbHelper: NotesDatabaseHelper, params : Map<String, String>) {
        val task = GetDataTask(dbHelper, params)
        task.execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateDataTask(val dbHelper: NotesDatabaseHelper)
        : AsyncTask<Note, Void, Boolean>() {

        override fun doInBackground(vararg notes: Note?): Boolean {
            val note = notes[0]
            if (note != null) {
                val result = dbHelper.updateNote(note)
                return result != 0
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true)
                containerUi?.updateData()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertDataTask(val dbHelper: NotesDatabaseHelper)
        : AsyncTask<Note, Void, Boolean>() {

        override fun doInBackground(vararg notes: Note?): Boolean {
            val insertNotes = notes.filterNotNull()
            if (insertNotes.count() > 0) {
                dbHelper.addNotes(insertNotes)
                return true
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true)
                containerUi?.updateData()
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteDataTask(val dbHelper: NotesDatabaseHelper)
        : AsyncTask<Note, Void, Boolean>() {
        override fun doInBackground(vararg notes: Note?): Boolean {
            val note = notes[0]
            if (note != null) {
                val result = dbHelper.deleteNote(note)
                return result != 0
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true) {
                containerUi?.updateData()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDataTask(val dbHelper: NotesDatabaseHelper,
                                        val params: Map<String, String>)
        : AsyncTask<Void, Void, Array<Note>>() {

        override fun doInBackground(vararg p0: Void?): Array<Note> {
            return dbHelper.getFilteredNotes(params)
        }

        override fun onPostExecute(result: Array<Note>?) {
            super.onPostExecute(result)
            if (result != null)
                containerUi?.updateDataCallback(result)
        }
    }

}
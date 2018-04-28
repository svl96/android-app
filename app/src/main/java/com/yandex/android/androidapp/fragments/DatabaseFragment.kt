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

    fun test_function() : String {
        return "Test"
    }

    fun getAllData(db: NotesDatabaseHelper) {
        val task = GetAllDataTask(db)
        task.execute()
    }

    fun updateAsync(db: NotesDatabaseHelper, note: Note) {
//        val task = UpdateDataTask()
//        task.execute(note)
    }


    @SuppressLint("StaticFieldLeak")
    private inner class UpdateDataTask(val dbHelper: NotesDatabaseHelper)
        : AsyncTask<Note, Void, Boolean>() {

        override fun doInBackground(vararg notes: Note?): Boolean {
            val note = notes[0]
            if (note != null)
                dbHelper.updateNote(note)

            return false
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetAllDataTask(val dbHelper: NotesDatabaseHelper)
        : AsyncTask<Void, Void, Array<Note>>() {

        override fun doInBackground(vararg p0: Void?): Array<Note> {
            return dbHelper.getAllNotes()
        }

        override fun onPostExecute(result: Array<Note>?) {
            super.onPostExecute(result)
//            mCallback(result)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertDataTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg p0: String?): Boolean {
            return true
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteDataTask : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg p0: Void?): Boolean {
            return true

        }
    }



}
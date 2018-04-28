package com.yandex.android.androidapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import android.view.*

import android.widget.EditText
import android.widget.TextView
import com.yandex.android.androidapp.*
import java.util.*


class EditFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(note: Note?) : EditFragment {
            val args = Bundle()

            if (note != null) {
                args.putSerializable(EXTRA_NOTE, note)
            }
            val fragment = EditFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private var note : Note? = null
    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var noteColor : Int = DEFAULT_COLOR
    private var currentColor : View? = null
    private var saveNote : () -> Unit = {}
    private var containerUi : ContainerUI? = null

    private val _tag : String = "EditFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(_tag, "onCreate()")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        note = arguments.getSerializable(EXTRA_NOTE) as Note?
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(_tag, "onCreateView()")

        val rootView = inflater?.inflate(R.layout.fragment_edit, container, false)!!

        editTitle = rootView.findViewById(R.id.title_edit)
        editDescription = rootView.findViewById(R.id.description_edit)
        currentColor = rootView.findViewById(R.id.color_view)

        if (note != null)
            onEditMode()
        else onCreateMode()


        currentColor?.setOnClickListener {
            onChooseColor()
            containerUi?.closeKeyboard()
        }
        currentColor?.setBackgroundColor(noteColor)

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")
    }

    private fun sendNote() {
        val outIntent = Intent()
        outIntent.putExtra(EXTRA_NOTE, note)
        targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, outIntent)
        fragmentManager.popBackStack()
    }

    // region Setup Menu

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.save_button, menu)
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
        val colorPickerFragment = ColorPickerFragment.newInstance(noteColor)
        colorPickerFragment.setTargetFragment(this, GET_COLOR_REQUEST)
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, colorPickerFragment, "ColorPickerFragment")
                .addToBackStack(null)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(_tag, "onActivityResult")
        if (resultCode == Activity.RESULT_OK && requestCode == GET_COLOR_REQUEST &&
                data != null) {
            noteColor = data.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
            note?.color = noteColor
            currentColor?.setBackgroundColor(noteColor)
        }
    }

    // endregion

    // region Edit Note

    private fun onEditMode() {
        containerUi?.setActivityTitle(R.string.edit_title)

        this.editTitle?.setText(note?.title, TextView.BufferType.EDITABLE)
        this.editDescription?.setText(note?.description, TextView.BufferType.EDITABLE)

        noteColor = note?.color ?: DEFAULT_COLOR

        saveNote = {updateNote()}

    }

    private fun updateNote(){
        val date = Calendar.getInstance().time
        note?.title = editTitle?.text.toString()
        note?.description = editDescription?.text.toString()
        note?.timeEdit = date
        note?.timeView = date
        note?.color = noteColor
    }

    // endregion

    // region Create Note

    private fun onCreateMode() {
        containerUi?.setActivityTitle(R.string.create_title)
        saveNote = {createNote()}
    }

    private fun createNote() {
        val uniqueID = UUID.randomUUID().toString()
        val noteTitle = editTitle?.text.toString()
        val noteDescription = editDescription?.text.toString()
        val noteDate = Calendar.getInstance().time
        val noteColor = noteColor
        note = Note(
                id = uniqueID,
                title = noteTitle,
                description = noteDescription,
                timeCreate = noteDate,
                timeView = noteDate,
                timeEdit = noteDate,
                color = noteColor)
    }

    // endregion

    private fun createBackPressDialog() : AlertDialog {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage("Leave Without Save?")
                .setPositiveButton("Yes", { _, _ ->
                    fragmentManager.popBackStack()
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

    private fun updateTimeViewValue() : Boolean {
        val date = Calendar.getInstance().time
        if (note != null) {
            note?.timeView = date
            return true
        }
        return false
    }

    fun onBackPressed() {
        when {
            isDataChanged() -> {
                val dialog = createBackPressDialog()
                dialog.show()
            }
            updateTimeViewValue() -> sendNote()
            else -> fragmentManager.popBackStack()
        }
    }

}
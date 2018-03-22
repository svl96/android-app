package com.yandex.android.androidapp

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*


class EditActivity : AppCompatActivity() {

    private var editTitle : EditText? = null
    private var editDescription : EditText? = null
    private var saveButton : Button? = null
    private var noteColor : Int? = null
    private var colors: IntArray = intArrayOf()

    private fun setColors() {
        val colorIds = listOf(
                R.string.noteColorRed,
                R.string.noteColorAmber,
                R.string.noteColorBlue,
                R.string.noteColorCyan,
                R.string.noteColorDeepOrange,
                R.string.noteColorLime,
                R.string.noteColorPink,
                R.string.noteColorPurple)

        colors = getHSVGradientColors(60)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        setColors()
        drawGrad()

        this.editTitle = findViewById(R.id.title_edit)
        this.editDescription = findViewById(R.id.description_edit)
        this.saveButton = findViewById(R.id.save_button)

        val editMode = this.intent.getBooleanExtra(EXTRA_EDIT_MODE, false)

        if (editMode) {
            onEditMode()
        }
        else onCreateMode()
    }

    private fun getHSVGradientColors(hueStep : Int) : IntArray {
        val satVal = .8F
        val brightnessVal = 1F
        val colorsList = mutableListOf<Int>()
        for (i in 0..360 step hueStep ) {
            val hueVal = i.toFloat()
            colorsList.add(Color.HSVToColor(floatArrayOf(hueVal, satVal, brightnessVal)))
        }

        return colorsList.toIntArray()
    }

    private fun drawGrad() {
        val colorView = findViewById<View>(R.id.test_gradient_view)
        val scrollLayout = findViewById<LinearLayout>(R.id.scroll_layout)
        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                getHSVGradientColors(60))
        drawable.shape = GradientDrawable.RECTANGLE
        scrollLayout.background = drawable

        val squareCount = 16
        val squareWidthDP = 50
        val squareMarginDP : Int = squareWidthDP/2
        val hueValues = calcHueValues(squareCount, squareWidthDP, squareMarginDP)

        for (i in 1..squareCount) {
            val hsvColorBright = Color.HSVToColor(floatArrayOf(hueValues[i-1].toFloat(), 1F, .95F))

            scrollLayout.addView(createRectView(hsvColorBright))
        }
    }

    private fun calcHueValues(squareCount : Int, widthDP : Int, marginDP : Int ) : Array<Double> {
        val fullWidth = (squareCount * (widthDP + marginDP*2)).toDouble()
        val outHueValues = mutableListOf<Double>()
        val halfWidth = widthDP / 2
        var currentPos = 0
        for (i in 1..squareCount) {
            val center = currentPos + marginDP + halfWidth
            val hueVal = center / fullWidth * 360
            outHueValues.add(hueVal)
            currentPos += 2 * marginDP + widthDP
        }

        return outHueValues.toTypedArray()
    }

    private fun createRectView(color : Int) : View {
        val outRect = View(this)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        val displayMetrics = resources.displayMetrics

        layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50F, displayMetrics).toInt()

        layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50F, displayMetrics).toInt()

        val typedMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25F, displayMetrics).toInt()

        layoutParams.marginStart = typedMargin
        layoutParams.marginEnd = typedMargin
        layoutParams.topMargin = typedMargin
        layoutParams.bottomMargin = typedMargin

        outRect.layoutParams = layoutParams
        outRect.setBackgroundColor(color)


        outRect.setOnClickListener { v ->
            Log.d("onClickListener", "enter")
            val drowableBack = v.background as ColorDrawable
            noteColor = drowableBack.color
            Log.d("onClickListener", noteColor.toString())
            editTitle?.setBackgroundColor(noteColor ?: Color.WHITE)
        }

        return outRect
    }

    private fun sendNote(note: Note) {
        val intent = Intent().apply {
            putExtra(EXTRA_NOTE, note)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // region EditNote

    private fun onEditMode() {
        setTitle(R.string.edit_title)
        val note = this.intent.getSerializableExtra(EXTRA_NOTE) as Note

        this.editTitle?.setText(note.title, TextView.BufferType.EDITABLE)
        this.editDescription?.setText(note.description, TextView.BufferType.EDITABLE)

        saveButton?.setOnClickListener {
            updateNote(note)
            sendNote(note)
        }
    }

    private fun updateNote(note: Note){
        note.title = editTitle?.text.toString()
        note.description = editDescription?.text.toString()
        note.datetime = Calendar.getInstance().time
        note.color = noteColor ?: note.color
    }

    // endregion

    // region CreateNote
    private fun onCreateMode() {
        setTitle(R.string.create_title)
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)

        saveButton?.setOnClickListener {
            val note = createNote(noteId)
            sendNote(note)
        }
    }

    private fun createNote(id: Int): Note {
        val noteTitle = editTitle?.text.toString()
        val noteDescription = editDescription?.text.toString()
        val noteDate = Calendar.getInstance().time
        val noteColor = noteColor ?: colors[Random().nextInt(colors.size)]
        return Note(id, noteTitle, noteDescription , noteDate, noteColor)
    }

    // endregion



}
package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView


class ColorPickerActivity : AppCompatActivity() {

    private var currentColorView : View? = null
    private var hsvTextView : TextView? = null
    private var rgbTextView : TextView? = null

    private var currentColorValue : Int? = null

    private val squareSideDP : Int = 50

    private val tag = "ColorPikerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        Log.d(tag, "onCreate()")

        currentColorView = findViewById(R.id.current_color_view)
        hsvTextView = findViewById(R.id.text_hsv)
        rgbTextView = findViewById(R.id.text_rgb)

        currentColorValue = intent.getIntExtra(EXTRA_COLOR, R.color.colorAccent)
        currentColorView?.setBackgroundColor(currentColorValue!!)

        drawGrad()
    }

    override fun onBackPressed() {
        val intent = Intent().apply {
            putExtra(EXTRA_COLOR, currentColorValue)
        }
        Log.d(tag, "onBackPressed")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d(tag, "onOptionItemSelected")
        Log.d(tag, item?.itemId.toString())
        if (item?.itemId == R.id.parent)
        {
            Log.d(tag, "parentId")
        } else if (item?.itemId == R.id.home) {
            Log.d(tag, "homeId")
        }
        onBackPressed()
        return true
    }

    // region Draw Gradient Scroll

    private fun drawGrad() {
        val scrollLayout = findViewById<LinearLayout>(R.id.scroll_layout)
        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                getHSVGradientColors(60))
        drawable.shape = GradientDrawable.RECTANGLE
        scrollLayout.background = drawable

        val squareCount = 16
        val squareMarginDP : Int = squareSideDP/2
        val hueValues = calcHueValues(squareCount, squareSideDP, squareMarginDP)

        for (i in 1..squareCount) {
            val hsvColorBright = Color.HSVToColor(floatArrayOf(hueValues[i-1].toFloat(), 1F, .95F))
            scrollLayout.addView(createRectView(hsvColorBright))
        }

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

    // endregion

    private fun createRectView(color : Int) : View {
        val outRect = View(this)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        val displayMetrics = resources.displayMetrics

        val typedSquareSide = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                squareSideDP.toFloat(), displayMetrics).toInt()
        val typedSquareMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                squareSideDP.toFloat() / 2, displayMetrics).toInt()

        layoutParams.width =  typedSquareSide
        layoutParams.height = typedSquareSide

        layoutParams.marginStart = typedSquareMargin
        layoutParams.marginEnd = typedSquareMargin
        layoutParams.topMargin = typedSquareMargin
        layoutParams.bottomMargin = typedSquareMargin

        outRect.layoutParams = layoutParams
        outRect.setBackgroundColor(color)


        outRect.setOnClickListener { v ->
            Log.d("onClickListener", "enter")
            val drawableBack = v.background as ColorDrawable
            setUpCurrentColorView(drawableBack.color)
        }

        return outRect
    }

    private fun setUpCurrentColorView(color: Int) {
        currentColorValue = color
        currentColorView?.setBackgroundColor(color)
    }

}
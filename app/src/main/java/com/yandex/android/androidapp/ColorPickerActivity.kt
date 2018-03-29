package com.yandex.android.androidapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
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
    private var scrollLayout : LinearLayout? = null

    private var currentColorValue : Int = DEFAULT_COLOR
    private val squareSideDP : Int = 50
    private val squareMarginDP: Int = 25
    private val squareCount: Int = 16
    private val hueStep : Int = 60
    private val tag = "ColorPikerActivity"
    private val backgroundSat : Float = .8F
    private val backgroundBrightness : Float = 1F
    private val squareSat : Float = 1F
    private val squareBrightness = .95F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)


        currentColorView = findViewById(R.id.current_color_view)
        hsvTextView = findViewById(R.id.text_hsv)
        rgbTextView = findViewById(R.id.text_rgb)
        scrollLayout = findViewById(R.id.scroll_layout)

        setupCurrentColorView(intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR))
        setupColorTextViews()

        drawScrollView()
    }

    // region Setup Color View

    private fun setupColorTextViews() {
        hsvTextView?.text = getHSVFormattedString()
        rgbTextView?.text = getRGBFormattedString()
    }

    private fun getRGBFormattedString() : String {
        val red = Color.red(currentColorValue)
        val green = Color.green(currentColorValue)
        val blue = Color.blue(currentColorValue)

        return String.format("RGB: $red, $green, $blue")
    }

    private fun getHSVFormattedString() : String {
        val hsvValue : FloatArray = floatArrayOf(1F, 1F, 1F)
        Color.colorToHSV(currentColorValue, hsvValue)

        return String.format(
                "HSV: ${hsvValue[0].toInt()}, ${hsvValue[1].toInt()}, ${hsvValue[2].toInt()}")
    }

    private fun setupCurrentColorView(color: Int) {
        currentColorValue = color
        currentColorView?.setBackgroundColor(color)
    }

    // endregion

    // region Back Press Action

    override fun onBackPressed() {
        val intent = Intent().apply {
            putExtra(EXTRA_COLOR, currentColorValue)
        }
        Log.d(tag, "onBackPressed")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    // endregion

    // region Draw Gradient Scroll

    private fun fillScrollBackground() {
        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                getHSVGradientColors(hueStep))

        drawable.shape = GradientDrawable.RECTANGLE
        scrollLayout?.background = drawable
    }

    private fun addColorRectanglesView() {
        val hueValues = calcCenterHueValues(squareCount, squareSideDP, squareMarginDP)

        for (i in 1..squareCount) {
            val hsvColorBright = Color.HSVToColor(floatArrayOf(
                    hueValues[i-1].toFloat(),
                    squareSat,
                    squareBrightness
            ))
            scrollLayout?.addView(createColorRectView(hsvColorBright))
        }
    }

    private fun drawScrollView() {
        fillScrollBackground()
        addColorRectanglesView()
    }

    private fun getHSVGradientColors(hueStep : Int) : IntArray {
        val colorsList = mutableListOf<Int>()
        for (i in 0..360 step hueStep ) {
            val hueVal = i.toFloat()
            colorsList.add(Color.HSVToColor(
                    floatArrayOf(hueVal, backgroundSat, backgroundBrightness)))
        }

        return colorsList.toIntArray()
    }

    private fun calcCenterHueValues(squareCount : Int, widthDP : Int, marginDP : Int ) : Array<Double> {
        val outHueValues = mutableListOf<Double>()

        val squareShift = 2 * marginDP + widthDP
        val fullWidth = (squareCount * squareShift).toDouble()
        val centerShift = marginDP + widthDP / 2

        var currentPos = 0
        for (i in 1..squareCount) {
            val center = currentPos + centerShift
            val hueVal = (center / fullWidth) * 360
            outHueValues.add(hueVal)
            currentPos += squareShift
        }

        return outHueValues.toTypedArray()
    }

    private fun createColorRectView(color : Int) : View {
        val outRect = View(this)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        val displayMetrics = resources.displayMetrics

        val typedSquareSide = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                squareSideDP.toFloat(), displayMetrics).toInt()
        val typedSquareMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                squareMarginDP.toFloat(), displayMetrics).toInt()

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
            setupCurrentColorView(drawableBack.color)
            setupColorTextViews()
        }

        return outRect
    }

    // endregion

}
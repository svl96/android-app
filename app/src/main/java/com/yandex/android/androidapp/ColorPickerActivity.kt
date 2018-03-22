package com.yandex.android.androidapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView


class ColorPickerActivity : AppCompatActivity() {

    private var currentColorView : View? = null
    private var hsvTextView : TextView? = null
    private var rgbTextView : TextView? = null

    private var currentColorValue : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        currentColorView = findViewById(R.id.current_color_view)
        hsvTextView = findViewById(R.id.text_hsv)
        rgbTextView = findViewById(R.id.text_rgb)

        val currentColorValue = intent.getIntExtra(EXTRA_COLOR, Color.RED)
        val shape = currentColorView?.background as ShapeDrawable
        shape.paint.color = currentColorValue

        val colorDrawable = currentColorView?.background as GradientDrawable

        val colorScroll = findViewById<ScrollView>(R.id.color_scrollview)
        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(Color.RED, Color.YELLOW, Color.GREEN,
                Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED))

        drawable.shape = GradientDrawable.RECTANGLE
        colorScroll.background = drawable

        drawGrad()
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

    private fun setUpCurrentColorView(color: Int) {
        currentColorValue = color
        currentColorView?.setBackgroundColor(color)
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
            val drawableBack = v.background as ColorDrawable
            setUpCurrentColorView(drawableBack.color)
        }

        return outRect
    }

}
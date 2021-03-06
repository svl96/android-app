package com.yandex.android.androidapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.yandex.android.androidapp.Note
import com.yandex.android.androidapp.R

class ColorPickerFragment : Fragment() {

    private var currentColorView : View? = null
    private var hsvTextView : TextView? = null
    private var rgbTextView : TextView? = null
    private var scrollLayout : LinearLayout? = null

    private var currentColorValue : Int = Note.DEFAULT_COLOR

    companion object {
        const val EXTRA_COLOR : String = "com.yandex.android.EXTRA_COLOR"

        @JvmStatic
        fun newInstance(color: Int) : ColorPickerFragment {
            val args = Bundle()

            args.putInt(EXTRA_COLOR, color)
            val fragment = ColorPickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    // region On Create

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        currentColorValue = savedInstanceState?.getInt("COLOR") ?: arguments.getInt(EXTRA_COLOR)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val rootView = inflater?.inflate(R.layout.fragment_color_picker, container, false)!!

        currentColorView = rootView.findViewById(R.id.current_color_view)
        hsvTextView = rootView.findViewById(R.id.text_hsv)
        rgbTextView = rootView.findViewById(R.id.text_rgb)
        scrollLayout = rootView.findViewById(R.id.scroll_layout)

        currentColorView?.setBackgroundColor(currentColorValue)

        setupColorTextViews()
        drawScrollView()

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("COLOR", currentColorValue)

        super.onSaveInstanceState(outState)
    }

    // endregion

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

//    override fun onBackPressed() {
//        val intent = Intent().apply {
//            putExtra(EXTRA_COLOR, currentColorValue)
//        }
//        Log.d(tag, "onBackPressed")
//        setResult(Activity.RESULT_OK, intent)
//        finish()
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        onBackPressed()
//        return true
//    }

    // endregion

    // region Setup Menu

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.save_button, menu)
    }

    private fun sendColor() {
        val outIntent = Intent().apply {
            putExtra(EXTRA_COLOR, currentColorValue)
        }
        targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, outIntent)
        fragmentManager.popBackStack()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.save_note_action) {
            sendColor()
        }
        return true
    }

    // endregion

    // region Draw Gradient Scroll

    private fun fillScrollBackground() {
        val hueStep = resources.getInteger(R.integer.color_pick_hue_step)
        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                getHSVGradientColors(hueStep))

        drawable.shape = GradientDrawable.RECTANGLE
        scrollLayout?.background = drawable
    }

    private fun addColorRectanglesView() {
        val squareCount = resources.getInteger(R.integer.color_pick_square_count)
        val squareSideDP = resources.getInteger(R.integer.color_pick_square_side)
        val squareMarginDP = resources.getInteger(R.integer.color_pick_square_margin)
        val hueValues = calcCenterHueValues(squareCount, squareSideDP, squareMarginDP)

        val squareSat: Float = (resources
                .getInteger(R.integer.color_pick_square_saturation) / 100.0).toFloat()
        val squareBrightness: Float = (resources
                .getInteger(R.integer.color_pick_square_brightness) / 100.0).toFloat()

        for (i in 1..squareCount) {
            val hsvColorBright = Color.HSVToColor(floatArrayOf(
                    hueValues[i-1].toFloat(),
                    squareSat,
                    squareBrightness
            ))
            scrollLayout?.addView(createColorRectView(hsvColorBright, squareSideDP, squareMarginDP))
        }
    }

    private fun drawScrollView() {
        fillScrollBackground()
        addColorRectanglesView()
    }

    private fun getHSVGradientColors(hueStep : Int) : IntArray {
        val colorsList = mutableListOf<Int>()
        val backgroundSat: Float = (resources
                .getInteger(R.integer.color_pick_background_saturation) / 100.0).toFloat()
        val backgroundBrightness: Float = (resources
                .getInteger(R.integer.color_pick_background_brightness) / 100.0).toFloat()

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

    private fun createColorRectView(color : Int, squareSideDP: Int, squareMarginDP: Int) : View {
        val outRect = View(this.activity)
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
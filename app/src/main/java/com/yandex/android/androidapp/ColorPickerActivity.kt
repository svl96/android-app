package com.yandex.android.androidapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.ScrollView

class ColorPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        val colorScroll = findViewById<ScrollView>(R.id.color_scrollview)

        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(Color.RED, Color.YELLOW, Color.GREEN,
                Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED))
        drawable.shape = GradientDrawable.RECTANGLE
        colorScroll.background = drawable
    }
}
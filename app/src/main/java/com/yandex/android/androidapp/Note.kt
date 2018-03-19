package com.yandex.android.androidapp

import android.graphics.Color
import java.io.Serializable
import java.util.*

data class Note(val id: Int, var title: String, var description: String = "",
                var datetime: Date, val color: Int) : Serializable
package com.yandex.android.androidapp

import java.io.Serializable
import java.util.*

data class Note(val id: String, var title: String, var description: String = "", var color: Int,
                val timeCreate: Date, var timeEdit: Date, var timeView: Date
                ) : Serializable
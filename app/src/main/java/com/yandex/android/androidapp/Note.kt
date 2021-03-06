package com.yandex.android.androidapp

import android.graphics.Color
import org.json.JSONObject
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

data class Note(val id: String, var title: String, var description: String = "", var color: Int,
                val timeCreate: Date, var timeEdit: Date, var timeView: Date
                ) : Serializable {

    companion object {
        const val DEFAULT_COLOR : Int = Color.RED

        @JvmStatic
        fun formatDate(date : Date?) : String? {
            if (date != null) {
                val dateFormatString = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
                val dateFormat = SimpleDateFormat(dateFormatString, Locale.getDefault())

                return dateFormat.format(date)
            }
            return null
        }

        @JvmStatic
        fun parseDate(string: String?) : Date? {
            if (string != null) {
                val dateFormatString = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
                val dateFormat = SimpleDateFormat(dateFormatString, Locale.getDefault())
                return dateFormat.parse(string)
            }

            return null
        }

        fun getNoteFromJson(id: String, json : String ) : Note? {
            return try {
                val jsonObj = JSONObject(json)

                val title = jsonObj.getString("title")
                val description = jsonObj.getString("description")
                val color = Color.parseColor(jsonObj.getString("color"))
                val timeCreate = parseDate(jsonObj.getString("created"))!!
                val timeEdit = parseDate(jsonObj.getString("edited"))!!
                val timeView = parseDate(jsonObj.getString("viewed"))!!

                Note(id, title, description, color, timeCreate, timeEdit, timeView)
            } catch (ex : NullPointerException) {
                null
            }
        }
    }

    fun getJson() : String {
        val jsonMap = HashMap<String, String>()
        jsonMap["title"] = title
        jsonMap["description"] = description
        jsonMap["color"] = getHEXColor()
        jsonMap["created"] = formatDate(timeCreate) ?: ""
        jsonMap["edited"] = formatDate(timeEdit) ?: ""
        jsonMap["viewed"] = formatDate(timeView) ?: ""

        return JSONObject(jsonMap).toString()
    }

    fun getHEXColor() : String {
        val red = (color shr 16) and 0xff
        val green = (color shr 8) and 0xff
        val blue = color and 0xff
        return "#%02x%02x%02x".format(red, green, blue)
    }

}
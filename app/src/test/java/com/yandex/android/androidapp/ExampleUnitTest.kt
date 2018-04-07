package com.yandex.android.androidapp

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dateFormatTest() {
        val dateFormatString : String = "yyyy-MM-dd'T'HH:mm:ssXXX"
        val string = "2017-04-24T12:00:00+05:00"

        val dateFormat = SimpleDateFormat(dateFormatString, Locale.getDefault())
        val date = dateFormat.parse(string)

        assertEquals(123, date)

    }

}

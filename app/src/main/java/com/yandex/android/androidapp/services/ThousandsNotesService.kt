package com.yandex.android.androidapp.services

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.yandex.android.androidapp.*
import java.util.*

class ThousandsNotesService : IntentService("ThousandsNotes") {

    var mNotifyManager: NotificationManager? = null
    var mNotifyBuilder: NotificationCompat.Builder? = null

    val mTag = "ThousandsNotesService"

    override fun onCreate() {
        Log.d(mTag, "onCreate()")
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {

        val databaseHelper = NotesDatabaseHelper(this)
        writeNotes(databaseHelper)
        sendResponse()
    }

    private fun writeNotes(dbHelper: NotesDatabaseHelper) {
        val id = 123123
        Log.d(mTag, "onHandleIntent()")
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyBuilder = NotificationCompat.Builder(this, "CHANNEL_ID")



        mNotifyBuilder?.setContentTitle("Write Notes")
        mNotifyBuilder?.setContentText("Write in Progress")
        mNotifyBuilder?.setSmallIcon(R.drawable.ic_alert)
        mNotifyBuilder?.setProgress(100, 0, false)
        for (i in 1..50) {
            writeNote(dbHelper, i)
            mNotifyBuilder?.setProgress(100, i, false)
            mNotifyManager?.notify(id, mNotifyBuilder?.build())
            Thread.sleep(500)
            Log.d(mTag, i.toString())
        }

        mNotifyBuilder?.setContentText("Download complete")
                ?.setProgress(0,0,false)
        mNotifyManager?.notify(id, mNotifyBuilder?.build())


    }

    private fun writeNote(dbHelper: NotesDatabaseHelper, index: Int) {
        val note = createNote(index)
        dbHelper.addNotes(listOf(note))
    }

    private fun createNote(index: Int) : Note {
        val id = UUID.randomUUID().toString()
        val title = "Title: $index"
        val desciption = "description: $index"
        val noteDate = Calendar.getInstance().time

        return Note(id, title, desciption,
                color = Color.RED,
                timeCreate = noteDate,
                timeEdit = noteDate,
                timeView = noteDate)
    }

    override fun onDestroy() {
        Log.d(mTag, "onDestroy()")
        super.onDestroy()
    }


    private fun sendResponse() {
        Log.d(mTag, "sendResponse()")
        val responseIntent = Intent()

        responseIntent.action = ACTION_UPDATE
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
        responseIntent.putExtra(EXTRA_THOUSANDS_NOTES, true)
        sendBroadcast(responseIntent)

    }


}
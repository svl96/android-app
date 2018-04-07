package com.yandex.android.androidapp

import android.content.res.Configuration
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.yandex.android.androidapp.fragments.EditFragment
import com.yandex.android.androidapp.fragments.ListNotesFragment
import android.view.inputmethod.InputMethodManager
import com.yandex.android.androidapp.fragments.AboutAppFragment
import com.yandex.android.androidapp.fragments.SettingsFragment


const val EXTRA_NOTE : String = "com.yandex.android.EXTRA_NOTE"
const val EXTRA_EDIT_MODE : String = "com.yandex.android.EXTRA_EDIT_MODE"
const val EXTRA_COLOR : String = "com.yandex.android.EXTRA_COLOR"
const val DEFAULT_COLOR : Int = Color.RED
const val GET_NOTE_REQUEST : Int = 1
const val EDIT_NOTE_REQUEST : Int = 2
const val GET_COLOR_REQUEST : Int = 3

class MainActivity : AppCompatActivity(), ContainerUI, NotesContainer  {

    private val tag = "MainActivity"

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var _notes : Array<Note> = arrayOf()
    private var databaseHelper : NotesDatabaseHelper? = null

    // region Setup Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainTesting", "onCreate()")

        databaseHelper = NotesDatabaseHelper(this)
        uploadFromDatabase()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.nav_view)

        if (savedInstanceState == null) {
            val fragment = ListNotesFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, "ListNotesFragment")
                    .commit()
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawerLayout = findViewById(R.id.drawer_layout)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        setupNavigation()
    }
    // endregion

    private fun setupNavigation() {
        navigationView?.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home_item -> onHomeSelected()
                R.id.settings_item -> onSettingsSelected()
                R.id.about_item -> onAboutAppSelected()
                else -> true
            }
        }
    }

    private fun openSelectedFragment(tag: String, fragment: Fragment) {
        if (!fragment.isVisible) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit()
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
    }

    private fun onHomeSelected() : Boolean {
        val tag = "ListNotesFragment"
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null)
            fragment = ListNotesFragment.newInstance()

        openSelectedFragment(tag, fragment)
        return true
    }

    private fun onSettingsSelected() : Boolean {
        val tag = "SettingsFragment"
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null)
            fragment = SettingsFragment.newInstance()

        openSelectedFragment(tag, fragment)
        return true
    }

    private fun onAboutAppSelected() : Boolean {
        val tag = "AboutAppFragment"
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null)
            fragment = AboutAppFragment.newInstance()

        openSelectedFragment(tag, fragment)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        closeKeyboard()
        if (item?.itemId == 16908332)
            drawerLayout?.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }

    override fun setActivityTitle(titleId: Int) {
        setTitle(titleId)
    }

    override fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true)
            drawerLayout?.closeDrawer(GravityCompat.START)
        else {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment is EditFragment) {
                fragment.onBackPressed()
            }
            else {
                super.onBackPressed()
            }
        }
    }

    private fun uploadFromDatabase()  {
        _notes = databaseHelper?.getAllNotes() ?: arrayOf()
    }

    override fun getNotes(): Array<Note> {
        Log.d(tag, "getNotes()")
        Log.d(tag, _notes.contentDeepToString())

        return _notes
    }

    override fun deleteNote(note: Note): Boolean {
        val deleteRes = databaseHelper?.deleteNote(note)
        uploadFromDatabase()

        return deleteRes != 0
    }

    override fun updateNote(note: Note): Boolean {
        val updateRes = databaseHelper?.updateNote(note)
        uploadFromDatabase()

        return  updateRes != 0
    }

    override fun addNote(note: Note) {
        databaseHelper?.addNotes(listOf(note))
        uploadFromDatabase()
    }
}

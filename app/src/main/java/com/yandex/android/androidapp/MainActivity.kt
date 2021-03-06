package com.yandex.android.androidapp

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.yandex.android.androidapp.fragments.*
import java.util.*

const val APP_PREFERENCES = "mysettings"
const val SHARED_SORT_BY = "SHARED_SORT_BY"
const val SHARED_SORT_ORDER = "SHARED_SORT_ORDER"
const val SHARED_FILTER_BY = "SHARED_FILTER_BY"
const val SHARED_FILTER_DATE = "SHARED_FILTER_DATE"
const val SHARED_FILTER_ENABLE = "SHARED_FILTER_ENABLE"
const val GET_NOTE_REQUEST : Int = 1
const val EDIT_NOTE_REQUEST : Int = 2
const val GET_COLOR_REQUEST : Int = 3

const val ACTION_UPDATE : String = "ACTION_UPDATE"

private val REQUEST_EXTERNAL_STORAGE = 1
private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity(), ContainerUI  {

    private val tag = "MainActivity"

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var notesContainer: NotesContainer? = null
    private var databaseHelper : NotesDatabaseHelper? = null
    private var sharedPreferences : SharedPreferences? = null
    private var databaseFragment : DatabaseFragment? = null


    // region Setup Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainTesting", "onCreate()")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        databaseHelper = NotesDatabaseHelper(this)
        navigationView = findViewById(R.id.nav_view)

        setupDatabaseFragment()

        notesContainer = NotesContainer(databaseHelper!!, databaseFragment!!)
        setupNewNotesContainer()
        updateData()

        if (savedInstanceState == null) {
            val fragment = ListNotesFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, ListNotesFragment.FRAGMENT_TAG)
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


    private fun setupDatabaseFragment() {
        databaseFragment = supportFragmentManager.findFragmentByTag(DatabaseFragment.FRAGMENT_TAG)
                as DatabaseFragment?

        if (databaseFragment == null) {
            databaseFragment = DatabaseFragment.newInstance("type")

            supportFragmentManager
                    .beginTransaction()
                    .add(databaseFragment, DatabaseFragment.FRAGMENT_TAG)
                    .commit()
        }
    }

    // region  Navigation

    private fun setupNavigation() {
        navigationView?.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home_item -> onHomeSelected()
                R.id.settings_item -> onSettingsSelected()
                R.id.import_export_item -> onImportExportSelected()
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
        var fragment = supportFragmentManager.findFragmentByTag(ListNotesFragment.FRAGMENT_TAG)
        if (fragment == null)
            fragment = ListNotesFragment.newInstance()

        openSelectedFragment(tag, fragment)
        return true
    }

    private fun onImportExportSelected(): Boolean {
        verifyStoragePermissions(this)
        var fragment = supportFragmentManager.findFragmentByTag(ImportExportFragment.FRAGMENT_TAG)
        if (fragment == null)
            fragment = ImportExportFragment.newInstance()

        openSelectedFragment(tag, fragment)
        return true
    }

    private fun onSettingsSelected() : Boolean {
        var fragment = supportFragmentManager.findFragmentByTag(SettingsFragment.FRAGMENT_TAG)
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

    // endregion

    private fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
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

    private fun getTimeColumn(sharedKey: String, defaultColumn: String) : String {
        return when(sharedPreferences?.getString(sharedKey, "")) {
            getString(R.string.by_edit_time_text) -> NotesDatabaseHelper.COLUMN_EDIT_TIME
            getString(R.string.by_create_time_text) -> NotesDatabaseHelper.COLUMN_CREATE_TIME
            getString(R.string.by_view_time_text) -> NotesDatabaseHelper.COLUMN_VIEW_TIME
            else -> defaultColumn
        }
    }

    override fun getNotesContainer(): NotesContainer {
        return notesContainer!!
    }

    private fun setupNewNotesContainer() {
        val sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val sortByColumn = getTimeColumn(SHARED_SORT_BY, NotesDatabaseHelper.DEFAULT_SORT_COLUMN)
        val filterByColumn = getTimeColumn(SHARED_FILTER_BY, NotesDatabaseHelper.DEFAULT_FILTER_COLUMN)

        val sortOrderParam = when(sharedPreferences?.getString(SHARED_SORT_ORDER, "")) {
            getString(R.string.ascent_text) -> NotesDatabaseHelper.ASCENT_SORT_ORDER
            getString(R.string.descent_text) -> NotesDatabaseHelper.DESCENT_SORT_ORDER
            else -> NotesDatabaseHelper.DEFAULT_SORT_ORDER
        }

        val filterEnable = sharedPreferences?.getBoolean(SHARED_FILTER_ENABLE, false) ?: false

        val date  = sharedPreferences?.getLong(SHARED_FILTER_DATE,
                -1) ?: -1

        val filterParam = if (filterEnable && date > 0 ) Calendar.getInstance() else null
        filterParam?.time = Date(date)

        notesContainer?.setSelectParams(filterParam, filterParam,
                sortByColumn, filterByColumn, sortOrderParam)
    }

    override fun updateData() {
        Log.d(tag, "updateData")
        if (notesContainer == null) {
            notesContainer = NotesContainer(databaseHelper!!, databaseFragment!!)
        }
        setupNewNotesContainer()
        notesContainer?.refreshDataAsync()
    }

    override fun updateDataCallback(items: Array<Note>) {
        if (notesContainer == null) {
            notesContainer = NotesContainer(databaseHelper!!, databaseFragment!!)
        }
        notesContainer?.setNotes(items)
        Log.d(tag, "updateDataCallback")
        val listFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (listFragment is ListNotesFragment) {
            listFragment.updateListNotes()
        }
    }

}

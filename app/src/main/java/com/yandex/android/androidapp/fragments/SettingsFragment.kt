package com.yandex.android.androidapp.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.yandex.android.androidapp.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    companion object {
        const val FRAGMENT_TAG = "SettingsFragment"

        @JvmStatic
        fun newInstance() : SettingsFragment {
            return SettingsFragment()
        }
    }

    private var containerUi: ContainerUI? = null
    private val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val disableAlpha = 0.2f
    private val enableAlpha = 1f


    override fun onAttach(context: Context?) {

        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_settings, container, false)!!
        containerUi?.setActivityTitle(R.string.settings_title)

        val sp = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        setupSortBySettings(rootView, sp)

        setupSortOrderSettings(rootView, sp)

        setupFilterCheckerSettings(rootView, sp)

        return rootView
    }

    private fun setupSortBySettings(rootView: View, sp : SharedPreferences) {
        val sortBySettings = rootView.findViewById<ConstraintLayout>(R.id.sort_settings)
        val sortByValueTextView = sortBySettings.findViewById<TextView>(R.id.sort_setting_config)
        sortByValueTextView.text = sp.getString(SHARED_SORT_BY,
                resources.getString(R.string.by_edit_time_text))

        sortBySettings.setOnClickListener {
            showPopupMenu(it, R.menu.sortby_menu, sortByValueTextView, SHARED_SORT_BY) }
    }

    private fun setupSortOrderSettings(rootView: View, sp : SharedPreferences) {
        val sortOrderSettings = rootView.findViewById<ConstraintLayout>(R.id.sort_order)
        val sortOrderValueTextView = sortOrderSettings.findViewById<TextView>(R.id.sort_order_value)
        sortOrderValueTextView.text = sp.getString(SHARED_SORT_ORDER,
                resources.getString(R.string.descent_text))

        sortOrderSettings.setOnClickListener {
            showPopupMenu(it, R.menu.sort_order, sortOrderValueTextView, SHARED_SORT_ORDER) }
    }

    private fun setupFilterCheckerSettings(rootView: View, sp: SharedPreferences) {

        val filterEnable = sp.getBoolean(SHARED_FILTER_ENABLE, false)

        val switcher : SwitchCompat = rootView.findViewById(R.id.filter_switch)
        switcher.isChecked = filterEnable

        setupFilterSettings(rootView, sp, filterEnable)

        switcher.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            writeInShared(SHARED_FILTER_ENABLE, b)
            setupFilterSettings(rootView, sp, b)
        }
    }

    private fun setupFilterSettings(rootView: View, sp: SharedPreferences, filterEnable: Boolean) {
        val sharedDate = sp.getLong(SHARED_FILTER_DATE, -1)

        val filterSettings = rootView.findViewById<ConstraintLayout>(R.id.filter_settings)
        val filterValue = filterSettings.findViewById<TextView>(R.id.filter_at_value)
        filterValue.text = if (sharedDate > 0 ) simpleDateFormat.format(sharedDate) else ""

        val filterBySettings = rootView.findViewById<ConstraintLayout>(R.id.filter_by_settings)
        val filterByValueTextView = filterBySettings.findViewById<TextView>(R.id.filter_by_value)
        filterByValueTextView.text = sp.getString(SHARED_FILTER_BY,
                resources.getString(R.string.by_edit_time_text))


        if (filterEnable) {
            filterSettings.alpha = enableAlpha
            filterBySettings.alpha = enableAlpha
            val datePickerDialog = getDatePickerDialog(filterValue)
            filterSettings.setOnClickListener {
                datePickerDialog.show()
            }
            filterBySettings.setOnClickListener {
                showPopupMenu(it, R.menu.sortby_menu, filterByValueTextView, SHARED_FILTER_BY) }

        } else {
            filterSettings.alpha = disableAlpha
            filterBySettings.alpha = disableAlpha
            filterSettings.setOnClickListener{  }
            filterBySettings.setOnClickListener { }
        }
    }

    private fun getDatePickerDialog(resultTextView: TextView) : DatePickerDialog{

        val calendar = Calendar.getInstance()
        val onDateSetListener = DatePickerDialog.OnDateSetListener {
            _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            val date = simpleDateFormat.format(calendar.time)
            resultTextView.text = date
            writeInShared(SHARED_FILTER_DATE, calendar.time.time)
        }

        return DatePickerDialog(context, onDateSetListener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

    }

    private fun writeInShared(key: String, value: Any){
        val shared = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val editor = shared.edit()
        when(value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
            is Long -> editor.putLong(key, value)
            else -> {}
        }
        editor.apply()
    }


    private fun showPopupMenu(view: View, menuId: Int, textView: TextView, sharedKey : String ) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(menuId)

        popupMenu.setOnMenuItemClickListener {
            textView.text = it.title
            writeInShared(sharedKey, it.title.toString())
            true
            }
        popupMenu.show()
    }

    override fun onDestroyView() {
        containerUi?.updateData()
        super.onDestroyView()
    }

}
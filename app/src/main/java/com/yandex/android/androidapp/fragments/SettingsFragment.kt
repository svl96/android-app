package com.yandex.android.androidapp.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.yandex.android.androidapp.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() : SettingsFragment {
            return SettingsFragment()
        }
    }

    private var containerUi: ContainerUI? = null

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

        val sortBySettings = rootView.findViewById<ConstraintLayout>(R.id.sort_settings)
        val sortByValueTextView = sortBySettings.findViewById<TextView>(R.id.sort_setting_config)
        sortByValueTextView.text = sp.getString(SHARED_SORT_BY,
                resources.getString(R.string.by_edit_time_text))

        sortBySettings.setOnClickListener {
            showPopupMenu(it, R.menu.sortby_menu, sortByValueTextView, SHARED_SORT_BY) }

        val sortOrderSettings = rootView.findViewById<ConstraintLayout>(R.id.sort_order)
        val sortOrderValueTextView = sortOrderSettings.findViewById<TextView>(R.id.sort_order_value)
        sortOrderValueTextView.text = sp.getString(SHARED_SORT_ORDER,
                resources.getString(R.string.descent_text))

        sortOrderSettings.setOnClickListener {
            showPopupMenu(it, R.menu.sort_order, sortOrderValueTextView, SHARED_SORT_ORDER) }

        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val sharedDate = sp.getLong(SHARED_FILTER_DATE, -1)

        val filterSettings = rootView.findViewById<ConstraintLayout>(R.id.filter_settings)
        val filterValue = filterSettings.findViewById<TextView>(R.id.filter_by_value)
        filterValue.text = if (sharedDate > 0 ) simpleDateFormat.format(sharedDate) else ""


        val onDateSetListener = DatePickerDialog.OnDateSetListener {
            _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            val date = simpleDateFormat.format(calendar.time)
            filterValue.text = date
            writeLongInShared(SHARED_FILTER_DATE, calendar.time.time)
        }

        val datePickerDialog = DatePickerDialog(context, onDateSetListener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])


        filterSettings.setOnClickListener {
            datePickerDialog.show()
        }

        return rootView
    }

    private fun writeLongInShared(key: String, value: Long){
        val shared = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        shared.edit()
                .putLong(key, value)
                .apply()
    }

    private fun writeStringInShared(key: String, value: String){
        val shared = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        shared.edit()
                .putString(key, value)
                .apply()
    }

    private fun showPopupMenu(view: View, menuId: Int, textView: TextView, sharedKey : String ) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(menuId)

        popupMenu.setOnMenuItemClickListener {
            textView.text = it.title
            writeStringInShared(sharedKey, it.title.toString())
            true
            }
        popupMenu.show()
    }

    override fun onDestroyView() {
        containerUi?.updateData()
        super.onDestroyView()
    }

}
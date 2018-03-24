package com.yandex.android.androidapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NotesAdapter(private val _context: Context, private val _notes: ArrayList<Note>)
    : BaseAdapter() {

    override fun getItem(p0: Int): Any {
        return _notes[p0]
    }

    override fun getItemId(p0: Int): Long {
        return _notes[p0].id.toLong()
    }

    override fun getCount(): Int {
        return _notes.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var customView : View? = convertView
        var viewHolder : ViewHolder
        if (convertView == null) {
            customView = LayoutInflater.from(_context)
                    .inflate(R.layout.note_item, parent, false)
            viewHolder = ViewHolder(customView)
            customView?.tag = viewHolder
        } else {
            viewHolder = customView?.tag as ViewHolder
        }

        val item = getItem(position) as Note
        val dateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
        viewHolder.color?.setBackgroundColor(item.color)
        viewHolder.title?.text = item.title
        viewHolder.description?.text = item.description
        viewHolder.date?.text = dateFormat.format(item.datetime)

        return customView!!
    }

    private class ViewHolder(view: View?) {

        val title: TextView?
        val description: TextView?
        val color: View?
        val date: TextView?

        init {
            this.title = view?.findViewById(R.id.item_title)
            this.description = view?.findViewById(R.id.item_description)
            this.color = view?.findViewById(R.id.item_color)
            this.date = view?.findViewById(R.id.item_date)
        }
    }
}

package com.yandex.android.androidapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yandex.android.androidapp.fragments.ItemsContainer
import com.yandex.android.androidapp.Note
import com.yandex.android.androidapp.R
import java.text.SimpleDateFormat
import java.util.*


class RecyclerViewAdapter(private val itemsContainer: ItemsContainer<Note>)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)
            : ViewHolder {
        val view = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.note_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsContainer.getItems().size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val dateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
        val item = itemsContainer.getItems()[position]
        holder?.title?.text = item.title
        holder?.description?.text = item.description
        holder?.color?.setBackgroundColor(item.color)
        holder?.date?.text = dateFormat.format(item.timeCreate)

        holder?.onClickHandle = {
            itemsContainer.editItem(item)
        }

        holder?.onLongClickHandler = {
            itemsContainer.deleteItem(position)
        }

    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener,
            View.OnLongClickListener
    {
        val title: TextView? = itemView?.findViewById(R.id.item_title)
        val description: TextView? = itemView?.findViewById(R.id.item_description)
        val color: View? = itemView?.findViewById(R.id.item_color)
        val date: TextView? = itemView?.findViewById(R.id.item_date)
        var onClickHandle : () -> Unit = {}
        var onLongClickHandler : () -> Boolean = {false}

        init {
            itemView?.setOnClickListener(this)
            itemView?.setOnLongClickListener(this)
        }


        override fun onLongClick(p0: View?): Boolean {
            return onLongClickHandler()
        }

        override fun onClick(p0: View?) {
            onClickHandle()
        }

    }

}
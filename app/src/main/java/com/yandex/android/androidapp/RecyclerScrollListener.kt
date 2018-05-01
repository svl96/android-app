package com.yandex.android.androidapp

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.AbsListView

abstract class RecyclerScrollListener(private val manager: LinearLayoutManager, var currentPosition: Int)
    : RecyclerView.OnScrollListener() {

    private var isScrolling = false
    private var totalItemCount = 0
    private val startPosition = 0
    private var loading = true

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true
        }
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val currentItemCount = manager.itemCount
        val lastItemPosition = manager.findLastVisibleItemPosition()

        if (currentItemCount < totalItemCount) {
            currentPosition = startPosition
            totalItemCount = currentItemCount
            if (totalItemCount == 0)
                loading = true
        }

        if (loading && (currentItemCount > totalItemCount)) {
            loading = false
            totalItemCount = currentItemCount
        }

        if (!loading && (lastItemPosition + 5 > currentItemCount)) {
            currentPosition = lastItemPosition
            onLoad(currentPosition, totalItemCount, recyclerView)
            loading = true
        }
    }

    fun resetState() {
        currentPosition = startPosition
        totalItemCount = 0
        loading = true
    }

    abstract fun onLoad(currentPosition: Int, totalCount: Int, view: RecyclerView?)
}
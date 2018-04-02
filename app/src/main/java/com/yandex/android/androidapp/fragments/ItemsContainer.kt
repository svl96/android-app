package com.yandex.android.androidapp.fragments


interface ItemsContainer<Item> {

    fun getItems() : ArrayList<Item>

    fun createItem()

    fun editItem(item: Item)

    fun deleteItem(position: Int) : Boolean

}
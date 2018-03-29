package com.yandex.android.androidapp

import java.text.ParsePosition


interface ItemsContainer<Item> {

    fun getItems() : ArrayList<Item>

    fun createItem()

    fun editItem(item: Item)

    fun deleteItem(position: Int) : Boolean

}
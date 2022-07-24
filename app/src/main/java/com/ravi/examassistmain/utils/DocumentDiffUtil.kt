package com.ravi.examassistmain.utils

import androidx.recyclerview.widget.DiffUtil

class DocumentDiffUtil<T>(
    private val oldList: List<T>?,
    private val newList: List<T>?
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList?.size?:0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?:0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition) === newList?.get(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition) == newList?.get(newItemPosition)
    }
}
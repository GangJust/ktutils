package com.freegang.ktutils.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class KRecyclerAdapter<RV : RecyclerView.ViewHolder, E : IRecyclerAdapter.Item> :
    RecyclerView.Adapter<RV>(), IRecyclerAdapter<E> {
    private val mItems = mutableListOf<E>()

    private fun checkIndex(index: Int) {
        require(index in 0 until mItems.size) { "Index out of range: $index, items size: ${mItems.size}" }
    }

    // add
    override fun addItem(item: E) {
        val oldSize = mItems.size
        mItems.add(item)
        notifyItemInserted(oldSize)
    }

    override fun addItem(index: Int, item: E) {
        checkIndex(index)
        mItems.add(index, item)
        notifyItemInserted(index)
    }

    override fun addItems(items: List<E>): Boolean {
        val oldSize = mItems.size
        return mItems.addAll(items).also {
            notifyItemRangeInserted(oldSize, items.size)
        }
    }

    override fun addItems(vararg items: E): Boolean {
        return addItems(items.toList())
    }

    override fun addItems(index: Int, items: List<E>): Boolean {
        return mItems.addAll(index, items).also {
            notifyItemRangeInserted(index, items.size)
        }
    }

    override fun addItems(index: Int, vararg items: E): Boolean {
        return addItems(index, items.toList())
    }

    // remove
    override fun removeItem(index: Int) {
        checkIndex(index)
        mItems.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun removeItem(item: E): Boolean {
        val index = mItems.indexOf(item)
        if (index != -1) {
            mItems.removeAt(index)
            notifyItemRemoved(index)
            return true
        }
        return false
    }

    override fun removeRange(first: Int, last: Int) {
        if (first >= 0 && last < mItems.size) {
            mItems.subList(first, last + 1).clear()
            notifyItemRangeRemoved(first, last - first + 1)
        }
    }

    override fun removeRange(range: IntRange) {
        removeRange(range.first, range.last)
    }

    override fun removeAll() {
        val oldSize = mItems.size
        mItems.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    override fun clear() {
        removeAll()
    }

    // update
    override fun update(index: Int) {
        checkIndex(index)
        notifyItemChanged(index)
    }

    override fun update(first: Int, last: Int) {
        if (first >= 0 && last < mItems.size) {
            notifyItemRangeChanged(first, last - first + 1)
        }
    }

    override fun update(range: IntRange) {
        update(range.first, range.last)
    }

    override fun update(index: Int, item: E) {
        if (index >= 0 && index < mItems.size) {
            mItems[index] = item
            notifyItemChanged(index)
        }
    }

    override fun update(item: E, newItem: E): Boolean {
        val index = mItems.indexOf(item)
        if (index != -1) {
            mItems[index] = newItem
            notifyItemChanged(index)
            return true
        }
        return false
    }

    override fun update(first: Int, last: Int, items: List<E>): Boolean {
        if (first < 0 || last >= mItems.size || first > last) {
            return false
        }

        val oldSize = mItems.size
        val newSize = first + items.size + oldSize - 1 - last
        val replacedSize = minOf(items.size, last - first + 1)
        val addedSize = maxOf(0, items.size - replacedSize)

        // Replace and/or add elements
        for (i in items.indices) {
            if (first + i < oldSize) {
                mItems[first + i] = items[i]
            } else {
                mItems.add(items[i])
            }
        }

        // Remove excess elements
        while (mItems.size > newSize) {
            mItems.removeAt(mItems.size - 1)
        }

        // Notify changes
        notifyItemRangeChanged(first, replacedSize)
        if (addedSize > 0) {
            notifyItemRangeInserted(first + replacedSize, addedSize)
        }
        if (newSize < oldSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }

        return true
    }

    override fun update(first: Int, last: Int, vararg items: E): Boolean {
        return update(first, last, items.toList())
    }

    override fun update(range: IntRange, items: List<E>): Boolean {
        return update(range.first, range.last, items)
    }

    override fun update(range: IntRange, vararg items: E): Boolean {
        return update(range.first, range.last, items.toList())
    }

    // query
    override fun getItem(position: Int): E? {
        checkIndex(position)
        return mItems[position]
    }

    override fun getItems(): List<E> {
        return mItems
    }

    override fun getSubsetItems(range: IntRange): List<E> {
        return mItems.subList(maxOf(0, range.first), minOf(mItems.size, range.last + 1))
            .toList()
    }

    override fun getSubsetItems(first: Int, last: Int): List<E> {
        return mItems.subList(maxOf(0, first), minOf(mItems.size, last + 1))
            .toList()
    }

    // count
    override fun getItemCount(): Int {
        return mItems.size
    }
}
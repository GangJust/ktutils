package com.freegang.ktutils.adapter

interface IRecyclerAdapter<E> {

    // add
    /**
     * 添加一个项目到列表的末尾。
     * Add an item to the end of the list.
     */
    fun addItem(item: E)

    /**
     * 在指定的位置添加一个项目。
     * Add an item at the specified position.
     */
    fun addItem(index: Int, item: E)

    /**
     * 添加一个项目列表到列表的末尾。
     * Add a list of items to the end of the list.
     */
    fun addItems(items: List<E>): Boolean

    /**
     * 添加一个或多个项目到列表的末尾。
     * Add one or more items to the end of the list.
     */
    fun addItems(vararg items: E): Boolean

    /**
     * 在指定的位置添加一个项目列表。
     * Add a list of items at the specified position.
     */
    fun addItems(index: Int, items: List<E>): Boolean

    /**
     * 在指定的位置添加一个或多个项目。
     * Add one or more items at the specified position.
     */
    fun addItems(index: Int, vararg items: E): Boolean

    // remove
    /**
     * 根据索引从列表中移除一个项目。
     * Remove an item from the list by index.
     */
    fun removeItem(index: Int)

    /**
     * 从列表中移除一个项目。
     * Remove an item from the list.
     */
    fun removeItem(item: E): Boolean

    /**
     * 从列表中移除一定范围的项目。
     * Remove a range of items from the list.
     */
    fun removeRange(first: Int, last: Int)

    /**
     * 根据给定的范围从列表中移除项目。
     * Remove items from the list within the given range.
     */
    fun removeRange(range: IntRange)

    /**
     * 移除列表中的所有项目。
     * Remove all items from the list.
     */
    fun removeAll()

    /**
     * 清空列表。
     * Clear the list.
     */
    fun clear()

    // update
    /**
     * 更新列表中指定位置的项目。
     * Update the item at the specified position in the list.
     */
    fun update(index: Int)

    /**
     * 更新列表中一定范围的项目。
     * Update a range of items in the list.
     */
    fun update(first: Int, last: Int)

    /**
     * 根据给定的范围更新列表中的项目。
     * Update items in the list within the given range.
     */
    fun update(range: IntRange)

    /**
     * 更新列表中指定位置的项目。
     * Update the item at the specified position in the list.
     */
    fun update(index: Int, item: E)

    /**
     * 将列表中的一个项目更新为新的项目。
     * Update an item in the list to a new item.
     */
    fun update(item: E, newItem: E): Boolean

    /**
     * 将列表中一定范围的项目更新为新的项目列表。
     * Update a range of items in the list to a new list of items.
     */
    fun update(first: Int, last: Int, items: List<E>): Boolean

    /**
     * 将列表中一定范围的项目更新为新的一个或多个项目。
     * Update a range of items in the list to one or more new items.
     */
    fun update(first: Int, last: Int, vararg items: E): Boolean

    /**
     * 根据给定的范围将列表中的项目更新为新的项目列表。
     * Update items in the list within the given range to a new list of items.
     */
    fun update(range: IntRange, items: List<E>): Boolean

    /**
     * 根据给定的范围将列表中的项目更新为新的一个或多个项目。
     * Update items in the list within the given range to one or more new items.
     */
    fun update(range: IntRange, vararg items: E): Boolean

    // query
    /**
     * 获取列表中指定位置的项目。
     * Get the item at the specified position in the list.
     */
    fun getItem(position: Int): E?

    /**
     * 获取列表中的所有项目。
     * Get all items in the list.
     */
    fun getItems(): List<E>

    /**
     * 获取列表中给定范围的项目。
     * Get the items in the given range from the list.
     */
    fun getSubsetItems(range: IntRange): List<E>

    /**
     * 获取列表中一定范围的项目。
     * Get a range of items from the list.
     */
    fun getSubsetItems(first: Int, last: Int): List<E>

    // entity interfaces
    /**
     * 适配器列表项，所有Item都应该实现该接口。
     * adapter list items, all items should implement the interface.
     */
    interface Item {
        
    }
}

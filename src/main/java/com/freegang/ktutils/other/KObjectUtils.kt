package com.freegang.ktutils.other

object KObjectUtils {

    /**
     * 检查给定的元素是否存在于可变参数数组中。
     *
     * @param any 要检查的元素。
     * @param anys 要在其中查找的元素的数组。
     * @return 如果数组包含给定的元素，则返回true，否则返回false。
     */
    @JvmStatic
    fun anyEquals(any: Any, vararg anys: Any): Boolean {
        return anys.contains(any)
    }
}
package com.freegang.ktutils.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

fun interface FindMethodPredicate {
    fun predicate(method: Method): Boolean
}

fun interface FindFieldPredicate {
    fun predicate(field: Field): Boolean
}
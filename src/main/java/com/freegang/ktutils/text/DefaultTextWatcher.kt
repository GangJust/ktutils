package com.freegang.ktutils.text

import android.text.Editable
import android.text.TextWatcher

class DefaultTextWatcher : TextWatcher {
    private var mBeforeChanged: BeforeChanged? = null
    private var mOnChanged: OnChanged? = null
    private var mAfterChanged: AfterChanged? = null

    @JvmName("_watcher_")
    fun watcher(
        beforeChanged: BeforeChanged? = null,
        onChanged: OnChanged? = null,
        afterChanged: AfterChanged? = null,
    ): TextWatcher {
        this.mBeforeChanged = beforeChanged
        this.mOnChanged = onChanged
        this.mAfterChanged = afterChanged
        return this;
    }

    fun beforeChanged(beforeChanged: BeforeChanged): DefaultTextWatcher {
        this.mBeforeChanged = beforeChanged
        return this
    }

    fun onChange(onChanged: OnChanged): DefaultTextWatcher {
        this.mOnChanged = onChanged
        return this
    }

    fun afterChanged(afterChanged: AfterChanged): DefaultTextWatcher {
        this.mAfterChanged = afterChanged
        return this
    }

    @Deprecated("please use `beforeChanged`")
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mBeforeChanged?.beforeChanged(s, start, count, after)
    }

    @Deprecated("please use `onChange`")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mOnChanged?.onChanged(s, start, before, count)
    }

    @Deprecated("please use `afterChanged`")
    override fun afterTextChanged(s: Editable?) {
        mAfterChanged?.afterChanged(s)
    }

    fun interface BeforeChanged {
        fun beforeChanged(s: CharSequence?, start: Int, count: Int, after: Int)
    }

    fun interface OnChanged {
        fun onChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    }

    fun interface AfterChanged {
        fun afterChanged(s: Editable?)
    }
}
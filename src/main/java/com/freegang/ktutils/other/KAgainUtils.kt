package com.freegang.ktutils.other

class KAgainUtils private constructor(
    private val interval: Long,
) {
    private var lastHitTime = 0L

    /**
     * 命中事件监听
     */
    private var onHitListener: OnHitListenerImpl? = null

    /**
     * 命中事件, 两次命中间隔小于[interval]秒触发，常用场景：再按一次退出
     */
    fun hit() {
        if (System.currentTimeMillis() - lastHitTime < interval) {
            lastHitTime = 0L
            onHitListener?.againBlock?.invoke()
        } else {
            lastHitTime = System.currentTimeMillis()
            onHitListener?.hitBlock?.invoke()
        }
    }

    /**
     * 该事件只允许被设置一次
     */
    fun setOnHitListener(listener: OnHitListener.() -> Unit) {
        if (onHitListener != null) return
        val listenerImpl = OnHitListenerImpl()
        listener.invoke(listenerImpl)
        onHitListener = listenerImpl
    }

    interface OnHitListener {
        fun onHit(hit: () -> Unit)

        fun onHitAgain(again: () -> Unit)
    }

    private class OnHitListenerImpl : OnHitListener {
        var hitBlock: (() -> Unit)? = null
        var againBlock: (() -> Unit)? = null

        override fun onHit(hit: () -> Unit) {
            hitBlock = hit
        }

        override fun onHitAgain(again: () -> Unit) {
            againBlock = again
        }
    }

    companion object {
        fun newInstance(interval: Long = 2000L) = KAgainUtils(interval)
    }
}
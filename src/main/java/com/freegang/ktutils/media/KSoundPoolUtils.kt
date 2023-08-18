package com.freegang.ktutils.media

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import java.io.FileDescriptor

class KSoundPoolUtils private constructor() {
    private val mSoundCache: MutableMap<Any, Int> = mutableMapOf()

    fun clearCache(): KSoundPoolUtils {
        mSoundCache.clear()
        return this
    }

    fun addSound(afd: AssetFileDescriptor): KSoundPoolUtils {
        mSoundPool?.let { soundPool ->
            val soundId = soundPool.load(afd, 1)
            mSoundCache[afd] = soundId
        }
        return this
    }

    fun addSound(fd: FileDescriptor, offset: Long, length: Long): KSoundPoolUtils {
        mSoundPool?.let { soundPool ->
            val soundId = soundPool.load(fd, offset, length, 1)
            mSoundCache[fd] = soundId
        }
        return this
    }

    fun addSound(context: Context, @RawRes resId: Int): KSoundPoolUtils {
        mSoundPool?.let { soundPool ->
            val soundId = soundPool.load(context, resId, 1)
            mSoundCache[resId] = soundId
        }
        return this
    }

    fun addSound(path: String): KSoundPoolUtils {
        mSoundPool?.let { soundPool ->
            val soundId = soundPool.load(path, 1)
            mSoundCache[path] = soundId
        }
        return this
    }

    fun play() {
        mSoundPool?.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (status == 0) {
                mSoundCache.values.forEach { soundId ->
                    if (sampleId == soundId) {
                        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1f)
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_STREAMS = 5
        private var instance: KSoundPoolUtils? = null
        private var mSoundPool: SoundPool? = null

        @JvmStatic
        @JvmOverloads
        fun get(clear: Boolean = false): KSoundPoolUtils {
            if (instance == null) {
                synchronized(KSoundPoolUtils::class.java) {
                    if (instance == null) {
                        instance = KSoundPoolUtils().apply {
                            val soundPoolBuilder = SoundPool.Builder()
                                .setMaxStreams(MAX_STREAMS)
                                .setAudioAttributes(
                                    AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_GAME)
                                        .build()
                                )
                            mSoundPool = soundPoolBuilder.build()
                        }
                    }
                }
            }

            if (clear) {
                instance?.clearCache()
            }
            return instance!!
        }

        @JvmStatic
        fun release() {
            mSoundPool?.release()
            instance?.clearCache()

            mSoundPool = null
            instance = null
        }
    }
}
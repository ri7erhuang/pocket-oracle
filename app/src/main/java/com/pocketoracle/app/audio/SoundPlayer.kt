package com.pocketoracle.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.pocketoracle.app.R

/** 本地音效播放：木鱼敲击、计时铃声（res/raw 打包，无需联网）。 */
object SoundPlayer {

    private var pool: SoundPool? = null
    private var knockId = 0
    private var bellId = 0
    private val loaded = mutableSetOf<Int>()

    fun init(context: Context) {
        if (pool != null) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
        val app = context.applicationContext
        knockId = pool!!.load(app, R.raw.knock, 1)
        bellId = pool!!.load(app, R.raw.bell, 2)
        pool?.setOnLoadCompleteListener { _, sampleId, _ -> loaded.add(sampleId) }
    }

    fun knock() = play(knockId)

    fun bell() = play(bellId)

    private fun play(id: Int) {
        if (id == 0 || id !in loaded) return
        pool?.play(id, 0.9f, 0.9f, 1, 0, 1f)
    }

    fun release() {
        pool?.release()
        pool = null
        loaded.clear()
    }
}

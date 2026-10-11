package com.pocketoracle.app.data

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 计时状态放在进程级单例，页面切换 / 旋转屏幕时计时不中断。
 * 结束判定由页面的 tick 循环负责（到 0 播铃声）。
 */
object PomodoroState {

    enum class Phase { WORK, REST }

    var phase by mutableStateOf(Phase.WORK)
    var running by mutableStateOf(false)
    var endAt by mutableStateOf(0L)
    var remainMs by mutableStateOf(0L)
    var finished by mutableStateOf(false)

    fun start(totalMs: Long) {
        remainMs = totalMs
        endAt = SystemClock.elapsedRealtime() + totalMs
        running = true
        finished = false
    }

    fun pause() {
        if (!running) return
        remainMs = remaining()
        running = false
    }

    fun reset() {
        running = false
        remainMs = 0L
        finished = false
    }

    fun remaining(): Long =
        if (running) (endAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L) else remainMs

    fun onFinished() {
        running = false
        remainMs = 0L
        finished = true
    }

    fun switchPhase() {
        phase = if (phase == Phase.WORK) Phase.REST else Phase.WORK
        reset()
    }
}

object CountdownState {

    var running by mutableStateOf(false)
    var endAt by mutableStateOf(0L)
    var remainMs by mutableStateOf(0L)
    var totalMs by mutableStateOf(0L)
    var finished by mutableStateOf(false)

    fun start(ms: Long) {
        if (ms <= 0) return
        totalMs = ms
        remainMs = ms
        endAt = SystemClock.elapsedRealtime() + ms
        running = true
        finished = false
    }

    fun pause() {
        if (!running) return
        remainMs = remaining()
        running = false
    }

    fun reset() {
        running = false
        remainMs = 0L
        finished = false
    }

    fun remaining(): Long =
        if (running) (endAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L) else remainMs

    fun onFinished() {
        running = false
        remainMs = 0L
        finished = true
    }
}

/** 把毫秒格式化为 mm:ss 或 hh:mm:ss */
fun formatMs(ms: Long): String {
    val total = (ms + 999) / 1000
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

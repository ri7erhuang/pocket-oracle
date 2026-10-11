package com.pocketoracle.app.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * 摇一摇检测：加速度阈值 + 冷却时间防抖，避免轻微晃动误触发。
 */
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit,
) : SensorEventListener {

    private val manager =
        context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeAt = 0L
    private var registered = false

    private val threshold = 4.5f   // 超出重力加速度的幅度（m/s²）
    private val cooldownMs = 1000L // 触发后的冷却

    fun start() {
        if (registered || sensor == null) return
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        registered = true
        lastShakeAt = System.currentTimeMillis()
    }

    fun stop() {
        if (!registered) return
        manager.unregisterListener(this)
        registered = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        val (x, y, z) = event.values
        val magnitude = sqrt(x * x + y * y + z * z)
        val delta = magnitude - SensorManager.GRAVITY_EARTH
        val now = System.currentTimeMillis()
        if (delta > threshold && now - lastShakeAt > cooldownMs) {
            lastShakeAt = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

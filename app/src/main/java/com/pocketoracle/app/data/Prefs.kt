package com.pocketoracle.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 本地持久化：功德计数、MBTI 选择、壁纸、番茄配置、旋转开关、运势刷新盐。
 */
object Prefs {
    private const val FILE = "pocket_oracle"

    private lateinit var sp: SharedPreferences

    fun init(context: Context) {
        if (::sp.isInitialized) return
        sp = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    }

    var woodfishCount: Long
        get() = sp.getLong("woodfish_count", 0L)
        set(value) = sp.edit().putLong("woodfish_count", value).apply()

    var mbtiType: String
        get() = sp.getString("mbti_type", "INTJ") ?: "INTJ"
        set(value) = sp.edit().putString("mbti_type", value).apply()

    var wallpaper: Int
        get() = sp.getInt("wallpaper", 0)
        set(value) = sp.edit().putInt("wallpaper", value).apply()

    var pomodoroWorkMin: Int
        get() = sp.getInt("pomodoro_work", 25)
        set(value) = sp.edit().putInt("pomodoro_work", value).apply()

    var pomodoroRestMin: Int
        get() = sp.getInt("pomodoro_rest", 5)
        set(value) = sp.edit().putInt("pomodoro_rest", value).apply()

    var rotationLock: Boolean
        get() = sp.getBoolean("rotation_lock", true)
        set(value) = sp.edit().putBoolean("rotation_lock", value).apply()

    /** 运势刷新盐：同一天点“刷新”会换一版，重启不丢。 */
    var fortuneSalt: Int
        get() = sp.getInt("fortune_salt", 0)
        set(value) = sp.edit().putInt("fortune_salt", value).apply()

    var fortuneDay: String
        get() = sp.getString("fortune_day", "") ?: ""
        set(value) = sp.edit().putString("fortune_day", value).apply()
}

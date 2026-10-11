package com.pocketoracle.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pocketoracle.app.audio.SoundPlayer
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.nav.AppNav
import com.pocketoracle.app.ui.theme.PocketTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Prefs.init(this)
        SoundPlayer.init(this)
        applyOrientation()
        setContent {
            PocketTheme {
                AppNav()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applyOrientation()
    }

    private fun applyOrientation() {
        requestedOrientation = if (Prefs.rotationLock) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) SoundPlayer.release()
    }
}

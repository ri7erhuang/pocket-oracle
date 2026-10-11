package com.pocketoracle.app.features

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.lock.LockHelper
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.components.Wallpaper
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper

/** 模块12 设置：锁屏 / 壁纸 / WIFI / 桌面 / 旋转。 */
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var rotationLock by remember { mutableStateOf(Prefs.rotationLock) }
    var wallpaperIndex by remember { mutableIntStateOf(Prefs.wallpaper) }
    var adminActive by remember { mutableStateOf(LockHelper.isActive(context)) }

    PageScaffold("设置", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionTitle("系统")

            SettingRow(
                title = "立即锁屏",
                subtitle = if (adminActive) "已启用设备管理员，点击即锁屏" else "需先启用设备管理员",
                onClick = {
                    if (LockHelper.isActive(context)) {
                        LockHelper.lockNow(context)
                    } else {
                        LockHelper.requestAdmin(context)
                    }
                },
            )

            SettingRow(
                title = "WIFI 设置",
                subtitle = "跳转系统 WIFI 页面",
                onClick = {
                    runCatching {
                        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }.onFailure {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                },
            )

            SettingRow(
                title = "桌面设置",
                subtitle = "跳转系统桌面 / 启动器设置",
                onClick = {
                    runCatching {
                        context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                    }.onFailure {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                },
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Paper)
                    .border(BorderStroke(2.dp, Ink), RectangleShape)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("重力旋转", style = MaterialTheme.typography.bodyMedium, color = Ink)
                    Text(
                        text = if (rotationLock) "已锁定竖屏" else "页面跟随重力旋转",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                    )
                }
                Switch(
                    checked = !rotationLock,
                    onCheckedChange = { followGravity ->
                        rotationLock = !followGravity
                        Prefs.rotationLock = rotationLock
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Paper,
                        checkedTrackColor = Ink,
                        uncheckedThumbColor = Ink,
                        uncheckedTrackColor = Paper,
                        uncheckedBorderColor = Ink,
                    ),
                )
            }

            Spacer(Modifier.height(6.dp))
            SectionTitle("壁纸（App 内页面背景）")

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Wallpaper.entries.forEachIndexed { index, wallpaper ->
                    val active = index == wallpaperIndex
                    Text(
                        text = wallpaper.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) Paper else Ink,
                        modifier = Modifier
                            .background(if (active) Ink else Paper)
                            .border(BorderStroke(2.dp, Ink), RectangleShape)
                            .clickable {
                                wallpaperIndex = index
                                Prefs.wallpaper = index
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            SectionTitle("关于")
            Text(
                text = "口袋先知 v1.0.0\n复刻趣味随身工具集 · 除蓝牙配对外全部离线运行\n文案、图标、音效均为本项目原创 / 开源素材",
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = "── $text",
        style = MaterialTheme.typography.titleMedium,
        color = Ink,
    )
}

@Composable
private fun SettingRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Paper)
            .border(BorderStroke(2.dp, Ink), RectangleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Ink)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = InkFaint)
        }
        Text(text = "›", style = MaterialTheme.typography.titleLarge, color = Ink)
    }
}

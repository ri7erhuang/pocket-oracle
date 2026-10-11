package com.pocketoracle.app.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Filter9Plus
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.nav.Routes
import com.pocketoracle.app.ui.components.WallpaperBackground
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class Feature(
    val route: String,
    val title: String,
    val desc: String,
    val icon: ImageVector,
)

private val features = listOf(
    Feature(Routes.ANSWERS, "答案之书", "遇事翻一页", Icons.AutoMirrored.Outlined.MenuBook),
    Feature(Routes.FORTUNE, "时势运势", "今日运势", Icons.Outlined.WbSunny),
    Feature(Routes.MBTI, "人格指南", "16型速查", Icons.Outlined.Face),
    Feature(Routes.TEN, "十以内", "1~10 随机", Icons.Outlined.Filter9Plus),
    Feature(Routes.WOODFISH, "木鱼", "敲击攒功德", Icons.Outlined.BackHand),
    Feature(Routes.COIN, "翻硬币", "正反二选一", Icons.Outlined.MonetizationOn),
    Feature(Routes.CLOCK, "时钟", "指针与数字", Icons.Outlined.Schedule),
    Feature(Routes.POMODORO, "番茄时钟", "专注一刻钟", Icons.Outlined.Alarm),
    Feature(Routes.COUNTDOWN, "计时器", "自由倒计时", Icons.Outlined.HourglassBottom),
    Feature(Routes.BLE, "蓝牙遥控", "BLE 按键", Icons.Outlined.Bluetooth),
    Feature(Routes.DICE, "掷骰子", "摇一摇出点", Icons.Outlined.Casino),
    Feature(Routes.SETTINGS, "设置", "壁纸与系统", Icons.Outlined.Settings),
)

@Composable
fun HomeScreen(onOpen: (String) -> Unit) {
    val today = LocalDate.now()
    val dateText = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA))
    val weekday = when (today.dayOfWeek.value) {
        1 -> "周一"; 2 -> "周二"; 3 -> "周三"; 4 -> "周四"
        5 -> "周五"; 6 -> "周六"; else -> "周日"
    }

    Box(Modifier.fillMaxSize().background(Paper)) {
        WallpaperBackground()
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
            ) {
                Text(
                    text = "口袋先知",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "POCKET ORACLE / 随身趣味工具集",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkFaint,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$dateText  $weekday · 离线可用",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink,
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(features) { feature ->
                    FeatureCard(feature) { onOpen(feature.route) }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    Column(
        Modifier
            .height(112.dp)
            .background(Paper.copy(alpha = 0.85f))
            .border(BorderStroke(2.dp, Ink), RectangleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            feature.icon,
            contentDescription = feature.title,
            tint = Ink,
            modifier = Modifier.size(26.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = feature.title,
            style = MaterialTheme.typography.titleMedium,
            color = Ink,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = feature.desc,
            style = MaterialTheme.typography.labelSmall,
            color = InkFaint,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

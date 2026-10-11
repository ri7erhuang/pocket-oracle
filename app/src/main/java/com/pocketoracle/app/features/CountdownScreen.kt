package com.pocketoracle.app.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.audio.SoundPlayer
import com.pocketoracle.app.data.CountdownState
import com.pocketoracle.app.data.formatMs
import com.pocketoracle.app.ui.components.ButtonStyle
import com.pocketoracle.app.ui.components.EInkButton
import com.pocketoracle.app.ui.components.EInkOutlineBox
import com.pocketoracle.app.ui.components.EInkStepper
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import kotlinx.coroutines.delay

/** 模块9 计时器：自定义时分秒倒计时。 */
@Composable
@Suppress("UNUSED_EXPRESSION")
fun CountdownScreen(onBack: () -> Unit) {
    var hours by remember { mutableIntStateOf(0) }
    var minutes by remember { mutableIntStateOf(5) }
    var seconds by remember { mutableIntStateOf(0) }
    var tick by remember { mutableLongStateOf(0L) }

    LaunchedEffect(CountdownState.running) {
        while (CountdownState.running) {
            delay(200L)
            tick += 1
            if (CountdownState.remaining() <= 0L) {
                SoundPlayer.bell()
                CountdownState.onFinished()
            }
        }
    }

    val totalMs = ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L
    val displayMs = when {
        CountdownState.finished -> 0L
        CountdownState.running -> CountdownState.remaining()
        CountdownState.remainMs > 0L -> CountdownState.remainMs
        else -> totalMs
    }
    tick // 订阅刷新
    val editing = !CountdownState.running

    PageScaffold("计时器", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = formatMs(displayMs),
                style = MaterialTheme.typography.displayLarge,
                color = Ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = when {
                    CountdownState.finished -> "时间到！"
                    CountdownState.running -> "倒计时中…"
                    else -> "设置时间后开始"
                },
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint,
            )

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EInkButton(
                    text = when {
                        CountdownState.running -> "暂停"
                        CountdownState.remainMs > 0L -> "继续"
                        else -> "开始"
                    },
                    enabled = totalMs > 0 || CountdownState.running || CountdownState.remainMs > 0,
                    onClick = {
                        if (CountdownState.running) {
                            CountdownState.pause()
                        } else {
                            val ms = if (CountdownState.remainMs > 0L) {
                                CountdownState.remainMs
                            } else totalMs
                            CountdownState.start(ms)
                        }
                    },
                )
                EInkButton(
                    text = "重置",
                    style = ButtonStyle.OUTLINE,
                    enabled = editing,
                    onClick = { CountdownState.reset() },
                )
            }

            Spacer(Modifier.height(28.dp))
            EInkOutlineBox {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    EInkStepper(
                        label = "小时",
                        valueText = hours.toString().padStart(2, '0'),
                        enabled = editing && CountdownState.remainMs == 0L,
                        onMinus = { hours = (hours - 1).coerceAtLeast(0) },
                        onPlus = { hours = (hours + 1).coerceAtMost(23) },
                    )
                    EInkStepper(
                        label = "分钟",
                        valueText = minutes.toString().padStart(2, '0'),
                        enabled = editing && CountdownState.remainMs == 0L,
                        onMinus = { minutes = (minutes - 1).coerceAtLeast(0) },
                        onPlus = { minutes = (minutes + 1).coerceAtMost(59) },
                    )
                    EInkStepper(
                        label = "秒",
                        valueText = seconds.toString().padStart(2, '0'),
                        enabled = editing && CountdownState.remainMs == 0L,
                        onMinus = { seconds = (seconds - 1).coerceAtLeast(0) },
                        onPlus = { seconds = (seconds + 1).coerceAtMost(59) },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "暂停后可继续；重置回到初始设置",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
        }
    }
}

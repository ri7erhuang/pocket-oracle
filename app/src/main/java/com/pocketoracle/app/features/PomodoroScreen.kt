package com.pocketoracle.app.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.audio.SoundPlayer
import com.pocketoracle.app.data.PomodoroState
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.data.formatMs
import com.pocketoracle.app.ui.components.ButtonStyle
import com.pocketoracle.app.ui.components.EInkButton
import com.pocketoracle.app.ui.components.EInkOutlineBox
import com.pocketoracle.app.ui.components.EInkStepper
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import kotlinx.coroutines.delay

/** 模块8 番茄时钟：开始 / 暂停 / 重置，结束本地铃声。 */
@Composable
@Suppress("UNUSED_EXPRESSION")
fun PomodoroScreen(onBack: () -> Unit) {
    var workMin by remember { mutableLongStateOf(Prefs.pomodoroWorkMin.toLong()) }
    var restMin by remember { mutableLongStateOf(Prefs.pomodoroRestMin.toLong()) }
    var tick by remember { mutableLongStateOf(0L) }

    LaunchedEffect(PomodoroState.running) {
        while (PomodoroState.running) {
            delay(200L)
            tick += 1
            if (PomodoroState.remaining() <= 0L) {
                SoundPlayer.bell()
                PomodoroState.onFinished()
            }
        }
    }

    val phase = PomodoroState.phase
    val totalMs = (if (phase == PomodoroState.Phase.WORK) workMin else restMin) * 60_000L
    val displayMs = when {
        PomodoroState.finished -> 0L
        PomodoroState.running -> PomodoroState.remaining()
        PomodoroState.remainMs > 0L -> PomodoroState.remainMs
        else -> totalMs
    }
    tick // 订阅刷新

    PageScaffold("番茄时钟", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (phase == PomodoroState.Phase.WORK) "专注中" else "休息中",
                style = MaterialTheme.typography.titleLarge,
                color = Ink,
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = formatMs(displayMs),
                style = MaterialTheme.typography.displayLarge,
                color = Ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = when {
                    PomodoroState.finished -> "时间到，休息一下"
                    PomodoroState.running -> "计时中…"
                    else -> "准备好就开始"
                },
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint,
            )

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (PomodoroState.finished) {
                    EInkButton(
                        text = if (phase == PomodoroState.Phase.WORK) "开始休息" else "开始专注",
                        onClick = { PomodoroState.switchPhase() },
                    )
                } else if (PomodoroState.running) {
                    EInkButton(text = "暂停", onClick = { PomodoroState.pause() })
                } else {
                    EInkButton(
                        text = if (PomodoroState.remainMs > 0L) "继续" else "开始",
                        onClick = { PomodoroState.start(totalMs - PomodoroState.remainMs) },
                    )
                }
                EInkButton(
                    text = "重置",
                    onClick = { PomodoroState.reset() },
                    style = ButtonStyle.OUTLINE,
                    enabled = !PomodoroState.running,
                )
            }

            Spacer(Modifier.height(28.dp))
            EInkOutlineBox {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    EInkStepper(
                        label = "工作时长",
                        valueText = "$workMin 分",
                        enabled = !PomodoroState.running,
                        onMinus = {
                            workMin = (workMin - 1).coerceAtLeast(1)
                            Prefs.pomodoroWorkMin = workMin.toInt()
                        },
                        onPlus = {
                            workMin = (workMin + 1).coerceAtMost(60)
                            Prefs.pomodoroWorkMin = workMin.toInt()
                        },
                    )
                    EInkStepper(
                        label = "休息时长",
                        valueText = "$restMin 分",
                        enabled = !PomodoroState.running,
                        onMinus = {
                            restMin = (restMin - 1).coerceAtLeast(1)
                            Prefs.pomodoroRestMin = restMin.toInt()
                        },
                        onPlus = {
                            restMin = (restMin + 1).coerceAtMost(30)
                            Prefs.pomodoroRestMin = restMin.toInt()
                        },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "配置会自动保存到本地",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
        }
    }
}

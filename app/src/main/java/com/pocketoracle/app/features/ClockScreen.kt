package com.pocketoracle.app.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 模块7 时钟：指针表盘 + 数字时间/日期，按系统时间自动校时。 */
@Composable
fun ClockScreen(onBack: () -> Unit) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000L)
        }
    }

    val timeText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.CHINA))
    val dateText = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA))
    val weekday = when (now.dayOfWeek.value) {
        1 -> "周一"; 2 -> "周二"; 3 -> "周三"; 4 -> "周四"
        5 -> "周五"; 6 -> "周六"; else -> "周日"
    }

    PageScaffold("时钟", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(260.dp)) {
                    val r = size.minDimension / 2f - 4.dp.toPx()
                    val c = center
                    drawCircle(Paper, radius = r, center = c)
                    drawCircle(Ink, radius = r, center = c, style = Stroke(3.dp.toPx()))

                    // 分钟刻度
                    for (m in 0 until 60) {
                        val angle = Math.toRadians((m * 6 - 90).toDouble())
                        val isHour = m % 5 == 0
                        val outer = r * if (isHour) 0.88f else 0.94f
                        val inner = r * if (isHour) 0.78f else 0.90f
                        drawLine(
                            Ink.copy(alpha = if (isHour) 1f else 0.5f),
                            start = Offset(
                                c.x + (inner * Math.cos(angle)).toFloat(),
                                c.y + (inner * Math.sin(angle)).toFloat(),
                            ),
                            end = Offset(
                                c.x + (outer * Math.cos(angle)).toFloat(),
                                c.y + (outer * Math.sin(angle)).toFloat(),
                            ),
                            strokeWidth = if (isHour) 3.dp.toPx() else 1.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }

                    fun hand(hourAngleDeg: Double, length: Float, width: Float, color: androidx.compose.ui.graphics.Color) {
                        val a = Math.toRadians(hourAngleDeg)
                        drawLine(
                            color,
                            start = c,
                            end = Offset(
                                c.x + (r * length * Math.cos(a)).toFloat(),
                                c.y + (r * length * Math.sin(a)).toFloat(),
                            ),
                            strokeWidth = width,
                            cap = StrokeCap.Round,
                        )
                    }

                    val hourAngle = (now.hour % 12) * 30 + now.minute * 0.5
                    val minuteAngle = now.minute * 6 + now.second * 0.1
                    val secondAngle = now.second * 6.0
                    hand(hourAngle, 0.50f, 6.dp.toPx(), Ink)
                    hand(minuteAngle, 0.72f, 4.dp.toPx(), Ink)
                    hand(secondAngle, 0.82f, 1.5.dp.toPx(), Ink.copy(alpha = 0.7f))
                    drawCircle(Ink, radius = 5.dp.toPx(), center = c)
                    drawCircle(Paper, radius = 2.dp.toPx(), center = c)
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = timeText,
                style = MaterialTheme.typography.headlineMedium,
                color = Ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "$dateText  $weekday",
                style = MaterialTheme.typography.bodyMedium,
                color = InkFaint,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "跟随系统时间自动校准",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
        }
    }
}


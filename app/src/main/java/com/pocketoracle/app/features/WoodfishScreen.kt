package com.pocketoracle.app.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.audio.SoundPlayer
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper
import kotlinx.coroutines.launch

/** 模块5 木鱼：敲击计数 + 本地音效，长按重置。 */
@Composable
fun WoodfishScreen(onBack: () -> Unit) {
    var count by remember { mutableLongStateOf(Prefs.woodfishCount) }
    val swing = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun knock() {
        SoundPlayer.knock()
        count += 1
        Prefs.woodfishCount = count
        scope.launch {
            swing.snapTo(0f)
            swing.animateTo(1f, tween(90, easing = LinearEasing))
            swing.animateTo(0f, tween(170, easing = LinearEasing))
        }
    }

    PageScaffold("木鱼", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "功德 +$count",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "长按木鱼重置计数",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
            Spacer(Modifier.height(24.dp))

            Box(
                Modifier.size(280.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { knock() },
                                onLongPress = {
                                    count = 0
                                    Prefs.woodfishCount = 0
                                },
                            )
                        },
                ) {
                    val w = size.width
                    val h = size.height
                    val stroke = 4.dp.toPx()
                    val r = minOf(w, h) * 0.30f
                    val cx = w / 2f
                    val cy = h * 0.56f

                    // 鱼身
                    drawCircle(Paper, radius = r, center = Offset(cx, cy))
                    drawCircle(Ink, radius = r, center = Offset(cx, cy), style = Stroke(stroke))
                    // 鱼口
                    drawLine(
                        Ink,
                        start = Offset(cx - r * 0.85f, cy - r * 0.10f),
                        end = Offset(cx + r * 0.85f, cy - r * 0.10f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                    // 底座
                    drawLine(
                        Ink,
                        start = Offset(cx - r * 0.55f, cy + r + 12.dp.toPx()),
                        end = Offset(cx + r * 0.55f, cy + r + 12.dp.toPx()),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        Ink,
                        start = Offset(cx - r * 0.55f, cy + r + 2.dp.toPx()),
                        end = Offset(cx - r * 0.55f, cy + r + 12.dp.toPx()),
                        strokeWidth = stroke,
                    )
                    drawLine(
                        Ink,
                        start = Offset(cx + r * 0.55f, cy + r + 2.dp.toPx()),
                        end = Offset(cx + r * 0.55f, cy + r + 12.dp.toPx()),
                        strokeWidth = stroke,
                    )

                    // 木槌：静止在球右侧，敲击落回球心
                    val pivot = Offset(cx + r * 1.1f, cy - r * 1.1f)
                    val restAngle = Math.toRadians(80.0)
                    val hitAngle = Math.toRadians(135.0)
                    val angle = restAngle + (hitAngle - restAngle) * swing.value
                    val len = r * 1.5f
                    val tip = Offset(
                        pivot.x + (len * Math.cos(angle)).toFloat(),
                        pivot.y + (len * Math.sin(angle)).toFloat(),
                    )
                    drawLine(Ink, pivot, tip, strokeWidth = stroke, cap = StrokeCap.Round)
                    drawCircle(Ink, radius = r * 0.16f, center = tip)
                    drawCircle(Ink, radius = r * 0.07f, center = pivot)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "点击木鱼敲击",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink,
            )
        }
    }
}

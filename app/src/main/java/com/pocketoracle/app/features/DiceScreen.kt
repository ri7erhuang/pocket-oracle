package com.pocketoracle.app.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.pocketoracle.app.sensor.ShakeDetector
import com.pocketoracle.app.ui.components.ButtonStyle
import com.pocketoracle.app.ui.components.EInkButton
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/** 模块11 掷骰子：点击或摇一摇，简单滚动动画。 */
@Composable
fun DiceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val rollAnim = remember { Animatable(0f) }

    var value by remember { mutableIntStateOf(1) }
    var rolling by remember { mutableStateOf(false) }

    val roll: () -> Unit = {
        if (!rolling) {
            rolling = true
            scope.launch {
                val steps = 10
                repeat(steps) {
                    value = Random.nextInt(1, 7)
                    rollAnim.snapTo(rollAnim.value + 36f)
                    delay(55L)
                }
                value = Random.nextInt(1, 7)
                val snapTo = ((rollAnim.value / 360f).toInt() + 1) * 360f
                rollAnim.animateTo(
                    targetValue = snapTo,
                    animationSpec = tween(160, easing = LinearEasing),
                )
                rolling = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val detector = ShakeDetector(context) { roll() }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> detector.start()
                Lifecycle.Event.ON_STOP -> detector.stop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            detector.stop()
        }
    }

    PageScaffold("掷骰子", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier
                    .size(170.dp)
                    .graphicsLayer {
                        rotationZ = rollAnim.value
                        scaleX = 1f
                        scaleY = 1f
                    }
                    .background(Paper, RectangleShape)
                    .border(BorderStroke(4.dp, Ink), RectangleShape)
                    .clickable(onClick = roll),
                contentAlignment = Alignment.Center,
            ) {
                DiceFace(value)
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = if (rolling) "滚动中…" else "点数 $value",
                style = MaterialTheme.typography.titleLarge,
                color = Ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "点击骰子或摇一摇手机",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EInkButton(text = "掷一次", onClick = roll, style = ButtonStyle.OUTLINE)
            }
        }
    }
}

@Composable
private fun DiceFace(value: Int) {
    val positions = when (value) {
        1 -> listOf(0.5f to 0.5f)
        2 -> listOf(0.25f to 0.25f, 0.75f to 0.75f)
        3 -> listOf(0.25f to 0.25f, 0.5f to 0.5f, 0.75f to 0.75f)
        4 -> listOf(0.25f to 0.25f, 0.75f to 0.25f, 0.25f to 0.75f, 0.75f to 0.75f)
        5 -> listOf(0.25f to 0.25f, 0.75f to 0.25f, 0.5f to 0.5f, 0.25f to 0.75f, 0.75f to 0.75f)
        else -> listOf(0.25f to 0.25f, 0.75f to 0.25f, 0.25f to 0.5f, 0.75f to 0.5f, 0.25f to 0.75f, 0.75f to 0.75f)
    }
    androidx.compose.foundation.Canvas(Modifier.size(110.dp)) {
        val r = 11.dp.toPx()
        positions.forEach { (fx, fy) ->
            drawCircle(
                color = Ink,
                radius = r,
                center = Offset(size.width * fx, size.height * fy),
            )
        }
    }
}

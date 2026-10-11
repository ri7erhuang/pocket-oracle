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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.launch
import kotlin.random.Random

/** 模块6 翻硬币：点击或摇一摇。 */
@Composable
fun CoinScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    var result by remember { mutableStateOf("正面") }
    var pending by remember { mutableStateOf("正面") }
    var flipping by remember { mutableStateOf(false) }

    val flip: () -> Unit = {
        if (!flipping) {
            flipping = true
            pending = if (Random.nextBoolean()) "正面" else "反面"
            scope.launch {
                val base = rotation.value
                rotation.animateTo(
                    targetValue = base + 720f,
                    animationSpec = tween(750, easing = LinearEasing),
                )
                result = pending
                flipping = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val detector = ShakeDetector(context) { flip() }
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

    val phase = ((rotation.value % 360f) + 360f) % 360f
    val showFront = phase < 90f || phase > 270f
    val face = when {
        flipping && showFront -> pending
        flipping -> if (pending == "正面") "反面" else "正面"
        else -> result
    }

    PageScaffold("翻硬币", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier
                    .size(190.dp)
                    .graphicsLayer {
                        rotationY = rotation.value
                        cameraDistance = 14f * density
                    }
                    .background(Paper, RoundedCornerShape(percent = 50))
                    .border(BorderStroke(4.dp, Ink), RoundedCornerShape(percent = 50))
                    .clickable(onClick = flip),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = face,
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(28.dp))
            Text(
                text = if (flipping) "……" else "结果：$result",
                style = MaterialTheme.typography.titleLarge,
                color = Ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "点击硬币或摇一摇手机",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EInkButton(text = "抛一次", onClick = flip, style = ButtonStyle.OUTLINE)
            }
        }
    }
}

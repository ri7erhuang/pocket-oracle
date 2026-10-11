package com.pocketoracle.app.features

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import kotlin.random.Random

/** 模块4 十以内：1~10 随机数 */
@Composable
fun TenScreen(onBack: () -> Unit) {
    var number by remember { mutableIntStateOf(Random.nextInt(1, 11)) }
    var pulse by remember { mutableIntStateOf(0) }
    val scale by animateFloatAsState(
        targetValue = if (pulse % 2 == 0) 1f else 1.18f,
        animationSpec = tween(160),
        label = "pulse",
    )

    PageScaffold("十以内", onBack) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    number = Random.nextInt(1, 11)
                    pulse += 1
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "点击屏幕 · 1 ~ 10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkFaint,
                )
            }
        }
    }
}

package com.pocketoracle.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper

/** 壁纸（App 内页面背景）预设，仿墨水屏纸纹。 */
enum class Wallpaper(val label: String) {
    PLAIN("素纸"),
    GRID("方格纸"),
    DOT("点阵纸"),
    HATCH("斜纹纸"),
    HALFTONE("网点纸");

    companion object {
        fun of(index: Int): Wallpaper = entries.getOrElse(index) { PLAIN }
    }
}

@Composable
fun WallpaperBackground(modifier: Modifier = Modifier) {
    val wallpaper = Wallpaper.of(Prefs.wallpaper)
    Canvas(modifier.fillMaxSize()) {
        val stroke = 1.dp.toPx()
        when (wallpaper) {
            Wallpaper.PLAIN -> Unit
            Wallpaper.GRID -> {
                val step = 28.dp.toPx()
                var x = 0f
                while (x <= size.width) {
                    drawLine(InkFaint.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, size.height), stroke)
                    x += step
                }
                var y = 0f
                while (y <= size.height) {
                    drawLine(InkFaint.copy(alpha = 0.25f), Offset(0f, y), Offset(size.width, y), stroke)
                    y += step
                }
            }
            Wallpaper.DOT -> {
                val step = 20.dp.toPx()
                var y = step / 2
                while (y < size.height) {
                    var x = step / 2
                    while (x < size.width) {
                        drawCircle(Ink.copy(alpha = 0.22f), radius = 1.2.dp.toPx(), center = Offset(x, y))
                        x += step
                    }
                    y += step
                }
            }
            Wallpaper.HATCH -> {
                val step = 16.dp.toPx()
                var i = -size.height
                while (i < size.width) {
                    drawLine(
                        Ink.copy(alpha = 0.14f),
                        Offset(i, 0f),
                        Offset(i + size.height, size.height),
                        stroke,
                    )
                    i += step
                }
            }
            Wallpaper.HALFTONE -> {
                val step = 26.dp.toPx()
                var row = 0
                var y = step / 2
                while (y < size.height) {
                    val offset = if (row % 2 == 0) 0f else step / 2
                    var x = step / 2 + offset
                    while (x < size.width) {
                        drawCircle(Ink.copy(alpha = 0.3f), radius = 3.dp.toPx(), center = Offset(x, y))
                        x += step
                    }
                    y += step
                    row++
                }
            }
        }
    }
}

/** 页面进入时的“墨水屏全刷”黑闪效果。 */
@Composable
private fun EInkFlash() {
    val anim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        anim.animateTo(0f, tween(durationMillis = 200, easing = LinearEasing))
    }
    if (anim.value > 0.01f) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = anim.value }
                .background(Ink)
                .zIndex(99f),
        )
    }
}

/**
 * 统一页面外壳：壁纸背景 + 顶栏（返回 + 标题）+ 内容 + 墨水屏刷新闪。
 */
@Composable
fun PageScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Paper)) {
        WallpaperBackground()
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .drawBehind {
                        drawLine(Ink, Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx())
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = Ink)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            content()
        }
        EInkFlash()
    }
}

enum class ButtonStyle { SOLID, OUTLINE }

/** 复古描边按钮：按下时向右下位移 2dp，模拟实体按键。 */
@Composable
fun EInkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonStyle = ButtonStyle.SOLID,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val dx = if (pressed) 2.dp else 0.dp
    val colors = MaterialTheme.colorScheme
    val solid = style == ButtonStyle.SOLID && enabled
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = dx.toPx()
                translationY = dx.toPx()
            }
            .background(if (solid) colors.primary else colors.background)
            .border(BorderStroke(2.dp, colors.outline), RectangleShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (solid) colors.onPrimary else colors.onBackground,
        )
    }
}

@Composable
fun EInkOutlineBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .border(BorderStroke(2.dp, Ink), RectangleShape)
            .background(Paper.copy(alpha = 0.6f))
            .padding(16.dp),
    ) { content() }
}

/** 步进器：标签 + [−] 数值 [+]。 */
@Composable
fun EInkStepper(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            StepButton("−", enabled, onMinus)
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .border(BorderStroke(2.dp, Ink), RectangleShape)
                    .background(Paper)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                )
            }
            StepButton("+", enabled, onPlus)
        }
    }
}

@Composable
private fun StepButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .background(if (enabled) Ink else InkFaint)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Paper,
        )
    }
}

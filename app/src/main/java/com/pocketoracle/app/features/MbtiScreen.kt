package com.pocketoracle.app.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.data.Mbti
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.ui.components.EInkOutlineBox
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper

/** 模块3 人格指南（MBTI 资料速查） */
@Composable
fun MbtiScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf(Prefs.mbtiType) }
    val type = remember(selected) { Mbti.find(selected) }

    PageScaffold("人格指南", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "选择你的类型（仅作资料查阅）",
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint,
            )
            Spacer(Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                userScrollEnabled = false,
            ) {
                items(Mbti.types) { item ->
                    val active = item.code == selected
                    Text(
                        text = item.code,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (active) Paper else Ink,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(if (active) Ink else Paper)
                            .border(BorderStroke(2.dp, Ink), RectangleShape)
                            .clickable {
                                selected = item.code
                                Prefs.mbtiType = item.code
                            }
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            EInkOutlineBox {
                Column {
                    Text(
                        text = "${type.code} · ${type.title}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Ink,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = type.intro,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "行事指南",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                    )
                    Spacer(Modifier.height(8.dp))
                    type.tips.forEachIndexed { index, tip ->
                        Text(
                            text = "${index + 1}. $tip",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink,
                            modifier = Modifier.padding(vertical = 3.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "已记住你的选择，下次进入直接显示",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }
    }
}

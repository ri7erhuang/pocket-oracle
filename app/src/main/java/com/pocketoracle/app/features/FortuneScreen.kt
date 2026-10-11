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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pocketoracle.app.data.Fortune
import com.pocketoracle.app.data.Prefs
import com.pocketoracle.app.ui.components.ButtonStyle
import com.pocketoracle.app.ui.components.EInkButton
import com.pocketoracle.app.ui.components.EInkOutlineBox
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 模块2 时势运势 */
@Composable
fun FortuneScreen(onBack: () -> Unit) {
    val today = LocalDate.now()
    val todayKey = today.format(DateTimeFormatter.BASIC_ISO_DATE)

    // 同一天内容固定；跨日期自动重置刷新盐
    if (Prefs.fortuneDay != todayKey) {
        Prefs.fortuneDay = todayKey
        Prefs.fortuneSalt = 0
    }

    var salt by remember { mutableIntStateOf(Prefs.fortuneSalt) }
    val result = remember(todayKey, salt) { Fortune.generate(today, salt) }

    PageScaffold("时势运势", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = today.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.CHINA)),
                    style = MaterialTheme.typography.bodySmall,
                    color = InkFaint,
                )
                EInkButton(
                    text = "刷新",
                    onClick = {
                        salt += 1
                        Prefs.fortuneSalt = salt
                    },
                    style = ButtonStyle.OUTLINE,
                )
            }
            Spacer(Modifier.height(16.dp))

            EInkOutlineBox {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = result.headline,
                        style = MaterialTheme.typography.titleLarge,
                        color = Ink,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            EInkOutlineBox {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FortuneRow("幸运色", result.luckyColor)
                    FortuneRow("幸运数", result.luckyNumber.toString())
                    FortuneRow("幸运物", result.luckyItem)
                    FortuneRow("宜", result.todo)
                    FortuneRow("忌", result.avoid)
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = result.note,
                style = MaterialTheme.typography.bodyMedium,
                color = InkFaint,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "运势仅供娱乐参考",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FortuneRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = "$label ·",
            style = MaterialTheme.typography.bodyMedium,
            color = InkFaint,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink,
        )
    }
}

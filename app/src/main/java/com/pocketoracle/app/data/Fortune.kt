package com.pocketoracle.app.data

import java.time.LocalDate
import kotlin.random.Random

/** 模块2 时势运势：按日期哈希生成，同一天固定，刷新可换“当日版本”。 */
object Fortune {

    data class Result(
        val date: LocalDate,
        val headline: String,
        val luckyColor: String,
        val luckyNumber: Int,
        val luckyItem: String,
        val todo: String,
        val avoid: String,
        val note: String,
    )

    private val headlines = listOf(
        "平川一日，宜稳步推进",
        "小有波澜，宜静观其变",
        "顺风顺水，宜主动出击",
        "宜整理旧事，不宜开新局",
        "判断力在线的一天",
        "人际有惊喜，留意邀约",
        "慢热型好运，午后转旺",
        "适合独处与深思考",
        "小破财，大平安",
        "话少福多，少说多做",
        "旧问题出现转机",
        "宜早睡，忌熬夜决定",
    )

    private val colors = listOf("墨黑", "米白", "石灰", "靛青", "松绿", "陶土", "雾灰", "藏蓝")
    private val items = listOf("一支好笔", "保温杯", "旧书一本", "纸巾", "钥匙扣", "耳机", "雨伞", "零钱包")
    private val todos = listOf(
        "把拖了三天的那件事回掉",
        "给久未联系的人发条消息",
        "整理桌面，清空下载文件夹",
        "出门走一条没走过的路",
        "把大任务拆成三小步",
        "提前十分钟出门",
        "读十页书再刷手机",
        "把明天要穿的衣服备好",
        "先做最难的那一项",
        "记录今天的一个小确幸",
    )
    private val avoids = listOf(
        "避免临时改计划",
        "避免在饥饿时做承诺",
        "避免深夜发长消息",
        "避免比较",
        "避免一次揽下三件事",
        "避免纠结已经过去的选择",
        "避免冲动消费",
        "避免替别人做决定",
    )
    private val notes = listOf(
        "今天的关键字：稳。",
        "今天的幸运时刻：傍晚。",
        "今天的隐藏任务：倾听。",
        "今天的惊喜藏在小事里。",
        "今天适合把话说清楚。",
        "今天的答案在两次深呼吸之后。",
        "今天别急着下结论。",
        "今天的运气偏爱先动手的人。",
    )

    private fun seedOf(date: LocalDate, salt: Int): Int {
        val day = date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
        return day * 31 + salt * 7919
    }

    fun generate(date: LocalDate, salt: Int = 0): Result {
        val r = Random(seedOf(date, salt))
        return Result(
            date = date,
            headline = headlines[r.nextInt(headlines.size)],
            luckyColor = colors[r.nextInt(colors.size)],
            luckyNumber = r.nextInt(1, 100),
            luckyItem = items[r.nextInt(items.size)],
            todo = todos[r.nextInt(todos.size)],
            avoid = avoids[r.nextInt(avoids.size)],
            note = notes[r.nextInt(notes.size)],
        )
    }
}

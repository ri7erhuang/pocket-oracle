# 口袋先知 Pocket Oracle

复刻「口袋先知」硬件能力的 Android 离线趣味工具集，复古黑白仿墨水屏 UI。
除蓝牙配对外，全部功能本地离线运行。

## 功能（12 模块）

| # | 模块 | 说明 |
|---|------|------|
| 1 | 答案之书 | 点击随机抽取一条原创答案 |
| 2 | 时势运势 | 按日期哈希生成当日固定运势，可刷新换版 |
| 3 | 人格指南 | 16 型 MBTI 资料速查，选择本地记住 |
| 4 | 十以内 | 1~10 随机数 |
| 5 | 木鱼 | 敲击音效 + 功德计数（持久化），长按重置 |
| 6 | 翻硬币 | 点击 / 摇一摇，翻转动画 |
| 7 | 时钟 | 指针表盘 + 数字时间日期，跟随系统校时 |
| 8 | 番茄时钟 | 工作/休息时长可配置，结束本地铃声 |
| 9 | 计时器 | 自定义时分秒倒计时，结束本地铃声 |
| 10 | 蓝牙遥控 | BLE 扫描/连接，发送上一页/下一页/快门 |
| 11 | 掷骰子 | 点击 / 摇一摇，六面点数动画 |
| 12 | 设置 | 立即锁屏、壁纸切换、WIFI/桌面设置、重力旋转 |

## 技术栈

- Kotlin 1.9.22 · AGP 8.2.2 · Gradle 8.5
- Jetpack Compose（BOM 2024.02.00）+ Material3，单 Activity + Navigation Compose
- minSdk 26 / targetSdk 34
- 持久化：SharedPreferences（功德计数、MBTI 选择、壁纸、番茄配置、旋转开关）
- 音效：`tools/gen_audio.py` 合成的本地 wav（木鱼 knock / 铃声 bell），打包于 `res/raw`
- 图标：Material Symbols（Apache-2.0），壁纸与木鱼/骰子图形为 Canvas 自绘

## 构建

```bash
./gradlew assembleDebug        # 调试包 app/build/outputs/apk/debug/
./gradlew assembleRelease      # 发布包（需自行配置签名）
```

重新生成音效（可选）：

```bash
python3 tools/gen_audio.py
```

## 权限

- 蓝牙：`BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT`（Android 12+），旧版用 `ACCESS_FINE_LOCATION`
- 无需网络、无需音频权限；摇一摇使用加速度传感器（无需权限）

## 已知限制

1. **蓝牙 HID**：Android 只能作为 BLE 中央端（Central），无法把自己模拟成 HID 外设；本项目连接常见 BLE 快门/翻页器并向其可写特征写入按键，不同设备特征各异，可能需要适配。
2. **立即锁屏**：需先在系统弹窗中启用设备管理员；部分 OEM 系统限制锁屏行为。
3. **重力旋转**：切换后回到页面时生效（Activity onResume 应用）。

## 版权说明

全部文案为原创编写，图标使用 Apache-2.0 的 Material 图形库，音效为脚本合成，未复制原硬件素材，个人学习自用无风险；商业化上架前请再次自查素材来源。
# pocket-oracle

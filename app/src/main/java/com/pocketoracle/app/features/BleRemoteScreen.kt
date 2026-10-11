package com.pocketoracle.app.features

import android.annotation.SuppressLint
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pocketoracle.app.ble.BleController
import com.pocketoracle.app.ui.components.ButtonStyle
import com.pocketoracle.app.ui.components.EInkButton
import com.pocketoracle.app.ui.components.EInkOutlineBox
import com.pocketoracle.app.ui.components.PageScaffold
import com.pocketoracle.app.ui.theme.Ink
import com.pocketoracle.app.ui.theme.InkFaint
import com.pocketoracle.app.ui.theme.Paper

private fun neededPermissions(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

private fun hasPermissions(context: Context): Boolean = neededPermissions().all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/** 模块10 蓝牙遥控：BLE 扫描 / 连接 / 发送按键。 */
@Composable
@SuppressLint("MissingPermission")
fun BleRemoteScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasPermissions(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted = hasPermissions(context) }

    DisposableEffect(Unit) {
        onDispose { BleController.stopScan() }
    }

    fun requestPermissions() {
        permissionLauncher.launch(neededPermissions())
    }

    fun enableBluetooth() {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter: BluetoothAdapter? = manager?.adapter
        if (adapter != null && !adapter.isEnabled) {
            runCatching {
                context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        }
    }

    PageScaffold("蓝牙遥控", onBack) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EInkOutlineBox {
                Column {
                    Text(
                        text = "状态",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = BleController.status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink,
                    )
                    if (BleController.connectedName != null) {
                        Text(
                            text = "设备：${BleController.connectedName} · 可写特征 ${BleController.writableCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint,
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EInkButton(
                    text = if (BleController.scanning) "停止扫描" else "扫描设备",
                    onClick = {
                        if (!granted) {
                            requestPermissions()
                            return@EInkButton
                        }
                        if (BleController.scanning) BleController.stopScan()
                        else BleController.scan(context)
                    },
                )
                EInkButton(
                    text = "打开蓝牙",
                    style = ButtonStyle.OUTLINE,
                    onClick = {
                        if (!granted) requestPermissions() else enableBluetooth()
                    },
                )
                if (BleController.connectedAddress != null) {
                    EInkButton(
                        text = "断开",
                        style = ButtonStyle.OUTLINE,
                        onClick = { BleController.disconnect() },
                    )
                }
            }

            if (BleController.devices.isNotEmpty()) {
                Text(
                    text = "扫描到的设备（点击连接）",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkFaint,
                )
                BleController.devices.forEach { device ->
                    val connected = device.address == BleController.connectedAddress
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(if (connected) Ink else Paper)
                            .border(BorderStroke(2.dp, Ink), RectangleShape)
                            .clickable {
                                if (!granted) {
                                    requestPermissions()
                                } else {
                                    BleController.connect(context, device.address)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (connected) Paper else Ink,
                            )
                            Text(
                                text = device.address,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (connected) Paper else InkFaint,
                            )
                        }
                        Text(
                            text = "${device.rssi} dBm",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (connected) Paper else InkFaint,
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            EInkOutlineBox {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "遥控按键",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EInkButton(
                            text = "上一页",
                            onClick = { BleController.send(BleController.Command.PREV) },
                        )
                        EInkButton(
                            text = "下一页",
                            onClick = { BleController.send(BleController.Command.NEXT) },
                        )
                        EInkButton(
                            text = "快门",
                            onClick = { BleController.send(BleController.Command.SHUTTER) },
                        )
                    }
                }
            }

            Text(
                text = "说明：Android 只能作为 BLE 中央端，无法把自己模拟成 HID 外设；本模块连接常见 BLE 快门 / 翻页器，把按键写入其可写特征。首次使用请授予蓝牙权限。",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
            )

            if (!granted) {
                EInkButton(
                    text = "授予蓝牙权限",
                    style = ButtonStyle.OUTLINE,
                    onClick = { requestPermissions() },
                )
            }
        }
    }
}

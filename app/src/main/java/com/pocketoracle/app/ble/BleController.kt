package com.pocketoracle.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 模块10 蓝牙遥控（BLE 扫描 / 连接 / 特征写入）。
 *
 * 说明：Android 只能作为 BLE 中央设备（Central），无法把自己模拟成
 * HID 外设，因此这里连接常见 BLE 快门 / 翻页器，把按键写进其可写特征。
 */
@SuppressLint("MissingPermission")
object BleController {

    data class Found(val name: String, val address: String, val rssi: Int)

    enum class Command(val label: String, val payload: ByteArray) {
        PREV("上一页", byteArrayOf(0x01)),
        NEXT("下一页", byteArrayOf(0x02)),
        SHUTTER("快门", byteArrayOf(0x03)),
    }

    var scanning by mutableStateOf(false)
    var devices by mutableStateOf<List<Found>>(emptyList())
    var connectedName by mutableStateOf<String?>(null)
    var connectedAddress by mutableStateOf<String?>(null)
    var status by mutableStateOf("未连接")
    var writableCount by mutableStateOf(0)

    private var adapter: BluetoothAdapter? = null
    private var gatt: BluetoothGatt? = null
    private var writable: BluetoothGattCharacteristic? = null
    private val handler = Handler(Looper.getMainLooper())

    private val scanTimeout = Runnable { stopScan() }

    private fun adapterOf(context: Context): BluetoothAdapter? {
        if (adapter == null) {
            val manager = context.applicationContext
                .getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            adapter = manager?.adapter
        }
        return adapter
    }

    fun isBluetoothOn(context: Context): Boolean = adapterOf(context)?.isEnabled == true

    private val scanCb = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device ?: return
            val name = result.scanRecord?.deviceName ?: device.name ?: "未知设备"
            val current = devices.toMutableList()
            val idx = current.indexOfFirst { it.address == device.address }
            val found = Found(name, device.address, result.rssi)
            if (idx >= 0) current[idx] = found else current.add(found)
            devices = current.sortedByDescending { it.rssi }
        }

        override fun onScanFailed(errorCode: Int) {
            scanning = false
            status = "扫描失败：$errorCode"
        }
    }

    fun scan(context: Context) {
        val a = adapterOf(context) ?: return
        if (!a.isEnabled) {
            status = "蓝牙未开启"
            return
        }
        devices = emptyList()
        scanning = true
        status = "扫描中…"
        a.bluetoothLeScanner?.startScan(scanCb)
        handler.removeCallbacks(scanTimeout)
        handler.postDelayed(scanTimeout, 10_000)
    }

    fun stopScan() {
        handler.removeCallbacks(scanTimeout)
        if (!scanning) return
        scanning = false
        adapter?.bluetoothLeScanner?.stopScan(scanCb)
        if (status == "扫描中…") status = "扫描结束，共 ${devices.size} 台"
    }

    private val gattCb = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, newStateStatus: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                status = "已连接，发现服务…"
                g.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                writable = null
                writableCount = 0
                connectedName = null
                connectedAddress = null
                status = if (newStateStatus == BluetoothGatt.GATT_SUCCESS) "已断开" else "连接断开（$newStateStatus）"
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, discoverStatus: Int) {
            if (discoverStatus != BluetoothGatt.GATT_SUCCESS) {
                status = "服务发现失败：$discoverStatus"
                return
            }
            val chars = g.services
                .flatMap { it.characteristics }
                .filter {
                    it.properties and
                        (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
                }
            writable = chars.firstOrNull()
            writableCount = chars.size
            connectedAddress = g.device.address
            connectedName = g.device.name ?: g.device.address
            status = if (chars.isNotEmpty()) {
                "已连接 $connectedName，可写特征 ${chars.size} 个"
            } else {
                "已连接，但没有可写特征"
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicWrite(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            writeStatus: Int,
        ) {
            status = if (writeStatus == BluetoothGatt.GATT_SUCCESS) {
                "已发送 → ${connectedName ?: ""}"
            } else {
                "发送失败：$writeStatus"
            }
        }
    }

    fun connect(context: Context, address: String) {
        disconnect()
        val a = adapterOf(context) ?: return
        val device: BluetoothDevice = a.getRemoteDevice(address)
        status = "连接中…"
        gatt = device.connectGatt(context.applicationContext, false, gattCb)
    }

    fun disconnect() {
        handler.removeCallbacks(scanTimeout)
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        writable = null
        writableCount = 0
        connectedName = null
        connectedAddress = null
        status = "未连接"
    }

    fun send(command: Command): Boolean {
        val g = gatt ?: run { status = "未连接设备"; return false }
        val c = writable ?: run { status = "没有可写特征"; return false }
        @Suppress("DEPRECATION")
        c.value = command.payload
        @Suppress("DEPRECATION")
        val ok = g.writeCharacteristic(c)
        if (!ok) status = "写入失败"
        return ok
    }
}

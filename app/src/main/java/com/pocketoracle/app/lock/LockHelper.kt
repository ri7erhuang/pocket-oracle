package com.pocketoracle.app.lock

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/** 设备管理员接收器：仅用于“立即锁屏”。 */
class LockAdminReceiver : DeviceAdminReceiver()

object LockHelper {

    private fun component(context: Context) =
        ComponentName(context.applicationContext, LockAdminReceiver::class.java)

    fun isActive(context: Context): Boolean {
        val dpm = context.getSystemService(DevicePolicyManager::class.java)
        return dpm.isAdminActive(component(context))
    }

    fun lockNow(context: Context) {
        if (!isActive(context)) return
        val dpm = context.getSystemService(DevicePolicyManager::class.java)
        dpm.lockNow()
    }

    fun requestAdmin(context: Context) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component(context))
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "口袋先知需要设备管理员权限，才能实现「立即锁屏」功能。",
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

package com.hotspotguard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            // Phone restart ke baad service auto-start
            if (AppPreferences.isServiceEnabled(context)) {
                val serviceIntent = Intent(context, HotspotMonitorService::class.java)
                serviceIntent.action = HotspotMonitorService.ACTION_START

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}

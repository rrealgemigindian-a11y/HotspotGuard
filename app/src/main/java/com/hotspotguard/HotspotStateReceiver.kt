package com.hotspotguard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build

class HotspotStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.net.wifi.WIFI_AP_STATE_CHANGED") return

        // Service enabled check
        if (!AppPreferences.isServiceEnabled(context)) return

        val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, -1)

        when (state) {
            WifiManager.WIFI_AP_STATE_DISABLED, WifiManager.WIFI_AP_STATE_DISABLING -> {
                // Hotspot band hua — service ko batao
                val serviceIntent = Intent(context, HotspotMonitorService::class.java)
                serviceIntent.action = ACTION_HOTSPOT_OFF

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }

            WifiManager.WIFI_AP_STATE_ENABLED, WifiManager.WIFI_AP_STATE_ENABLING -> {
                // Hotspot on hua — countdown cancel karo
                val serviceIntent = Intent(context, HotspotMonitorService::class.java)
                serviceIntent.action = ACTION_HOTSPOT_ON

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}

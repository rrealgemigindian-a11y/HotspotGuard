package com.hotspotguard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class HotspotStateReceiver : BroadcastReceiver() {

    companion object {
        // WifiManager AP state constants (hidden in newer APIs, using raw values)
        const val WIFI_AP_STATE_DISABLING = 10
        const val WIFI_AP_STATE_DISABLED = 11
        const val WIFI_AP_STATE_ENABLING = 12
        const val WIFI_AP_STATE_ENABLED = 13
        const val EXTRA_WIFI_AP_STATE = "wifi_ap_state"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.net.wifi.WIFI_AP_STATE_CHANGED") return

        if (!AppPreferences.isServiceEnabled(context)) return

        val state = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1)

        when (state) {
            WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_DISABLING -> {
                val serviceIntent = Intent(context, HotspotMonitorService::class.java)
                serviceIntent.action = ACTION_HOTSPOT_OFF
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }

            WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_ENABLING -> {
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

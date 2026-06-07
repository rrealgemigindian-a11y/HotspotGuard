package com.hotspotguard

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import java.lang.reflect.Method

object HotspotUtils {

    fun isHotspotEnabled(context: Context): Boolean {
        return try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method: Method = wifiManager.javaClass.getDeclaredMethod("getWifiApState")
            method.isAccessible = true
            val state = method.invoke(wifiManager) as Int
            state == 13 // WIFI_AP_STATE_ENABLED = 13
        } catch (e: Exception) {
            false
        }
    }

    fun enableHotspot(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enableHotspotModern(context)
        } else {
            enableHotspotLegacy(context)
        }
    }

    private fun enableHotspotLegacy(context: Context) {
        try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

            // Pehle WiFi band karo (hotspot ke liye zaruri)
            wifiManager.isWifiEnabled = false

            // Reflection se hotspot on karo (Android 7 aur neeche)
            val method: Method = wifiManager.javaClass.getDeclaredMethod(
                "setWifiApEnabled",
                android.net.wifi.WifiConfiguration::class.java,
                Boolean::class.java
            )
            method.isAccessible = true
            method.invoke(wifiManager, null, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enableHotspotModern(context: Context) {
        try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

            // Android 8+ mein LocalOnlyHotspot hi possible hai bina root ke
            // Poora hotspot ke liye device ko root hona chahiye ya system app honi chahiye
            wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                    super.onStarted(reservation)
                    // Local hotspot start hua
                }

                override fun onFailed(reason: Int) {
                    super.onFailed(reason)
                    // Failed — try system method via reflection
                    enableHotspotViaReflectionModern(context)
                }
            }, null)
        } catch (e: Exception) {
            enableHotspotViaReflectionModern(context)
        }
    }

    private fun enableHotspotViaReflectionModern(context: Context) {
        try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = wifiManager.javaClass.getDeclaredMethod(
                "setWifiApEnabled",
                android.net.wifi.WifiConfiguration::class.java,
                Boolean::class.java
            )
            method.isAccessible = true
            method.invoke(wifiManager, null, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.hotspotguard

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import java.lang.reflect.Method

object MobileDataUtils {

    fun enableMobileData(context: Context) {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

            // Reflection se mobile data on karo
            val setMobileDataEnabledMethod: Method = connectivityManager.javaClass
                .getDeclaredMethod("setMobileDataEnabled", Boolean::class.java)
            setMobileDataEnabledMethod.isAccessible = true
            setMobileDataEnabledMethod.invoke(connectivityManager, true)
        } catch (e: Exception) {
            // Android 5+ pe kaam nahi karega without system permission
            // Try alternative method
            enableDataViaITelephony(context)
        }
    }

    private fun enableDataViaITelephony(context: Context) {
        try {
            val telephonyService = Class.forName("android.telephony.TelephonyManager")
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE)

            val method = telephonyService.getDeclaredMethod("setDataEnabled", Boolean::class.java)
            method.isAccessible = true
            method.invoke(telephonyManager, true)
        } catch (e: Exception) {
            e.printStackTrace()
            // Root access needed for newer Android versions
        }
    }

    fun isMobileDataEnabled(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val method = connectivityManager.javaClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true
            method.invoke(connectivityManager) as Boolean
        } catch (e: Exception) {
            false
        }
    }
}

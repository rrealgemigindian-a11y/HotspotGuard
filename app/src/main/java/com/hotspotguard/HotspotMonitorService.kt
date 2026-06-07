package com.hotspotguard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class HotspotMonitorService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "hotspot_guard_channel"
        const val DELAY_MS = 5 * 60 * 1000L  // 5 minutes in milliseconds
    }

    private var countDownTimer: CountDownTimer? = null
    private var isWaitingToRestart = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("🛡️ HotspotGuard chal raha hai..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                AppPreferences.setServiceEnabled(this, true)
                updateNotification("🛡️ HotspotGuard Active — Monitoring chal raha hai")
            }
            ACTION_STOP -> {
                countDownTimer?.cancel()
                isWaitingToRestart = false
                AppPreferences.setServiceEnabled(this, false)
                stopSelf()
            }
            ACTION_HOTSPOT_OFF -> {
                if (!isWaitingToRestart) {
                    startCountdownToRestart()
                }
            }
            ACTION_HOTSPOT_ON -> {
                if (isWaitingToRestart) {
                    countDownTimer?.cancel()
                    isWaitingToRestart = false
                    updateNotification("🛡️ HotspotGuard Active — Monitoring chal raha hai")
                }
            }
        }
        return START_STICKY
    }

    fun startCountdownToRestart() {
        isWaitingToRestart = true
        updateNotification("⏳ Hotspot band hua — 5 minute mein on hoga...")

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(DELAY_MS, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val minutesLeft = millisUntilFinished / 60000
                val secondsLeft = (millisUntilFinished % 60000) / 1000
                updateNotification("⏳ Hotspot on hoga: ${minutesLeft}m ${secondsLeft}s mein...")
            }

            override fun onFinish() {
                isWaitingToRestart = false
                enableHotspotAndData()
                updateNotification("✅ Hotspot on kar diya! Monitor jaari hai...")
            }
        }.start()
    }

    private fun enableHotspotAndData() {
        // Step 1: Enable mobile data
        MobileDataUtils.enableMobileData(this)

        // Step 2: Enable hotspot (with small delay for data to connect)
        android.os.Handler(mainLooper).postDelayed({
            HotspotUtils.enableHotspot(this)
        }, 2000)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HotspotGuard Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hotspot monitoring service"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HotspotGuard")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}

const val ACTION_HOTSPOT_OFF = "com.hotspotguard.HOTSPOT_OFF"
const val ACTION_HOTSPOT_ON = "com.hotspotguard.HOTSPOT_ON"

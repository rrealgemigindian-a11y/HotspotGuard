package com.hotspotguard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.hotspotguard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        requestNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun setupUI() {
        binding.btnStartService.setOnClickListener {
            startMonitoringService()
        }

        binding.btnStopService.setOnClickListener {
            stopMonitoringService()
        }

        binding.btnOpenHotspot.setOnClickListener {
            openHotspotSettings()
        }
    }

    private fun startMonitoringService() {
        val serviceIntent = Intent(this, HotspotMonitorService::class.java)
        serviceIntent.action = HotspotMonitorService.ACTION_START

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        AppPreferences.setServiceEnabled(this, true)
        updateStatus()
        Toast.makeText(this, "✅ Monitoring shuru ho gaya!", Toast.LENGTH_SHORT).show()
    }

    private fun stopMonitoringService() {
        val serviceIntent = Intent(this, HotspotMonitorService::class.java)
        serviceIntent.action = HotspotMonitorService.ACTION_STOP
        startService(serviceIntent)

        AppPreferences.setServiceEnabled(this, false)
        updateStatus()
        Toast.makeText(this, "❌ Monitoring band ho gaya.", Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus() {
        val isEnabled = AppPreferences.isServiceEnabled(this)
        val hotspotOn = HotspotUtils.isHotspotEnabled(this)

        binding.tvStatus.text = if (isEnabled) "🟢 Guard Active — Hotspot monitor chal raha hai" 
                                else "🔴 Guard Inactive — Service band hai"

        binding.tvHotspotStatus.text = if (hotspotOn) "📡 Hotspot: ON" else "📡 Hotspot: OFF"

        binding.btnStartService.isEnabled = !isEnabled
        binding.btnStopService.isEnabled = isEnabled
    }

    private fun openHotspotSettings() {
        try {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setClassName("com.android.settings", "com.android.settings.TetherSettings")
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }
}

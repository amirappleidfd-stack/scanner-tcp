package com.railwaypingtester.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.railwaypingtester.R

class PingService : android.app.Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "railway_ping_service"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): android.os.IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val notification = createNotification("Ping scan in progress...")

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        return START_STICKY
    }

    private fun createNotification(title: String): android.app.Notification {

        val intent = Intent(
            this,
            com.railwaypingtester.ui.screens.MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ping Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background ping scanning service"
            }

            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle(title)
            .setContentText(
                "Railway ping scanning in progress"
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(
                NotificationCompat.PRIORITY_LOW
            )
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::notificationManager.isInitialized) {
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }
}

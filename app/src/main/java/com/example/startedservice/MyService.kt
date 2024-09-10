package com.example.startedservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false

    companion object {
        private const val CHANNEL_ID = "ServiceChannel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "com.example.startedservice.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.vikram_rolex_bgm)
        createNotificationChannel()
        Toast.makeText(this, "Service created...", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NotificationChannel"
            val descriptionText = "Service channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, getNotification())
        Toast.makeText(this, "Service started...", Toast.LENGTH_SHORT).show()

        intent?.let {
            when (it.action) {
                ACTION_STOP -> {
                    pauseSong()
                }

                else -> {
                    playSong()
                }
            }
        }
        return START_STICKY
    }

    private fun getNotification(): Notification {
        val notificationIntent = Intent(this, SecondActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing music")
            .setContentText("Song playing in the background")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun playSong() {
        if (!isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    private fun pauseSong() {
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
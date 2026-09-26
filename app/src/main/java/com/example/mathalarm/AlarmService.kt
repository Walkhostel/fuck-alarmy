package com.example.mathalarm

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private var player: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(AlarmReceiver.NOTIFICATION_ID, buildNotification())
        playSound()
        return START_STICKY
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, AlarmReceiver.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Будильник звонит")
            .setContentText("Решите пример, чтобы выключить")
            .setOngoing(true)
            .build()

    private fun playSound() {
        val customUri = Prefs.getSoundUri(this)
        val uri: Uri = if (customUri != null) {
            Uri.parse(customUri)
        } else {
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            isLooping = true
            try {
                setDataSource(this@AlarmService, uri)
                prepare()
                start()
            } catch (e: Exception) {
                // Звук не загрузился — сервис и экран AlarmActivity всё равно работают.
            }
        }
    }

    private fun stopAlarmSound() {
        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null
    }

    override fun onDestroy() {
        stopAlarmSound()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(AlarmReceiver.NOTIFICATION_ID)
        super.onDestroy()
    }
}

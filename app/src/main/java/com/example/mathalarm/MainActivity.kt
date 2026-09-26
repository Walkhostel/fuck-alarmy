package com.example.mathalarm

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var etMin: EditText
    private lateinit var etMax: EditText
    private lateinit var etCount: EditText
    private lateinit var tvSound: TextView
    private lateinit var tvStatus: TextView

    private var pickedSoundUri: Uri? = null

    private val soundPickerLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                pickedSoundUri = uri
                tvSound.text = "Звук: выбран кастомный"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        etMin = findViewById(R.id.etMin)
        etMax = findViewById(R.id.etMax)
        etCount = findViewById(R.id.etCount)
        tvSound = findViewById(R.id.tvSound)
        tvStatus = findViewById(R.id.tvStatus)

        timePicker.setIs24HourView(true)
        timePicker.hour = Prefs.getHour(this)
        timePicker.minute = Prefs.getMinute(this)
        etMin.setText(Prefs.getPuzzleMin(this).toString())
        etMax.setText(Prefs.getPuzzleMax(this).toString())
        etCount.setText(Prefs.getPuzzleCount(this).toString())

        Prefs.getSoundUri(this)?.let { tvSound.text = "Звук: выбран кастомный" }

        requestPermissionsIfNeeded()

        findViewById<Button>(R.id.btnPickSound).setOnClickListener { pickSound() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveAndSchedule() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { cancelAlarm() }

        updateStatus()
    }

    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
    }

    private fun pickSound() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        }
        soundPickerLauncher.launch(intent)
    }

    private fun saveAndSchedule() {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val min = etMin.text.toString().toIntOrNull() ?: 2
        val max = etMax.text.toString().toIntOrNull() ?: 12
        val count = etCount.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 3

        Prefs.setAlarmTime(this, hour, minute)
        Prefs.setPuzzleRange(this, min, max)
        Prefs.setPuzzleCount(this, count)
        pickedSoundUri?.let { Prefs.setSoundUri(this, it.toString()) }
        Prefs.setEnabled(this, true)

        AlarmScheduler.schedule(this, hour, minute)
        updateStatus()
    }

    private fun cancelAlarm() {
        Prefs.setEnabled(this, false)
        AlarmScheduler.cancel(this)
        updateStatus()
    }

    private fun updateStatus() {
        tvStatus.text = if (Prefs.isEnabled(this)) {
            "Будильник включён на %02d:%02d".format(Prefs.getHour(this), Prefs.getMinute(this))
        } else {
            "Будильник выключен"
        }
    }
}

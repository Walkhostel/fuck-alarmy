package com.example.mathalarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {

    private lateinit var tvPuzzle: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvError: TextView
    private lateinit var etAnswer: EditText

    private var solvedCount = 0
    private var targetCount = 3

    // [a, b, правильный_ответ] — из Rust (NativeCore.generate)
    private lateinit var current: IntArray
    private val currentAnswer get() = current[2]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()
        setContentView(R.layout.activity_alarm)

        tvPuzzle = findViewById(R.id.tvPuzzle)
        tvProgress = findViewById(R.id.tvProgress)
        tvError = findViewById(R.id.tvError)
        etAnswer = findViewById(R.id.etAnswer)

        targetCount = Prefs.getPuzzleCount(this)

        findViewById<Button>(R.id.btnSubmit).setOnClickListener { checkAnswer() }
        findViewById<Button>(R.id.btnExit).setOnClickListener { stopAlarmAndFinish() }

        nextPuzzle()
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun nextPuzzle() {
        current = NativeCore.generate(Prefs.getPuzzleMin(this), Prefs.getPuzzleMax(this))
        tvPuzzle.text = "${current[0]} × ${current[1]} = ?"
        tvProgress.text = "Пример ${solvedCount + 1} из $targetCount"
        etAnswer.text.clear()
        tvError.visibility = TextView.INVISIBLE
    }

    private fun checkAnswer() {
        val input = etAnswer.text.toString().toIntOrNull()
        if (input != null && NativeCore.checkAnswer(input, currentAnswer)) {
            solvedCount++
            if (solvedCount >= targetCount) {
                stopAlarmAndFinish()
            } else {
                nextPuzzle()
            }
        } else {
            tvError.visibility = TextView.VISIBLE
            etAnswer.text.clear()
        }
    }

    private fun stopAlarmAndFinish() {
        stopService(Intent(this, AlarmService::class.java))
        finishAndRemoveTask()
    }

    // Выход — только явной кнопкой "Выйти".
    override fun onBackPressed() {}
}

package com.example.mathalarm

/** Мостик в Rust. Только математика примера и расчёт времени срабатывания — ничего больше. */
object NativeCore {
    init {
        System.loadLibrary("mathalarm_core")
    }

    /** [a, b, правильный_ответ] */
    external fun generate(min: Int, max: Int): IntArray

    external fun checkAnswer(userAnswer: Int, correctAnswer: Int): Boolean

    /** epoch-millis ближайшего будущего срабатывания с этим локальным часом:минутой. */
    external fun nextFireTimeMillis(nowMillis: Long, tzOffsetMillis: Long, hour: Int, minute: Int): Long
}

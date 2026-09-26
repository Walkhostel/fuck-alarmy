package com.example.mathalarm

import android.content.Context

object Prefs {
    private const val FILE = "math_alarm_prefs"

    private fun p(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun setAlarmTime(ctx: Context, hour: Int, minute: Int) {
        p(ctx).edit().putInt("hour", hour).putInt("minute", minute).apply()
    }

    fun getHour(ctx: Context) = p(ctx).getInt("hour", 7)
    fun getMinute(ctx: Context) = p(ctx).getInt("minute", 0)

    fun setSoundUri(ctx: Context, uri: String?) {
        p(ctx).edit().putString("sound_uri", uri).apply()
    }

    fun getSoundUri(ctx: Context): String? = p(ctx).getString("sound_uri", null)

    fun setPuzzleRange(ctx: Context, min: Int, max: Int) {
        p(ctx).edit().putInt("puzzle_min", min).putInt("puzzle_max", max).apply()
    }

    fun getPuzzleMin(ctx: Context) = p(ctx).getInt("puzzle_min", 2)
    fun getPuzzleMax(ctx: Context) = p(ctx).getInt("puzzle_max", 12)

    fun setPuzzleCount(ctx: Context, count: Int) {
        p(ctx).edit().putInt("puzzle_count", count).apply()
    }

    fun getPuzzleCount(ctx: Context) = p(ctx).getInt("puzzle_count", 3)

    fun setEnabled(ctx: Context, enabled: Boolean) {
        p(ctx).edit().putBoolean("enabled", enabled).apply()
    }

    fun isEnabled(ctx: Context) = p(ctx).getBoolean("enabled", false)
}

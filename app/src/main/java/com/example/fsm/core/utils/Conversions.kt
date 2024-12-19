package com.example.fsm.core.utils

import java.util.Date


fun formatTime(milliseconds: Int): String {
    val minutes = milliseconds / 1000 / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun convertMillisecondsToTime(ms: Int): String {
    val hours = ms / 3600000
    val minutes = (ms % 3600000) / 60000
    val seconds = (ms % 60000) / 1000

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}



fun calculatePercentage(part: Double, whole: Double): Int {
    return ((part / whole) * 100).toInt()
}

fun calculateOriginalValue(percentage: Double, part: Double): Int {
    return ((part * 100) / percentage).toInt()
}

fun calculatePartFromPercentage(percentage: Double, whole: Double): Int {
    return ((percentage / 100) * whole).toInt()
}
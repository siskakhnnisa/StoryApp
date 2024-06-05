package com.example.storyappfix.utils

import android.content.Context
import android.os.Environment
import com.example.storyappfix.R
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


private const val FormatTime = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

object CameraUtils{

fun getTimeUpload(context: Context, timeStamp: String): String {
    val currentTime = currentDate()
    val timeUpload = parseDate(timeStamp)
    val different: Long = currentTime.time - timeUpload.time
    val second = different / 1000
    val minute = second / 60
    val hour = minute / 60
    val days = hour / 24
    val timeLabel = when (minute.toInt()) {
        0 -> "$second ${context.getString(R.string.second)}"
        in 1..59 -> "$minute ${context.getString(R.string.minutes)}"
        in 60..1440 -> "$hour ${context.getString(R.string.hours)}"
        else -> "$days ${context.getString(R.string.days)}"
    }
    return timeLabel
}

fun parseDate(timeStamp: String): Date =
    try {
        val dateFormatter = SimpleDateFormat(FormatTime, Locale.getDefault())
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
        dateFormatter.parse(timeStamp) as Date
    } catch (e: ParseException) {
        currentDate()
    }

fun currentDate(): Date = Date()

    private const val FORMAT_FILE = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(
    FORMAT_FILE,
    Locale.US
).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val directoryStorage: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", directoryStorage)
}

}
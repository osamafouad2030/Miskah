package com.example.mishkat.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import java.util.Calendar

/**
 * مدير جدولة وتخصيص تنبيهات المراجعة اليومية لتحريرات عاصم
 */
class MishkatReminderManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "MishkatReminderMgr"
        private const val PREFS_NAME = "mishkat_reminder_prefs"
        private const val KEY_ENABLED = "reminder_enabled"
        private const val KEY_HOUR = "reminder_hour"
        private const val KEY_MINUTE = "reminder_minute"
        private const val KEY_TOPIC = "reminder_topic"
        private const val REQUEST_CODE = 1001

        const val CHANNEL_ID = "mishkat_daily_reminder_channel"
        const val ACTION_DAILY_REMINDER = "com.example.mishkat.DAILY_REMINDER"
    }

    init {
        createNotificationChannel()
    }

    fun isReminderEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun getReminderHour(): Int = prefs.getInt(KEY_HOUR, 20) // الافتراضي 8:00 مساءً

    fun getReminderMinute(): Int = prefs.getInt(KEY_MINUTE, 0)

    fun getReminderTopic(): String =
        prefs.getString(KEY_TOPIC, "مراجعة طرق قصر المنفصل وأوجه السكت") ?: "مراجعة طرق قصر المنفصل"

    fun setReminder(enabled: Boolean, hour: Int, minute: Int, topic: String) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, enabled)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .putString(KEY_TOPIC, topic)
            .apply()

        if (enabled) {
            scheduleAlarm(hour, minute, topic)
        } else {
            cancelAlarm()
        }
    }

    private fun scheduleAlarm(hour: Int, minute: Int, topic: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, MishkatReminderReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
            putExtra("reminder_topic", topic)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // إذا كان الموعد قد مضى اليوم، نجدوله للغد
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Log.d(TAG, "Daily reminder scheduled for ${hour}:${minute} on topic: $topic")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder alarm: ${e.message}", e)
        }
    }

    private fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MishkatReminderReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        Log.d(TAG, "Daily reminder cancelled")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "تنبيهات المراجعة اليومية لمشكاة"
            val descriptionText = "تذكير يومي للورد ومراجعة تحريرات روايتي حفص وشعبة من طيبة النشر"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }
}

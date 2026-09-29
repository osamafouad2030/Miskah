package com.example.mishkat.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

/**
 * مستقبل تنبيهات المراجعة اليومية لبث إشعارات تذكير الدروس والتحريرات
 */
class MishkatReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "MishkatReminderReceiver"
        private const val NOTIFICATION_ID = 2002
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called with action: ${intent.action}")

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // إعادة ضبط التنبيهات بعد إعادة تشغيل الجهاز إذا كانت مفعلة
            val reminderManager = MishkatReminderManager(context)
            if (reminderManager.isReminderEnabled()) {
                reminderManager.setReminder(
                    enabled = true,
                    hour = reminderManager.getReminderHour(),
                    minute = reminderManager.getReminderMinute(),
                    topic = reminderManager.getReminderTopic()
                )
            }
            return
        }

        val topic = intent.getStringExtra("reminder_topic") ?: "تحريرات عاصم من طيبة النشر"

        // فحص صلاحية الإشعارات على أندرويد 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted, skipping notification display")
                return
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val notificationTitle = "موعد ورد المراجعة اليومي مع «مشكاة» 📖"
        val notificationText = "حان موعد مراجعتك اليومية لـ: $topic. افتح مشكاة لترسيخ الأوجه والتحريرات."

        val notification = NotificationCompat.Builder(context, MishkatReminderManager.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

package org.gkisalatiga.plus.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AlarmRoutes
import java.util.Calendar

class AlarmService {
    companion object {

        /**
         * Setting the morning devotion reminder every morning at 5:00 AM.
         */
        fun initSarenAlarm(ctx: Context) {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) Log.d("Groaker-Test", "[initSarenAlarm] Setting the alarm ...")

            // Set the alarm to start at approximately 5:00 a.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR, 5)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 5)
            }

            // Pass the payload
            // SOURCE: https://stackoverflow.com/a/62866557
            val notifyIntent = Intent(ctx, AlarmReceiver::class.java)
            notifyIntent.putExtra("host", AlarmRoutes.ALARM_SAREN)

            // Use "FLAG_MUTABLE" to avoid "targeting S+ version 31 and above" error.
            // SOURCE: https://stackoverflow.com/a/69235587
            val alarmIntent: PendingIntent = PendingIntent.getBroadcast(ctx, 0, notifyIntent, PendingIntent.FLAG_MUTABLE)
            val alarmMgr: AlarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )
        }

        /**
         * Reminder to read the afternoon devotion at approximately 12:00 p.m.
         */
        fun initYKBDailyAlarm(ctx: Context) {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) Log.d("Groaker-Test", "[initYKBDailyAlarm] Setting the alarm ...")

            // Set the alarm to start at approximately 5:00 a.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 5)
            }

            // Pass the payload
            // SOURCE: https://stackoverflow.com/a/62866557
            val notifyIntent = Intent(ctx, AlarmReceiver::class.java)
            notifyIntent.putExtra("host", AlarmRoutes.ALARM_YKB_HARIAN)

            // Use "FLAG_MUTABLE" to avoid "targeting S+ version 31 and above" error.
            // SOURCE: https://stackoverflow.com/a/69235587
            val alarmIntent: PendingIntent = PendingIntent.getBroadcast(ctx, 0, notifyIntent, PendingIntent.FLAG_MUTABLE)
            val alarmMgr: AlarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )
        }

    }
}
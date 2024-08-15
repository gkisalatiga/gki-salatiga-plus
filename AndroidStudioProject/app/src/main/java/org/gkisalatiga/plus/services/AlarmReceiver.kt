package org.gkisalatiga.plus.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AlarmRoutes

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT)  Log.d("Groaker-Alarm", "AlarmReceiver got triggered.")

        // Determining which alarm got triggered.
        // SOURCE: https://stackoverflow.com/a/62866557
        val triggeringHost = intent!!.extras!!.getString("host")

        if (triggeringHost == AlarmRoutes.ALARM_SAREN) {
            NotificationService.showSarenNotification(context!!)
        } else if (triggeringHost == AlarmRoutes.ALARM_YKB_HARIAN) {
            NotificationService.showYKBHarianNotification(context!!)
        } else {
            // There is something wrong with the notification scheduler. We must fix it.
            NotificationService.showDebugNotification(context!!)
        }

        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Alarm", "BOOT_COMPLETED received! Restarting the alarms ...")

            // Restart the alarms.
            AlarmService.initSarenAlarm(context!!)
            AlarmService.initYKBDailyAlarm(context!!)
        }
    }
}

/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.Tags
import org.gkisalatiga.plus.worker.YKBNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit


/**
 * Creates a worker scheduler that automatically calls a worker at an exact time of the day.
 * SOURCE: https://stackoverflow.com/a/50363613
 * SOURCE: https://blog.sanskar10100.dev/implementing-periodic-notifications-with-workmanager
 */
class WorkScheduler {
    companion object {

        fun scheduleYKBReminder(ctx: Context) {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_WORKER) Log.d("Groaker-Worker", "[WorkScheduler.scheduleYKBReminder] Scheduling the worker reminder ...")

            // Get the current time.
            val now = Calendar.getInstance()

            // Set at which time should we schedule this work.
            val target = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.SECOND, 20)
            }

            // TODO: This works
            /*val target = Calendar.getInstance().apply {
                // timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR, 5)
                set(Calendar.MINUTE, 37)
                set(Calendar.SECOND, 0)
            }*/

            // Ensures that we don't "schedule something in the past." This controls periodicity.
            if (target.before(now)) target.add(Calendar.MINUTE, 1)
            // if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)

            // TODO: This works
            /*if (target.before(now)) target.add(Calendar.MILLISECOND, (now.timeInMillis - target.timeInMillis).toInt())
            target.add(Calendar.SECOND, 10)*/

            // Prevents multiple firings of work trigger. (?)
            target.add(Calendar.MILLISECOND, 1500)
            // WorkManager.getInstance(ctx).cancelAllWorkByTag(Tags.NAME_YKB_WORK)
            // WorkManager.getInstance(ctx).cancelUniqueWork(Tags.NAME_YKB_WORK)

            // Creates the instance of a unique one-time work.
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(YKBNotificationWorker::class.java)
                .addTag(Tags.TAG_YKB_REMINDER)
                .setInitialDelay(target.timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build()

            // Prevents multiple firings of work trigger. (?)
            // WorkManager.getInstance(ctx).cancelAllWorkByTag(Tags.NAME_YKB_WORK)
            // WorkManager.getInstance(ctx).cancelAllWork()

            // Creates the request to start a worker.
            val request = WorkManager.getInstance(ctx).enqueueUniqueWork(
                Tags.NAME_YKB_WORK,
                ExistingWorkPolicy.REPLACE,  // --- prevents multiple trigger-fires (?)
                oneTimeWorkRequest
            )
            // val request = WorkManager.getInstance(ctx).enqueue(oneTimeWorkRequest)

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_WORKER) Log.d("Groaker-Worker", "[WorkScheduler.scheduleYKBReminder] What do we have here? What's the result? ${request.result}")

        }  // --- end of fun().

    }  // --- end of companion object.
}  // --- end of class.
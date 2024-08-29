/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Checks whether a new app update is found.
 */

package org.gkisalatiga.plus.services

import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.AppGallery
import org.gkisalatiga.plus.lib.AppStatic
import org.gkisalatiga.plus.lib.Downloader
import org.gkisalatiga.plus.services.DataUpdater.Companion
import org.json.JSONObject
import java.io.InputStream
import java.net.UnknownHostException
import java.util.concurrent.Executors

class ApplicationUpdater(private val ctx: Context) {

    companion object {
        // The location of the remote feeds.json file to check for updates.
        private const val FEEDS_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/feeds.json"
    }

    /**
     * Get the cloud-stored feed information
     * in order to determine whether we should update the data or not.
     */
    private fun getMostRecentFeeds() : JSONObject {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.d("Groaker-Updater", "[ApplicationUpdater.getMostRecentFeeds] Fetching the update feeds ...")
        val streamInput: InputStream = java.net.URL(ApplicationUpdater.FEEDS_JSON_SOURCE).openStream()
        val inputAsString: String = streamInput.bufferedReader().use { it.readText() }

        // Dumps the JSON string, and compare them with the currently cached JSON last update values.
        // if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_DUMP) Log.d("Groaker-Dump", inputAsString)

        // Returns the feeds data.
        return JSONObject(inputAsString).getJSONObject("feeds")
    }

    /**
     * Check for new app updates.
     */
    public fun checkAppUpdate() {
        // Upon successful data download, we manage the app's internal variable storage
        // according to the downloaded JSON file's schema.
        // We also make any appropriate settings accordingly.
        // ---
        // This is all done in a multi-thread so that we do not interrupt the main GUI.
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.d("Groaker-Test", "[ApplicationUpdater.checkAppUpdate] Checking the latest app update ...")

            try {
                // Retrieving the feed JSON file to check if update is even necessary.
                val feedJSONObject = getMostRecentFeeds()

                // Get the necessary information.
                GlobalSchema.newAppVersionCode.intValue = feedJSONObject.getJSONObject("app-update").getInt("version-code")
                GlobalSchema.newAppVersionName.value = feedJSONObject.getJSONObject("app-update").getString("version-name")
                GlobalSchema.newAppDownloadURL.value = feedJSONObject.getJSONObject("app-update").getString("download-url")

                // Trigger update check dialog display.
                GlobalSchema.triggerAppUpdateDialog.value = !GlobalSchema.triggerAppUpdateDialog.value
                // ---
                // Obtain the app's essential information.
                // SOURCE: https://stackoverflow.com/a/6593822
                val pInfo: PackageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0)
                val vName = pInfo.versionName
                val vCode = pInfo.versionCode
                // ---
                // Add app updater listener.
                if (GlobalSchema.newAppVersionCode.intValue > vCode) {
                    GlobalSchema.appUpdaterIsShown.value = true
                }

            } catch (e: UnknownHostException) {
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) Log.e("Groaker-Test", "[ApplicationUpdater.checkAppUpdate] Network error! Cannot check for the newest app update.")
            }

            // End the thread.
            executor.shutdown()

        }  // --- end of executor.execute()
    }

}
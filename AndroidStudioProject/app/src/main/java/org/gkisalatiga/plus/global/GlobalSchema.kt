/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * Implements a global declaration of variables, which can be accessed across classes.
 * SOURCE: https://tutorial.eyehunts.com/android/declare-android-global-variable-kotlin-example/
 * SOURCE: https://stackoverflow.com/a/52844621
 */

package org.gkisalatiga.plus.global

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import org.json.JSONObject

class GlobalSchema : Application() {
    override fun onCreate() { super.onCreate() }

    // Initializing the data schema of the app that will be shared across composables
    // and that will course the navigation of screens.
    companion object {

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which JSON API source to look up to in order to update the application content.
         * It cannot and should not be changed arbitrarily within the app code. */
        val JSONSource = "https://raw.githubusercontent.com/groaking/groaking.github.io/main/playground/gkisplus.json"

        // This is the filename which will save the above JSON source.
        val JSONSavedFilename = "gkisplus.json"

        // Stores the absolute path of the downloaded (into internal app storage) JSON metadata
        var absolutePathToJSONMetaData = ""

        // The state of the initialization of the JSON metadata.
        var isJSONMetaDataInitialized = mutableStateOf(false)

        // The JSONObject that can be globally accessed by any function and class in the app.
        var globalJSONObject: JSONObject? = null

        /* ------------------------------------------------------------------------------------ */
        /* These parameters are used to navigate across screens, fragments, and submenus in the composables.
         * These parameters must be individually a mutable state object.
         * Changing any of the following parameters would directly and immediately trigger recomposition. */

        // Determines where to go when pressing the "back" button after changing screens.
        var popBackScreen = mutableStateOf("")
        var popBackFragment = mutableStateOf("")
        var popBackSubmenu = mutableStateOf("")

        // Determine the next screen to open upon trigger.
        var pushScreen = mutableStateOf("")
        var pushFragment = mutableStateOf("")  // --- not used.
        var pushSubmenu = mutableStateOf("")  // --- not used.

        // Custom submenu global state for the tab "Services".
        var lastServicesSubmenu = mutableStateOf("")

        // Stores globally the state of the last opened main menu fragment.
        var lastMainScreenPagerPage = mutableStateOf("")

        /* The download status of the lib.Downloader's multithread. */
        var isPrivateDownloadComplete = mutableStateOf(false)

        /* Stores the path to the downloaded private file; used in lib.Downloader. */
        var pathToDownloadedPrivateFile = mutableStateOf("")

        /* ------------------------------------------------------------------------------------ */
        /* The following mutable variables are associated with internet downloads.
         * Their contents change dynamically according to a given download state. */

        val downloadedPathOf = mutableListOf(
            ""
        )

        /* ------------------------------------------------------------------------------------ */
        /* Initializing the global schema that does not directly trigger recomposition. */

        @SuppressLint("MutableCollectionMutableState")
        val ytViewerParameters = mutableMapOf<String, String>(
            /* These parameters are required for displaying the right content in the YouTube viewer. */
            "date" to "",
            "title" to "",
            "yt-id" to "",
        )

        /* This parameter is required for  manipulating the composition and the app's view. */
        // TODO: Find a way to use the app's context across functions without memory leak.
        @SuppressLint("StaticFieldLeak")
        var context: Context = AppCompatActivity()

    }
}
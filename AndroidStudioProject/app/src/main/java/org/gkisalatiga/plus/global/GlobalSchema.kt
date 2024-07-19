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
import androidx.compose.runtime.mutableStateOf

class GlobalSchema : Application() {
    override fun onCreate() { super.onCreate() }

    // Initializing the data schema of the app that will be shared across composables
    // and that will course the navigation of screens.
    companion object {

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

        /* ------------------------------------------------------------------------------------ */

        /* Initializing the global schema that does not directly trigger recomposition. */
        @SuppressLint("MutableCollectionMutableState")
        val norender = mutableMapOf<String, Any>(
            /* These parameters are required for displaying the right content. */
            "date" to 0,  // --- UNIX-style.
            "title" to "",
            "url" to "",

            /* These parameters are used in displaying a link confirmation dialog in the home screen. */
            "linkConfirmURL" to "",
            "linkConfirmTitle" to "",

            /* These parameters are required for  manipulating the composition and the app's view. */
            "context" to "",  // --- must be converted to an Android Context later on.
        )

    }
}
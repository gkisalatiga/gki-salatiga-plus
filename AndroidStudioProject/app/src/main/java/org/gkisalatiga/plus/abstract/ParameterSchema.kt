/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 * This class is the standardized schema for determining public function parameters
 * and passing payload between functions or methods.
 * This schema is especially useful for mutable objects and variables.
 * The class structure is adapted from the following,
 * SOURCE: https://stackoverflow.com/a/66178263
 * SOURCE: https://stackoverflow.com/a/69411013
 * SOURCE: https://developer.android.com/develop/ui/compose/performance/stability
 */

package org.gkisalatiga.plus.abstract

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavHostController

/**
 * @author github.com/groaking
 * @param title the title that describes this payload
 * @param url any arbitrary internet URL (whether in HTTP, HTTPS, FTP, or even Android Asset protocol)
 */

data class ParameterSchema (
    /* These parameters are required for displaying the right content. */
    // var date: Int,  // --- date in UNIX format.
    var title: String,
    var url: String,

    /* These parameters are used to navigate across screens, fragments, and submenus in the composables. */
    // Determines where to go when pressing the "back" button after changing screens.
    // var popBackScreen: String,
    // var popBackFragment: String,
    // var popBackSubmenu: String,

    /* These parameters are required for  manipulating the composition and the app's view. */
    // var context: Context,
)
/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the enumerated constant strings used in navigating between Composables.
 */

package org.gkisalatiga.plus.lib

public data class NavigationRoutes (
    // The individual Composable "screens" of the app.
    val SCREEN_MAIN: String = "nav_screen_main",
    val SCREEN_ABOUT: String = "nav_screen_about",
    val SCREEN_PROFILE: String = "nav_screen_profile",

    // The individual Composable "fragments" of each screen.
    // Note that we do not actually implement fragments, since we use Jetpack Compose.
    // "Fragment" is just an easy way to phrase "a container within a screen".
    val FRAG_MAIN_HOME: String = "nav_frag_home",
    val FRAG_MAIN_SERVICES: String = "nav_frag_services",
    val FRAG_MAIN_NEWS: String = "nav_frag_news",
    val FRAG_MAIN_EVENTS: String = "nav_frag_events",
    val FRAG_PROFILE_CHURCH: String = "nav_frag_church",
    val FRAG_PROFILE_PASTOR: String = "nav_frag_pastorate",
    val FRAG_PROFILE_ASSEMBLY: String = "nav_frag_assembly",
    val FRAG_PROFILE_MINISTRY: String = "nav_frag_ministry",
    val FRAG_ABOUT: String = "nav_frag_about",
    val FRAG_BLANK: String = "nav_frag_blank",
)
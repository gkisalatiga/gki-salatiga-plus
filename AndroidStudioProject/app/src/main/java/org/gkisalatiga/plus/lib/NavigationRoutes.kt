/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the enumerated constant strings used in navigating between Composables.
 */

package org.gkisalatiga.plus.lib

class NavigationRoutes {
    // The individual Composable "screens" of the app.
    public final val SCREEN_MAIN: String = "nav_screen_main"
    public final val SCREEN_ABOUT: String = "nav_screen_about"

    // The individual Composable "fragments" of each screen.
    // Note that we do not actually implement fragments, since we use Jetpack Compose.
    // "Fragment" is just an easy way to phrase "a container within a screen".
    public final val FRAG_MAIN_HOME: String = "nav_frag_home"
    public final val FRAG_MAIN_SERVICES: String = "nav_frag_services"
    public final val FRAG_MAIN_NEWS: String = "nav_frag_news"
    public final val FRAG_MAIN_EVENTS: String = "nav_frag_events"
    public final val FRAG_ABOUT: String = "nav_frag_about"
}
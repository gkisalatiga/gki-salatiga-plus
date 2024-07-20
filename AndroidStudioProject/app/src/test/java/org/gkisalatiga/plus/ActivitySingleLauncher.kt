/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 *
 * REFERENCES USED:
 *
 * Scaffold tutorial
 * SOURCE: https://www.jetpackcompose.net/scaffold
 *
 * Navigation between screens in Jetpack Compose
 * SOURCE: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740
 * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
 *
 * Navigation screen transition animation
 * SOURCE: https://stackoverflow.com/a/68749621
 *
 * You don't need a fragment nor separate activity in Jetpack Compose.
 * Each method can act as a separate container of an individual part.
 * SOURCE: https://stackoverflow.com/a/66378077
 *
 * On writing a clean code, it's pros and cons:
 * SOURCE: https://softwareengineering.stackexchange.com/a/29205
 */

package org.gkisalatiga.plus

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenProfile
import org.gkisalatiga.plus.ui.theme.GKISalatigaPlusTheme

class ActivitySingleLauncher : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable on-the-fly edit of drawable SVG vectors.
        // SOURCE: https://stackoverflow.com/a/38418049
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Preamble logging to the terminal.
        Log.d("Groaker", "Starting app: GKI Salatiga+")

        super.onCreate(savedInstanceState)
        setContent {
            val screenController = rememberNavController()
            // fragmentController is deprecated and will not be used in the future.
            // It is replaced with AnimatedVisibility that controls fragment visibility, because
            // for some reason, Composable does not support nested NavHosts.
            // ---
            // fragmentController continues to be passed to functions to ensure backward compatibility.
            val fragmentController = rememberNavController()
            val context = this

            GKISalatigaPlusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    initComposable(screenController, fragmentController, context)
                }
            }
        }
    }

    /**
     * This initializes the whole-screen "activity" of the app,
     * as well as the Composable navigation that navigates between Composable activities.
     * We really don't implement Activity and Fragment that much, since we use Jetpack Composable.
     *
     * This function also handles Composable navigation flow.
     */
    @Composable
    private fun initComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Log.d("Groaker", "Are we getting logged?")
        // This will manage the application's main "screens" (i.e., primary activities)
        NavHost(navController = screenController, startDestination = "${NavigationRoutes().SCREEN_MAIN}?") {
            composable(
                "${NavigationRoutes().SCREEN_MAIN}?{menu}&{submenu}",
                arguments = listOf(
                    navArgument("menu") {
                        nullable = true
                        type = NavType.StringType
                    },
                    navArgument("submenu") {
                        nullable = true
                        type = NavType.StringType
                    }
                )
            ) {
                // For backward-compatibility, the "menu" selection parameter is optional.
                var menu = requireNotNull(it.arguments).getString("menu")
                var submenu = requireNotNull(it.arguments).getString("submenu")

                // Mitigates OOB exception when trying to split the string.
                // For some unknown reason, both menu and submenu variables contain identical string.
                if (menu.toString().contains("&")) {
                    menu = menu.toString().split("&")[0]
                    submenu = submenu.toString().split("&")[1]
                }

                //ScreenMain(menu, submenu).getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().SCREEN_ABOUT) {
                //ScreenAbout().getComposable(screenController, fragmentController, context)
            }
            composable(
                "${NavigationRoutes().SCREEN_PROFILE}/{frag}",
                arguments = listOf(navArgument("frag") {
                    type = NavType.StringType
                })
            ) {
                val frag = requireNotNull(it.arguments).getString("frag")
                ScreenProfile(frag).getComposable(screenController, fragmentController, context)
            }
            composable(
                "${NavigationRoutes().SCREEN_WEBVIEW}/{dest}",
                arguments = listOf(navArgument("dest") {
                    type = NavType.StringType
                })
            ) {
                val dest = requireNotNull(it.arguments).getString("dest")
                // ScreenWebView(dest).getComposable(screenController, fragmentController, context)
            }
            composable(
                "${NavigationRoutes().SCREEN_VIDEO}/{dest}",
                arguments = listOf(navArgument("dest") {
                    type = NavType.StringType
                })
            ) {
                val dest = requireNotNull(it.arguments).getString("dest")
                // ScreenVideo(dest).getComposable(screenController, fragmentController, context)
            }
            composable(
                "${NavigationRoutes().SCREEN_LIVE}/{dest}?{submenu}",
                arguments = listOf(
                    navArgument("dest") {
                        type = NavType.StringType
                    },
                    navArgument("submenu") {
                        type = NavType.StringType
                    }
                )
            ) {
                val dest = requireNotNull(it.arguments).getString("dest")
                val submenu = requireNotNull(it.arguments).getString("submenu")
                // ScreenVideoLive(dest, submenu).getComposable(screenController, fragmentController, context)
            }
        }
    }
}
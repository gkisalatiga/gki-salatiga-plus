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
 */

package org.gkisalatiga.plus

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.gkisalatiga.plus.fragment.FragmentAbout
import org.gkisalatiga.plus.fragment.FragmentEvents
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentNews
import org.gkisalatiga.plus.fragment.FragmentServices

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.ui.theme.GKISalatigaPlusTheme

class ActivityLauncher : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screenController = rememberNavController()
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
        NavHost(navController = screenController, startDestination = NavigationRoutes().SCREEN_MAIN) {
            // This will manage the application's main "screens" (i.e., primary activities)
            composable(NavigationRoutes().SCREEN_MAIN) {
                ScreenMain().getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().SCREEN_ABOUT) {
                ScreenAbout().getComposable(screenController, fragmentController, context)
            }
        }

        // This will manage and navigate the main screen's Composable "fragment" contents.
        NavHost(navController = fragmentController, startDestination = NavigationRoutes().FRAG_MAIN_HOME) {
            composable(NavigationRoutes().FRAG_MAIN_HOME) {
                FragmentHome().getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().FRAG_MAIN_SERVICES) {
                FragmentServices().getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().FRAG_MAIN_NEWS) {
                FragmentNews().getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().FRAG_MAIN_EVENTS) {
                FragmentEvents().getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().FRAG_ABOUT) {
                FragmentAbout().getComposable(screenController, fragmentController, context)
            }
        }



        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            val curRoute = fragmentController.currentDestination?.route
            if (curRoute == NavigationRoutes().FRAG_MAIN_HOME) {
                Toast.makeText(context, "You just clicked $curRoute and exited the app!", Toast.LENGTH_SHORT).show()

                // Exit the application
                // SOURCE: https://stackoverflow.com/a/67402808
                this.finish()
            } else if (
                curRoute == NavigationRoutes().FRAG_MAIN_EVENTS ||
                curRoute == NavigationRoutes().FRAG_MAIN_NEWS ||
                curRoute == NavigationRoutes().FRAG_MAIN_SERVICES
            ) {
                // If we are in the main screen but not at fragment one, navigate the app to fragment one
                fragmentController.navigate(NavigationRoutes().FRAG_MAIN_HOME)
            } else {
                // By default, do press "back" as usual
                fragmentController.popBackStack()
                screenController.popBackStack()
            }
        }
    }
}
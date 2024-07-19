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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import org.gkisalatiga.plus.abstract.GlobalClass
import org.gkisalatiga.plus.abstract.ParameterSchema
import org.gkisalatiga.plus.global.GlobalSchema

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.ResetSchema
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenMain
// import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.screen.ScreenProfile
import org.gkisalatiga.plus.screen.ScreenVideo
import org.gkisalatiga.plus.screen.ScreenVideoLive
import org.gkisalatiga.plus.screen.ScreenWebView
import org.gkisalatiga.plus.ui.theme.GKISalatigaPlusTheme

class ActivityLauncher : ComponentActivity() {

    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable on-the-fly edit of drawable SVG vectors.
        // SOURCE: https://stackoverflow.com/a/38418049
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Preamble logging to the terminal.
        Log.d("Groaker", "Starting app: ${this.resources.getString(R.string.app_name_alias)}")

        // Call the superclass. (The default behavior. DO NOT CHANGE!)
        super.onCreate(savedInstanceState)

        // Determine the default screen, fragment, and submenu to open upon first app launch.
        val defaultScreenLaunch = NavigationRoutes().SCREEN_MAIN
        val defaultFragmentLaunch = NavigationRoutes().FRAG_MAIN_HOME
        val defaultServicesSubmenu = NavigationRoutes().SUB_KEBAKTIAN_ES

        // Setting some of the most important default values of the global schema.
        // (i.e., the composable navigation direction.)
        GlobalSchema.pushScreen.value = defaultScreenLaunch
        // GlobalSchema.pushFragment.value = defaultFragmentLaunch
        // GlobalSchema.pushScreen.value = defaultScreenLaunch
        GlobalSchema.lastMainScreenPagerPage.value = defaultFragmentLaunch
        GlobalSchema.lastServicesSubmenu.value = defaultServicesSubmenu

        // Setting the global context value.
        GlobalSchema.norender["context"] = this

        // Initiate the Jetpack Compose composition.
        // This is the entry point of every composable, similar to "main()" function in Java.
        setContent {

            // This variable allows one to control
            // whether the splash screen should be displayed upon launch.
            val showSplash = true

            if (showSplash) {
                // Splash screen.
                // SOURCE: https://medium.com/@fahadhabib01/animated-splash-screens-in-jetpack-compose-navigation-component-4e28f69ad559
                Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                    val splashNavController = rememberNavController()
                    NavHost(navController = splashNavController, startDestination = "splash_screen") {
                        composable("splash_screen") {
                            initSplashScreen(splashNavController = splashNavController)
                        }
                        composable("main_screen") {
                            initMainGraphic()
                        }
                    }
                }
            } else {
                // Just display the main graphic directly.
                initMainGraphic()
            }
        }
    }

    /**
     * This method determines what is shown during splash screen.
     * @param schema the app's custom configurable schema, shared across composables.
     */
    @Composable
    private fun initSplashScreen(splashNavController: NavHostController) {
        Log.d("Groaker", "Loading splash screen of the app ...")

        val scale = remember { androidx.compose.animation.core.Animatable(0f) }
        LaunchedEffect(key1 = true) {
            scale.animateTo(targetValue = 0.9f, animationSpec = tween(durationMillis = 1000, easing = { OvershootInterpolator(2f).getInterpolation(it) }))

            // Determines the duration of the splash screen.
            delay(1000L)
            splashNavController.navigate("main_screen")
        }

        // Displays the splash screen content.
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Image(painter = painterResource(id = R.drawable.baseline_filter_drama_512), contentDescription = "Logo", modifier = Modifier.scale(scale.value))
        }
    }

    /**
     * This method will become the navigation hub of screens across composables.
     * It also becomes the graphical base of all screens.
     */
    @Composable
    private fun initMainGraphic() {
        Log.d("Groaker", "Initializing main graphic ...")
        Log.d("Groaker", "Obtained 'pushScreen' value: ${GlobalSchema.pushScreen.value}")

        // Watch for the state change in the parameter "pushScreen".
        // SOURCE: https://stackoverflow.com/a/73129228
        key(GlobalSchema.pushScreen.value) {
            when(GlobalSchema.pushScreen.value) {
                NavigationRoutes().SCREEN_MAIN -> { ScreenMain().getComposable() }
                NavigationRoutes().SCREEN_ABOUT -> { ScreenAbout().getComposable() }
            }
        }
    }



    /*
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

                ScreenMain(menu, submenu).getComposable(screenController, fragmentController, context)
            }
            composable(NavigationRoutes().SCREEN_ABOUT) {
                ScreenAbout().getComposable(screenController, fragmentController, context)
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
                ScreenWebView(dest).getComposable(screenController, fragmentController, context)
            }
            composable(
                "${NavigationRoutes().SCREEN_VIDEO}/{dest}",
                arguments = listOf(navArgument("dest") {
                    type = NavType.StringType
                })
            ) {
                val dest = requireNotNull(it.arguments).getString("dest")
                ScreenVideo(dest).getComposable(screenController, fragmentController, context)
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
                ScreenVideoLive(dest, submenu).getComposable(screenController, fragmentController, context)
            }
        }
    }
 */
}
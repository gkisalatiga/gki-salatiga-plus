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
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.Downloader

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenForms
import org.gkisalatiga.plus.screen.ScreenLiturgi
import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.screen.ScreenSaRen
import org.gkisalatiga.plus.screen.ScreenVideo
import org.gkisalatiga.plus.screen.ScreenVideoLive
import org.gkisalatiga.plus.screen.ScreenWarta
import org.gkisalatiga.plus.screen.ScreenWebView
import org.gkisalatiga.plus.screen.ScreenYKB
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// import org.gkisalatiga.plus.screen.ScreenMain

class ActivityLauncher : ComponentActivity() {

    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable transparent status bar.
        // SOURCE: https://youtu.be/Ruu44ZUhkBM?si=KTtR2GjZdqMa-rBs
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )


        // Enable on-the-fly edit of drawable SVG vectors.
        // SOURCE: https://stackoverflow.com/a/38418049
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Preamble logging to the terminal.
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Starting app: ${this.resources.getString(R.string.app_name_alias)}")

        // Call the superclass. (The default behavior. DO NOT CHANGE!)
        super.onCreate(savedInstanceState)

        // Determine the default screen, fragment, and submenu to open upon first app launch,
        // as well as other pre-determined default values.
        val defaultScreenLaunch = NavigationRoutes().SCREEN_MAIN
        val defaultFragmentLaunch = NavigationRoutes().FRAG_MAIN_HOME
        val defaultServicesSubmenu = NavigationRoutes().SUB_BLANK
        val defaultNewTopBarBackground = R.drawable.topbar_greetings_home

        // Setting some of the most important default values of the global schema.
        // (i.e., the composable navigation direction.)
        GlobalSchema.pushScreen.value = defaultScreenLaunch
        GlobalSchema.lastMainScreenPagerPage.value = defaultFragmentLaunch
        GlobalSchema.lastServicesSubmenu.value = defaultServicesSubmenu

        // The top bar greeting background.
        GlobalSchema.lastNewTopBarBackground.value = defaultNewTopBarBackground

        // Setting the global context value.
        GlobalSchema.context = this

        // Setting the clipboard manager.
        // Should be performed within "onCreate" to avoid the following error:
        // java.lang.IllegalStateException: System services not available to Activities before onCreate()
        GlobalSchema.clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // Retrieving the latest JSON metadata.
        initMetaData()

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
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Loading splash screen of the app ...")

        val scale = remember { androidx.compose.animation.core.Animatable(1.6f) }
        LaunchedEffect(key1 = true) {
            scale.animateTo(targetValue = 0.4f, animationSpec = tween(durationMillis = 1000, easing = { FastOutSlowInEasing.transform(it) /*OvershootInterpolator(2f).getInterpolation(it)*/ }))

            // Determines the duration of the splash screen.
            delay(100)
            splashNavController.navigate("main_screen")
        }

        // Displays the splash screen content.
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(Color(0xff071450))) {
            Image(painter = painterResource(id = R.drawable.splash_screen_foreground), contentDescription = "Splash screen logo", modifier = Modifier.scale(scale.value))
        }
    }

    /**
     * This method will become the navigation hub of screens across composables.
     * It also becomes the graphical base of all screens.
     */
    @Composable
    private fun initMainGraphic() {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) {
            Log.d("Groaker", "Initializing main graphic ...")
            Log.d("Groaker", "Obtained 'pushScreen' value: ${GlobalSchema.pushScreen.value}")
        }

        // Watch for the state change in the parameter "pushScreen".
        // SOURCE: https://stackoverflow.com/a/73129228
        key(GlobalSchema.pushScreen.value) {
            when(GlobalSchema.pushScreen.value) {
                NavigationRoutes().SCREEN_MAIN -> { ScreenMain().getComposable() }
                NavigationRoutes().SCREEN_ABOUT -> { ScreenAbout().getComposable() }
                NavigationRoutes().SCREEN_PRERECORDED -> { ScreenVideo().getComposable() }
                NavigationRoutes().SCREEN_LIVE -> { ScreenVideoLive().getComposable() }
                NavigationRoutes().SCREEN_FORMS -> { ScreenForms().getComposable() }
                NavigationRoutes().SCREEN_YKB -> { ScreenYKB().getComposable() }
                NavigationRoutes().SCREEN_SAREN -> { ScreenSaRen().getComposable() }
                NavigationRoutes().SCREEN_WARTA -> { ScreenWarta().getComposable() }
                NavigationRoutes().SCREEN_LITURGI -> { ScreenLiturgi().getComposable() }
                NavigationRoutes().SCREEN_WEBVIEW -> { ScreenWebView().getComposable() }
            }
        }
    }

    /**
     * This app prepares the downloading of JSON metadata.
     * It should always be performed at the beginning of app to ensure updated content.
     * ---
     * This function does not need to become a composable function since it requires no UI.
     */
    private fun initMetaData() {

        // Upon successful metadata download, we manage the app's internal variable storage
        // according to the downloaded JSON file's schema.
        // We also make any appropriate settings accordingly.
        // ---
        // This is all done in a multi-thread so that we do not interrupt the main GUI.
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Create the JSON manager object.
            val appDB = AppDatabase()

            // Let's apply the fallback JSON data until the actual, update JSON metadata is downloaded.
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Loading the fallback JSON metadata ...")
            GlobalSchema.globalJSONObject = appDB.getFallbackMainData()

            // Set the flag to "false" to signal that we need to have the new data now.
            GlobalSchema.isJSONMetaDataInitialized.value = false

            while (true) {

                // Make the attempt to download the JSON file.
                Downloader().initMetaData()

                if (GlobalSchema.isJSONMetaDataInitialized.value) {
                    // Since the JSON metadata has now been downloaded, let's assign the actual JSON globally.
                    GlobalSchema.globalJSONObject = appDB.getMainData()
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Successfully refreshed the JSON data!")

                    // It is finally set-up. Let's break free from this loop.
                    break
                } else {
                    // Sleep for a couple of milliseconds before continuing.
                    // SOURCE: http://stackoverflow.com/questions/24104313/ddg#24104427
                    TimeUnit.SECONDS.sleep(1);
                    continue
                }

            }
        }
    }

}
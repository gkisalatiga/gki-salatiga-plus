/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load the live video URL passed by the navigation parameter.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import org.gkisalatiga.plus.R

import org.gkisalatiga.plus.lib.NavigationRoutes

/**
 * @param destination the destination live video URL that needs to be displayed.
 */
class ScreenVideoLive(destination: String?, submenu: String?) : ComponentActivity() {
    private val destination = destination
    private val submenu = submenu

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Scaffold (
            topBar = { this.getTopBar(screenController, fragmentController, context) }
                ) {
            // Hiding/displaying a certain fragment based on the passed argument.
            Toast.makeText(context, "Called fragment: $destination", Toast.LENGTH_SHORT).show()
            if (destination != null) {
            }

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                Text("Let us watch this streaming: $destination")
            }
        }

        // Ensures that we always land where we started.
        BackHandler {
            // When the user clicks "back", we navigate to the "services" tab instead of the "home" tab
            screenController.navigate("${NavigationRoutes().SCREEN_MAIN}?${NavigationRoutes().FRAG_MAIN_SERVICES}&${submenu}")
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(R.string.screenvideo_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    // When the user clicks "back", we navigate to the "services" tab instead of the "home" tab
                    screenController.navigate("${NavigationRoutes().SCREEN_MAIN}?${NavigationRoutes().FRAG_MAIN_SERVICES}&${submenu}")
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Localiszes desc"
                    )
                }
            },
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }
}
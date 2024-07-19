/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.gkisalatiga.plus.lib.AppDatabase

class FragmentAbout : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    /**
     * Navigation between screens
     * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
     */
    @Composable
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        // Setting the layout to center both vertically and horizontally
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        // ---
        // Enabling vertical scrolling
        // SOURCE: https://stackoverflow.com/a/72769561
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            val fragmentName = fragmentController.currentDestination?.route
            Text("This fragment is called: $fragmentName")

            // val jsonData = AppDatabase().loadDebug(context).getMainData().getJSONObject("home").getString("welcome-banner")
            val jsonData = "ABCADA"
            Text("Your JSON data: " + jsonData)
        }
    }
}
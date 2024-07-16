/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.gkisalatiga.plus.R

class FragmentEvents : ComponentActivity() {
    // The state of the currently selected chip.
    // (The default is to set the first chip as the one initially selected.)
    var selectedChip = listOf(
        mutableStateOf(true),
        mutableStateOf(false)
    )

    // The names of the chips.
    var nameOfChip = listOf(
        "Detil Harian",
        "Bulanan"
    )

    /**
     * Navigation between screens
     * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
     */
    @Composable
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        // Setting the layout to center both vertically and horizontally
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            // Show the current screen's main title.
            val fragmentName = fragmentController.currentDestination?.route
            Text("Senin", fontWeight = FontWeight.Bold, fontSize = 24.sp)

            // Sample text.
            val sampleText = listOf(
                "Latihan paduan suara",
                "Kebaktian I",
                "Kebaktian Berbahasa Arab"
            )

            // Show each individual event
            LazyColumn {
                items (3) {
                    Card (
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp).padding(horizontal = 5.dp)
                    ) {
                        Column (modifier = Modifier.padding(10.dp)) {
                            Text(sampleText[it], fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(5.dp), textAlign = TextAlign.Center)
                            Row (verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_groups_24),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxHeight().padding(horizontal = 5.dp).width(40.dp),
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.FillHeight
                                )
                                Text("Komisi Musik dan Liturgi")
                            }
                            Row (verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_location_on_24),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxHeight().padding(horizontal = 5.dp).width(40.dp),
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.FillHeight
                                )
                                Text("d'Emmerick Hotel")
                            }
                            Row (verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_access_time_24),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxHeight().padding(horizontal = 5.dp).width(40.dp),
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.FillHeight
                                )
                                Text("Jam 12 malam")
                            }
                        }
                    }
                }
            }
        }
    }
}
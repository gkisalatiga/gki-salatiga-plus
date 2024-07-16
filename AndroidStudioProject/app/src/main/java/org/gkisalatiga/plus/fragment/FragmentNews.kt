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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.lib.DownloadAndSaveImageTask

class FragmentNews : ComponentActivity() {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                items (10) {
                    Card (
                        onClick = { Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp).padding(horizontal = 10.dp)
                    ) {
                        Row ( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp) ) {
                            Image(
                                painter = painterResource(R.drawable.baseline_article_24),
                                contentDescription = "Some name",
                                modifier = Modifier.fillMaxHeight(),
                                alignment = Alignment.Center,
                                contentScale = ContentScale.FillHeight
                            )
                            Column {
                                Text("GKI Salatiga Membeli 5 Kamera PTZ Baru", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                                Text("32 April 2024 sebelum masehi")
                            }
                        }
                    }
                }
            }
        }
    }
}
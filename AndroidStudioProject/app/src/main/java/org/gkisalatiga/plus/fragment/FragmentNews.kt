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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.gkisalatiga.plus.R

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
                        modifier = Modifier.fillMaxWidth().padding(top = 5.dp).padding(horizontal = 5.dp)
                    ) {
                        Row ( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp) ) {
                            Image(
                                painter = painterResource(R.drawable.baseline_article_24),
                                contentDescription = "Some name",
                                alignment = Alignment.Center,
                            )
                            Column {
                                Text("McDonalds, Tong Tji, dan Saloka menjadi sponsor Bulan Keluarga 2024 GKI Salatiga", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp))
                                Text("32 April 2012 sebelum masehi", modifier = Modifier.padding(horizontal = 10.dp))
                                Text("Komisi Pelayanan dan Pekabaran Injil", fontStyle = FontStyle.Italic, modifier = Modifier.padding(horizontal = 10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
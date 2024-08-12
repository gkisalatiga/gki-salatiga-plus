/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom HTML body.
 * Only those HTML contents stored in the JSON schema's "data/static" node can be displayed.
 */

package org.gkisalatiga.plus.screen

import android.content.ClipData
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.json.JSONObject

class ScreenPersembahan() : ComponentActivity() {

    @Composable
    public fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
                ) {

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
        }

    }

    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current

        // The JSON node.
        val persembahanJSONArray = AppDatabase().getMainData().getJSONArray("offertory")

        // The column's saved scroll state.
        val scrollState = GlobalSchema.screenPersembahanScrollState!!
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(20.dp)
        ) {
            /* QRIS title. */
            Text(
                stringResource(R.string.section_title_qris),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )

            /* Display the banner image. */
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Box {
                    /* The base background for the transparent PNG. */
                    Box (Modifier.background(Color(0xffffffff)).matchParentSize()) {}

                    /* The transparent QR code. */
                    AsyncImage(
                        GlobalSchema.offertoryQRISImageSource,
                        contentDescription = "The QRIS code image to GKI Salatiga offertory account",
                        error = painterResource(R.drawable.thumbnail_loading),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            /* Other payment method title. */
            Text(
                stringResource(R.string.section_title_other_payment_method),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )

            // Iterate through every offertory option.
            var isFirstElement = true
            for (index in 0 until persembahanJSONArray.length()) {
                if (isFirstElement) {
                    HorizontalDivider()
                    isFirstElement = false
                }

                val currentNode = persembahanJSONArray[index] as JSONObject
                val notificationString = stringResource(R.string.offertory_number_copied)
                ListItem(
                    leadingContent = { Icon(Icons.Default.QrCodeScanner, "", modifier = Modifier.size(60.dp)) },
                    overlineContent = { Text( currentNode.getString("bank-name") ) },
                    headlineContent = {
                        val headlineText = "${currentNode.getString("bank-abbr")} ${currentNode.getString("bank-number")}"
                        Text(headlineText, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = { Text("a.n. ${currentNode.getString("account-holder")}") },
                    modifier = Modifier.clickable(onClick = {
                        // Attempt to copy text to clipboard.
                        // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                        val clipData = ClipData.newPlainText("text", currentNode.getString("bank-number").replace(".", ""))
                        GlobalSchema.clipManager!!.setPrimaryClip(clipData)

                        Toast.makeText(ctx, notificationString, Toast.LENGTH_SHORT).show()
                    })
                )
                HorizontalDivider()
            }
        }  // --- end of scrollable column.
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(R.string.screenoffertory_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }

}
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

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONObject

class ScreenGaleriYear : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
        }

    }

    @Composable
    private fun getMainContent() {

        // The agenda node.
        val galleryNode = GlobalSchema.globalGalleryObject!!

        // Enlist the list of albums in the currently selected year.
        val galleryYearList = galleryNode.getJSONArray(GlobalSchema.targetGalleryYear)

        // DEBUG. Always comment out.
        Log.d("Groaker-Test", "Current object (1): ${galleryYearList}")

        // Convert JSONArray to regular list. (JSONArray iterates from 1, not 0.)
        val enumeratedGalleryList: MutableList<JSONObject> =  mutableListOf(JSONObject())
        for (i in 0 until galleryYearList.length()) {
            val curNode = galleryYearList[i] as JSONObject
            enumeratedGalleryList.add(curNode)

            // DEBUG. Always comment out.
            Log.d("Groaker-Test", "Current object (2): ${curNode}")
        }

        // Remove the first item; JSONArrays start at 1.
        enumeratedGalleryList.removeAt(0)

        // The column's saved scroll state.
        val scrollState = rememberScrollState()
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = R.drawable.banner_gallery
            val imgDescription = "Menu banner"
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(LocalContext.current.resources.getDimension(R.dimen.banner_inner_padding).dp).padding(bottom = 10.dp)
            ) {
                Image(
                    painter = painterResource(imgSource),
                    contentDescription = imgDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }

            Log.d("Groaker-Test", "Current object (3): ${enumeratedGalleryList}")

            /* Draw the form selection elements. */
            enumeratedGalleryList.forEach {
                Log.d("Groaker-Test", "Current object (4): ${it}")

                // Determining the text title.
                val title = it["title"].toString()

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText((GlobalSchema.context), "Opening gallery album year: $title", Toast.LENGTH_SHORT).show()

                        /*// Set this screen as the anchor point for "back"
                        GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_GALERI_YEAR

                        // Navigate to the WebView viewer.
                        GlobalSchema.targetGalleryYear = it.toString()
                        GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_GALERI_YEAR*/
                    },
                    modifier = Modifier.padding(bottom = 10.dp).height(65.dp)
                ) {
                    Row ( modifier = Modifier.padding(5.dp).fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically ) {
                        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(start = 5.dp).weight(3f))
                        Spacer( modifier = Modifier.weight(1f) )
                        // The "arrow forward" icon.
                        Icon(Icons.AutoMirrored.Default.ArrowForward, "", modifier = Modifier.padding(vertical = 5.dp).padding(end = 5.dp).fillMaxHeight())
                    }
                }  // --- end of card.
            }  // --- end of forEach.

        }  // --- end of column.

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val topBarTitle = "Kilas Balik Tahun " + GlobalSchema.targetGalleryYear
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    topBarTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                    GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
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
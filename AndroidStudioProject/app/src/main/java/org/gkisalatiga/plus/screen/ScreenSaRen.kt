/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the daily devotional video of the morning.
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
import androidx.compose.foundation.layout.width
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
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONObject

class ScreenSaRen() : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {

        Scaffold (
            topBar = { getTopBar() }
                ) {
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
        }

    }
    
    @Composable
    private fun getMainContent() {
        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = R.drawable.banner_saren
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

            /* Retrieve the list of devotionals. */
            val formListAsJSONArray = AppDatabase().getMainData().getJSONObject("yt-video").getJSONArray("saren")

            /* Enumerate and enlist the individual card. */
            val enumeratedFormList: MutableList<Map<String, String>> =  mutableListOf(emptyMap<String, String>())
            for (i in 0 until formListAsJSONArray.length()) {
                val curNode = formListAsJSONArray[i] as JSONObject
                enumeratedFormList.add(mapOf(
                    "title" to curNode.getString("title"),
                    "date" to curNode.getString("date"),
                    "link" to curNode.getString("link"),
                    "thumbnail" to curNode.getString("thumbnail")
                ))
            }

            // For some reason, we must pop the 0-th item in cardsList
            // because JSONArray iterates from 1, not 0.
            enumeratedFormList.removeAt(0)

            /* Draw the news selection elements. */
            enumeratedFormList.forEach {

                // Preparing the arguments.
                val title = it["title"]
                val date = StringFormatter().convertDateFromJSON(it["date"]!!)
                val url = it["link"]!!
                val thumbnail = StringFormatter().getYouTubeThumbnailFromUrl( it["link"]!! )

                // Displaying the individual card.
                Card(
                    onClick = {
                        Toast.makeText((GlobalSchema.context), "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Set this screen as the anchor point for "back"
                        GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_SAREN

                        // Trying to switch to the YouTube viewer and open the stream.
                        Log.d("Groaker", "Opening YouTube stream: $url.")
                        GlobalSchema.ytViewerParameters["yt-link"] = url
                        GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                        GlobalSchema.ytViewerParameters["title"] = title!!
                        GlobalSchema.ytViewerParameters["date"] = date!!
                        GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_PRERECORDED
                    },
                    modifier = Modifier.padding(bottom = 10.dp).height(65.dp)
                ) {
                    Row ( modifier = Modifier.padding(0.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically ) {
                        // Displaying the thumbnail image.
                        AsyncImage(
                            model = thumbnail,
                            contentDescription = title,
                            error = painterResource(R.drawable.thumbnail_loading),
                            modifier = Modifier.weight(0.8f).fillMaxHeight(),
                            contentScale = ContentScale.Fit,
                        )
                        Text(title!!, fontSize = 20.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(start = 5.dp).weight(3f))
                    }
                }

            }

        }  // --- end of column.
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
                    stringResource(R.string.screensaren_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
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
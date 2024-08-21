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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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

class ScreenVideoList : ComponentActivity() {

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
            GlobalSchema.pushScreen.value = GlobalSchema.ytVideoListDispatcher
        }

    }
    
    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current

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

            /* Retrieve the list of video content. */
            val listOfVideoContent = GlobalSchema.videoListContentArray

            /* Display the banner image. */
            if (listOfVideoContent.size >= 1) {
                Surface (
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.aspectRatio(1.77778f),
                    onClick = {
                        // Preparing the arguments.
                        val title = listOfVideoContent[0].getString("title")
                        val date = StringFormatter().convertDateFromJSON(listOfVideoContent[0].getString("date"))
                        val url = listOfVideoContent[0].getString("link")
                        val desc = listOfVideoContent[0].getString("desc")
                        val thumbnail = listOfVideoContent[0].getString("thumbnail")

                        // Debug logging.
                        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Set this screen as the anchor point for "back"
                        GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_VIDEO_LIST

                        // Trying to switch to the YouTube viewer and open the stream.
                        Log.d("Groaker", "Opening YouTube stream: $url.")
                        GlobalSchema.ytViewerParameters["yt-link"] = url
                        GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                        GlobalSchema.ytViewerParameters["title"] = title!!
                        GlobalSchema.ytViewerParameters["date"] = date
                        GlobalSchema.ytViewerParameters["desc"] = desc
                        GlobalSchema.ytViewerParameters["thumbnail"] = thumbnail
                        GlobalSchema.ytCurrentSecond.floatValue = 0.0f
                        GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_LIVE
                    }
                ) {
                    AsyncImage(
                        listOfVideoContent[0].getString("thumbnail"),
                        contentDescription = "",
                        error = painterResource(R.drawable.thumbnail_loading),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            /* Draw the news selection elements. */
            listOfVideoContent.forEach {

                // Preparing the arguments.
                val title = it.getString("title")
                val date = StringFormatter().convertDateFromJSON(it.getString("date"))
                val url = it.getString("link")
                val desc = it.getString("desc")
                val thumbnail = it.getString("thumbnail")

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Set this screen as the anchor point for "back"
                        GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_VIDEO_LIST

                        // Trying to switch to the YouTube viewer and open the stream.
                        Log.d("Groaker", "Opening YouTube stream: $url.")
                        GlobalSchema.ytViewerParameters["yt-link"] = url
                        GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                        GlobalSchema.ytViewerParameters["title"] = title!!
                        GlobalSchema.ytViewerParameters["date"] = date
                        GlobalSchema.ytViewerParameters["desc"] = desc
                        GlobalSchema.ytViewerParameters["thumbnail"] = thumbnail
                        GlobalSchema.ytCurrentSecond.floatValue = 0.0f
                        GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_LIVE
                    },
                    modifier = Modifier.padding(bottom = 10.dp).height(65.dp)
                ) {
                    Row ( modifier = Modifier.padding(0.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically ) {
                        // Displaying the thumbnail image.
                        AsyncImage(
                            model = thumbnail,
                            contentDescription = title,
                            error = painterResource(R.drawable.thumbnail_loading),
                            modifier = Modifier.aspectRatio(1.77778f).fillMaxHeight(),
                            contentScale = ContentScale.Crop,
                        )
                        Text(title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            minLines = 1,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
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
                    GlobalSchema.videoListTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.ytVideoListDispatcher
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
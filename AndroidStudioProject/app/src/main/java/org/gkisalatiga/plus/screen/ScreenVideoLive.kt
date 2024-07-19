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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema

import org.gkisalatiga.plus.lib.NavigationRoutes


class ScreenVideoLive() : ComponentActivity() {

    // The YouTube video viewer object.
    var view: YouTubePlayerView? = null

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
                ) {

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                Column {

                    // Embedding the YouTube video into the composable.
                    // SOURCE: https://dev.to/mozeago/jetpack-compose-loadinghow-to-load-a-youtube-video-or-youtube-livestream-channel-to-your-android-application-4ffc
                    val youtubeVideoID = GlobalSchema.ytViewerParameters["yt-id"]
                    val ctx = LocalContext.current
                    AndroidView(factory = {
                        view = YouTubePlayerView(it)
                        val fragment = view!!.addYouTubePlayerListener(
                            object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    super.onReady(youTubePlayer)
                                    youTubePlayer.loadVideo(youtubeVideoID!!, 0f)
                                }
                            }
                        )
                        view!!
                    })

                    Text(GlobalSchema.ytViewerParameters["title"]!!, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    Text(GlobalSchema.ytViewerParameters["date"]!!)

                }
            }

        }

        // Ensures that we always land where we started.
        BackHandler {
            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
            view!!.release()
        }
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
                    stringResource(R.string.screenlive_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
                    view!!.release()
                }) { Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "") }
            },
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }

}
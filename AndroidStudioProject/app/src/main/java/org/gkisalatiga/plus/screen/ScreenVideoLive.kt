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
import android.content.ClipData
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema

import org.gkisalatiga.plus.lib.NavigationRoutes

class ScreenVideoLive() : ComponentActivity() {

    // The YouTube video viewer object.
    var view: YouTubePlayerView? = null

    // The trigger to open an URL in an external browser.
    private var doTriggerBrowserOpen = mutableStateOf(false)

    // Controls, from an outside composable, whether to display the link confirmation dialog.
    private var showLinkConfirmationDialog = mutableStateOf(false)

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
                ) {

            // Display the necessary content.
            Box ( modifier= Modifier
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .background(color = colorResource(R.color.brown_900))
                .fillMaxHeight()) {
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
                    //jeff 10.25
                    Spacer(Modifier.height(8.dp))
                    Text(GlobalSchema.ytViewerParameters["title"]!!,Modifier.absolutePadding(left = 20.dp, right = 20.dp), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.White)
                    Text(GlobalSchema.ytViewerParameters["date"]!!, Modifier.absolutePadding(left = 20.dp, right = 20.dp), color = Color(0xfffcfcfc))
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .absolutePadding(left = 10.dp, right = 10.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .height(300.dp)
                            .background(color = colorResource(R.color.grey_1))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(GlobalSchema.ytViewerParameters["desc"]!!, Modifier.absolutePadding(left = 10.dp, right = 10.dp), fontSize = 14.sp, lineHeight = 1.em, color = Color.White)
                        }
                        // Add other composables inside the Box if needed
                    }

                }
            }

        }

        // Display the external link open confirm dialog.
        getLinkConfirmationDialog()

        // Handles opening URLs in external browser.
        key(doTriggerBrowserOpen.value) {
            if (doTriggerBrowserOpen.value) {
                // Opens in an external browser.
                // SOURCE: https://stackoverflow.com/a/69103918
                LocalUriHandler.current.openUri(GlobalSchema.ytViewerParameters["yt-link"]!!)

                doTriggerBrowserOpen.value = false
            }
        }

        // Ensures that we always land where we started.
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
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
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                    view!!.release()
                }) { Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "") }
            },
            actions = {
                IconButton(onClick = {
                    showLinkConfirmationDialog.value = true
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.OpenInNew,
                        contentDescription = ""
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    /**
     * This function displays the confirmation dialog that asks the user
     * whether the user wants to proceed opening a certain link.
     * SOURCE: https://www.composables.com/tutorials/dialogs
     * SOURCE: https://developer.android.com/develop/ui/compose/components/dialog
     */
    @Composable
    private fun getLinkConfirmationDialog() {
        val ctx = LocalContext.current
        val notificationText = stringResource(R.string.yt_visit_link_link_copied)
        if (showLinkConfirmationDialog.value) {
            AlertDialog(
                onDismissRequest = { showLinkConfirmationDialog.value = false },
                title = { Text(stringResource(R.string.yt_visit_link_confirmation_title), fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                text = {
                    Column {
                        Text(stringResource(R.string.yt_visit_link_confirmation_subtitle) )
                        Surface (modifier = Modifier.fillMaxWidth(), color = Color.Transparent, onClick = {
                            // Attempt to copy text to clipboard.
                            // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                            val clipData = ClipData.newPlainText("text", GlobalSchema.ytViewerParameters["yt-link"])
                            GlobalSchema.clipManager!!.setPrimaryClip(clipData)

                            Toast.makeText(ctx, notificationText, Toast.LENGTH_SHORT).show()
                        }) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = GlobalSchema.ytViewerParameters["yt-link"]!!,
                                onValueChange = { /* NOTHING */ },
                                label = { Text("-") },
                                enabled = false,
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLinkConfirmationDialog.value = false }) {
                        Text(stringResource(R.string.yt_visit_link_cancel_btn).uppercase())
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showLinkConfirmationDialog.value = false
                        doTriggerBrowserOpen.value = true
                    }) {
                        Text(stringResource(R.string.yt_visit_link_proceed_btn).uppercase())
                    }
                }
            )
        }
    }

}
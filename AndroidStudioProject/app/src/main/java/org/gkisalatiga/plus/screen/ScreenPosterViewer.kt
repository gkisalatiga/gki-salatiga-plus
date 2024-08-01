/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom poster image.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ScreenPosterViewer() : ComponentActivity() {

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
            GlobalSchema.fragmentHomePosterDialogState.value = true
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
        }

    }

    @Composable
    @SuppressLint("SetJavaScriptEnabled", "ComposableNaming")
    private fun getMainContent() {

        // Declare the local image path that will be displayed.
        val targetPosterSource = GlobalSchema.posterDialogImageSource.value

        // Converting the image into base64 string in order to display it without "file://" scheme.
        // SOURCE: https://stackoverflow.com/a/4830846
        val bm = BitmapFactory.decodeFile(targetPosterSource)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val b = baos.toByteArray()
        val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)

        // Create the HTML body.
        // SOURCE: https://developer.android.com/develop/ui/views/layout/webapps/load-local-content#loadDataWithBaseUrl
        val HTMLBody = """
            <body style="padding: 0px; margin: 0px">
                <img src="data:image/png;base64,$encodedImage" style="position: absolute; height: 100%; width: 100%; border: none; margin: 0px; padding: 0px;"></img>
            </body>
        """.trimIndent()
        val encodedHTMLBody = Base64.encodeToString(HTMLBody.toByteArray(), Base64.NO_PADDING)

        /* Displaying the web view.
         * SOURCE: https://medium.com/@kevinnzou/using-webview-in-jetpack-compose-bbf5991cfd14 */
        // Adding a WebView inside AndroidView with layout as full screen
        AndroidView(factory = {
            WebView(it).apply {
                val wv = this
                wv.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Disable text selection and haptic feedback caused by long press.
                // This also disables copy-pasting of HTML text.
                // SOURCE: https://stackoverflow.com/a/12793740
                wv.setOnLongClickListener(View.OnLongClickListener { true })
                wv.isLongClickable = false
                wv.isHapticFeedbackEnabled = false

                // Enables JavaScript.
                // SOURCE: https://stackoverflow.com/a/69373543
                wv.settings.javaScriptEnabled = true

                // Enable pinching and zooming.
                // SOURCE: https://www.perplexity.ai/search/android-webview-how-to-enable-m7wBk07KS2GFsAk0cw1t4g
                // SOURCE: https://stackoverflow.com/a/7172165
                // SOURCE: https://stackoverflow.com/a/33784686
                // SOURCE: https://stackoverflow.com/a/26115592
                wv.settings.loadWithOverviewMode = true
                wv.settings.builtInZoomControls = true
                wv.settings.displayZoomControls = false
                wv.settings.useWideViewPort = false
                wv.settings.setSupportZoom(true)

                // Allow opening local file paths.
                // SOURCE: https://www.perplexity.ai/search/can-webview-access-html-in-the-dECz59cCQLugKCIpo.5dqg
                wv.settings.allowFileAccess = true
            }
        }, update = {
            // Load the local HTML content.
            it.loadData(encodedHTMLBody, "text/html", "base64")
        })

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
                    GlobalSchema.posterDialogTitle.value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.fragmentHomePosterDialogState.value = true
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
/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.NavigationRoutes
import java.io.File

class FragmentInfo : ComponentActivity() {

    // The trigger to open an URL in an external browser.
    private var doTriggerBrowserOpen = mutableStateOf(false)

    // The link to open in an external browser or app.
    private var externalLinkURL = mutableStateOf("https://www.example.com")

    // The JSON node of the social media CTA.
    private val socialMediaJSONNode = AppDatabase().getMainData().getJSONObject("url-profile")

    // The list of node title.
    // This must be manually specified in the app.
    // private val socialMediaNodeTitles: MutableList<String> = mutableListOf()
    private val socialMediaNodeTitles = listOf(
        "web",
        "fb",
        "insta",
        "youtube",
        "whatsapp",
        "email"
    )

    // The list of social media icons.
    private val socialMediaIcons = listOf(
        R.drawable.remixicon_wordpress_fill_48,
        R.drawable.remixicon_facebook_box_fill_48,
        R.drawable.remixicon_instagram_fill_48,
        R.drawable.remixicon_youtube_fill_48,
        R.drawable.remixicon_whatsapp_fill_48,
        R.drawable.remixicon_at_fill_48
    )

    // The list of social media CTA targets.
    private val socialMediaCTATargets: MutableList<String> = mutableListOf()

    @Composable
    public fun getComposable() {

        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = GlobalSchema.fragmentInfoScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            socialMediaNodeTitles.forEach {
                socialMediaCTATargets.add(socialMediaJSONNode.getString(it))
            }

            /* Display the individual "church info" card. */
            Column ( modifier = Modifier.padding(top = 10.dp) ) {
                // Assumes cardRoutes, cardIcons, cardLabels, and cardIconDescriptions all have the same size.
                (GlobalSchema.staticDataTitleArray).forEachIndexed { index, title ->

                    Card(
                        onClick = {
                            if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText((GlobalSchema.context), "You just clicked: $title!", Toast.LENGTH_SHORT).show()

                            // Set this screen as the anchor point for "back"
                            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN

                            // Display the church profile internal webview.
                            // i.e., offline HTML code without any internet download.
                            GlobalSchema.targetIndexHTMLPath = GlobalSchema.staticDataIndexHTMLArray[index]
                            GlobalSchema.internalWebViewTitle = title
                            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_INTERNAL_HTML
                        },

                        modifier = Modifier.padding(bottom = 10.dp).aspectRatio(2.4f).fillMaxWidth()
                        ) {

                        // Displaying the text-overlaid image.
                        Box {
                            /* The background featured image. */
                            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/customize
                            // ---
                            val contrast = 1.1f  // --- 0f..10f (1 should be default)
                            val brightness = 0.0f  // --- -255f..255f (0 should be default)
                            Image(
                                // Load local path image.
                                // SOURCE: https://stackoverflow.com/a/70827897
                                painter = rememberAsyncImagePainter(
                                    File(GlobalSchema.staticDataBannerArray[index])
                                ),
                                contentDescription = "Profile page: $title",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.colorMatrix(ColorMatrix(
                                    floatArrayOf(
                                        contrast, 0f, 0f, 0f, brightness,
                                        0f, contrast, 0f, 0f, brightness,
                                        0f, 0f, contrast, 0f, brightness,
                                        0f, 0f, 0f, 1f, 0f
                                    )
                                ))
                            )

                            /* Add shadow-y overlay background so that the white text becomes more visible. */
                            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/draw/brush
                            // SOURCE: https://stackoverflow.com/a/60479489
                            Box (
                                modifier = Modifier
                                    // Color pattern: 0xAARRGGBB (where "AA" is the alpha value).
                                    .background(Color(0x40fda308))
                                    .matchParentSize()
                            )

                            /* The card description label. */
                            Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = title,
                                    fontSize = 22.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 20.dp).padding(bottom = 20.dp),
                                    style = TextStyle(
                                        shadow = Shadow(Color.Black, Offset(3.0f, 3.0f), 8.0f)
                                    )
                                )
                            }
                        }  // --- end of box.

                    }
                }
            }  // --- end of church info card/column.

            /* Display the "two-dimensional" (i.e., having nested page) ministry info card. */
            // TODO: Only after the "Media" feature is introduced.
            getMinistryNestedInfo()

            /* Displays the social media CTAs. */
            Spacer(Modifier.height(50.dp))
            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                // Assumes identical ArrayList size.
                socialMediaIcons.forEachIndexed { index, drawableIcon ->

                    Surface(Modifier.weight(1.0f).clickable(onClick = {
                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[FragmentInfo.getComposable] Selected node: ${socialMediaNodeTitles[index]}")
                        doTriggerBrowserOpen.value = true
                        externalLinkURL.value = if (socialMediaNodeTitles[index] == "email") "mailto:${socialMediaNodeTitles[index]}" else socialMediaCTATargets[index]
                    })) {
                        // Modify the icon's color.
                        // SOURCE: https://stackoverflow.com/a/72365284
                        Image(
                            painter = painterResource(drawableIcon),
                            "Social Media CTA No. ${socialMediaNodeTitles[index]}",
                            colorFilter = ColorFilter.tint(Color(0xffe6ad84))
                        )
                    }
                }
            }

            /* Displays the copyright notice. */
            Column (Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(25.dp))
                Text(stringResource(R.string.about_copyright_notice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xffa46443),
                    style = TextStyle (textAlign = TextAlign.Center),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

        }  // --- end of scrollable column.

        // Handles opening URLs in external browser.
        key(doTriggerBrowserOpen.value) {
            if (doTriggerBrowserOpen.value) {
                // Opens in an external browser.
                // SOURCE: https://stackoverflow.com/a/69103918
                LocalUriHandler.current.openUri(externalLinkURL.value)

                doTriggerBrowserOpen.value = false
            }
        }
    }

    /**
     * Display the two-dimensional (i.e., having nested pages) ministry info page.
     * This card is displayed separately as an exception, because it is currently
     * the only card info that bears nested pages.
     */
    @Composable
    private fun getMinistryNestedInfo() {
        /*Card(
            onClick = {
                if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText((GlobalSchema.context), "You just clicked: $title!", Toast.LENGTH_SHORT).show()

                // Set this screen as the anchor point for "back"
                GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MINISTRY
            },

            modifier = Modifier.padding(bottom = 10.dp).height(150.dp)
        ) {

            // Displaying the text-overlaid image.
            Box {
                /* The background featured image. */
                // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/customize
                // ---
                val contrast = 1.1f  // --- 0f..10f (1 should be default)
                val brightness = 0.0f  // --- -255f..255f (0 should be default)
                Image(
                    // Load local path image.
                    // SOURCE: https://stackoverflow.com/a/70827897
                    painter = rememberAsyncImagePainter(
                        File(GlobalSchema.staticDataBannerArray[index])
                    ),
                    contentDescription = "Profile page: $title",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix(
                        floatArrayOf(
                            contrast, 0f, 0f, 0f, brightness,
                            0f, contrast, 0f, 0f, brightness,
                            0f, 0f, contrast, 0f, brightness,
                            0f, 0f, 0f, 1f, 0f
                        )
                    ))
                )

                /* Add shadow-y overlay background so that the white text becomes more visible. */
                // SOURCE: https://developer.android.com/develop/ui/compose/graphics/draw/brush
                // SOURCE: https://stackoverflow.com/a/60479489
                Box (
                    modifier = Modifier
                        // Color pattern: 0xAARRGGBB (where "AA" is the alpha value).
                        .background(Color(0x40fda308))
                        .matchParentSize()
                )

                /* The card description label. */
                Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp).padding(bottom = 20.dp),
                        style = TextStyle(
                            shadow = Shadow(Color.Black, Offset(3.0f, 3.0f), 8.0f)
                        )
                    )
                }
            }  // --- end of box.

        }*/
    }

}
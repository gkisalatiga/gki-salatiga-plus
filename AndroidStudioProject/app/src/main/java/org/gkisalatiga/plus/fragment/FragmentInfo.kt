/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.NavigationRoutes

/**
 * @param submenu determines which service tab is opened upon load.
 */
class FragmentInfo() : ComponentActivity() {

    // Defines the label of each "church info" card.
    private val cardLabels = listOf(
        R.string.card_info_church_profile,
        R.string.card_info_church_pastor,
        R.string.card_info_church_assembly,
        R.string.card_info_church_ministry,
        R.string.card_info_church_biblestudy,
        R.string.card_info_church_contact,
    )

    // Defines the icon description in each of the "church info" card.
    private val cardIconDescriptions = listOf(
        R.string.card_desc_info_church_profile,
        R.string.card_desc_info_church_pastor,
        R.string.card_desc_info_church_assembly,
        R.string.card_desc_info_church_ministry,
        R.string.card_desc_info_church_biblestudy,
        R.string.card_desc_info_church_contact,
    )

    // Defines and locates the featured image for each card in the "church info" fragment.
    private val cardFeaturedImages = listOf(
        R.drawable.about_background_gkis,
        R.drawable.about_background_pendeta,
        R.drawable.about_background_mj,
        R.drawable.about_background_bapel,
        R.drawable.about_background_pawilayah,
        R.drawable.about_background_medsos,
    )

    // Defines the JSON static ("data/static") node for displaying the local HTML content.
    private val cardJSONNodes = listOf(
        "church_profile",
        "pastor",
        "assembly",
        "ministry",
        "bible_study",
        "social_media"
    )

    @Composable
    public fun getComposable() {

        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            /* Display the individual "church info" card. */
            Column ( modifier = Modifier.padding(top = 10.dp) ) {
                // Assumes cardRoutes, cardIcons, cardLabels, and cardIconDescriptions all have the same size.
                cardLabels.forEachIndexed { index, resID ->

                    // The title of the church profile (e.g., "Pastor", "Ministry", etc.)
                    val title = stringResource(cardLabels[index])

                    Card(
                        onClick = {
                            if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText((GlobalSchema.context), "You just clicked: resource ID #$resID!", Toast.LENGTH_SHORT).show()

                            // Set this screen as the anchor point for "back"
                            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN

                            // Display the church profile internal webview.
                            // i.e., offline HTML code without any internet download.
                            GlobalSchema.targetStaticJSONNode = cardJSONNodes[index]
                            GlobalSchema.internalWebViewTitle = title
                            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_INTERNAL_HTML
                        },

                        modifier = Modifier.padding(bottom = 10.dp).height(150.dp)
                        ) {

                        // Displaying the text-overlaid image.
                        Box () {
                            /* The background featured image. */
                            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/customize
                            // ---
                            val contrast = 1.1f  // --- 0f..10f (1 should be default)
                            val brightness = 0.0f  // --- -255f..255f (0 should be default)
                            Image(
                                painter = painterResource(cardFeaturedImages[index]),
                                contentDescription = stringResource(cardIconDescriptions[index]),
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
            }
        }

    }


}
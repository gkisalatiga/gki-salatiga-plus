/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * AsyncImage.
 * SOURCE: https://coil-kt.github.io/coil/compose/
 */

package org.gkisalatiga.plus.fragment

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONArray
import org.json.JSONObject

class FragmentServices() : ComponentActivity() {

    // The list of services to display, corresponding to the JSONSchema node name.
    private val listOfServicesNode = listOf (
        "umum",
        "es",
        "saren",
        "kml"
    )

    // The list of services to display, corresponding to the section title string ID.
    private val listOfServicesTitle = listOf (
        R.string.submenu_services_umum,
        R.string.submenu_services_es,
        R.string.submenu_services_saren,
        R.string.submenu_services_kml
    )

    @Composable
    public fun getComposable() {

        // Enabling vertical scrolling, and setting the layout to center both vertically and horizontally.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        // SOURCE: https://stackoverflow.com/a/72769561
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            // Assumes both "listOfServicesNode" and "listOfServicesTitle" have the same list size.
            listOfServicesNode.forEachIndexed { index, str ->
                // Displaying the relevant YouTube-based church services.
                getServicesUI(str, stringResource(listOfServicesTitle[index]))
            }

        }

    }

    /**
     * Obtains the composable UI of the "list of services".
     * The data displayed is based on the arguments supplied.
     * @param nodeName the name of the JSON metadata node that represents the desired service section.
     * @param sectionTitle the title string that will be displayed on top of the section.
     */
    @Composable
    private fun getServicesUI(nodeName: String, sectionTitle: String) {

        // Get the application's JSON object.
        val json: JSONObject = AppDatabase().getMainData()

        // Navigate to this fragment's particular JSON node, and retrieve its array.
        val array: JSONArray = json.getJSONObject("yt-video").getJSONArray(nodeName)

        // Enlist the cards to be shown in this fragment.
        // This is equivalent to this fragment's particular JSON node.
        val cardsList: MutableList<Map<String, String>> = mutableListOf(emptyMap())
        for (i in 0 until array.length()) {
            val curNode = array[i] as JSONObject
            cardsList.add(mapOf(
                "title" to curNode.getString("title"),
                "date" to curNode.getString("date"),
                "link" to curNode.getString("link"),
                "thumbnail" to curNode.getString("thumbnail")
            ))
        }

        // For some reason, we must pop the 0-th item in cardsList
        // because JSONArray iterates from 1, not 0.
        cardsList.removeAt(0)

        // Testing and debugging.
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TESTING) {
            Log.d("Groaker-Test", "[FragmentServices] Size of the JSONObject's parsed list is: ${cardsList.size}")
            cardsList.forEachIndexed { index, map ->
                Log.d("Groaker-Test", "[$index] ${map["title"]}")
                Log.d("Groaker-Test", "[$index] ${map["date"]}")
                Log.d("Groaker-Test", "[$index] ${map["link"]}")
                Log.d("Groaker-Test", "[$index] ${map["thumbnail"]}")
            }
        }

        /* Displaying the section title. */
        Row (modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(sectionTitle, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            // SOURCE: https://stackoverflow.com/a/69278397
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Some desc") }
        }

        /* Displaying the individual item of this section. */
        val ctx = LocalContext.current
        LazyRow {
            // Left-right padding.
            item { Spacer(modifier = Modifier.width(5.dp)) }

            // The actual content. This displays the cards.
            items(cardsList.size) {

                // Preparing the arguments.
                val title = cardsList[it]["title"]
                val url = cardsList[it]["link"]

                // Format the date.
                val date = StringFormatter().convertDateFromJSON(cardsList[it]["date"]!!)

                // Retrieving the video thumbnail.
                val imgSrc = StringFormatter().getYouTubeThumbnailFromUrl(url!!)

                Card (
                    onClick = {
                        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "The card $title is clicked", Toast.LENGTH_SHORT).show()

                        // Trying to switch to the YouTube viewer and open the stream.
                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Opening the YouTube stream: $url.")
                        GlobalSchema.ytViewerParameters["yt-link"] = url
                        GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                        GlobalSchema.ytViewerParameters["title"] = title!!
                        GlobalSchema.ytViewerParameters["date"] = date
                        GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_LIVE
                    },
                    modifier = Modifier.fillMaxHeight().width(300.dp).padding(horizontal = 5.dp),
                ) {
                    Column {
                        // Displaying the image.
                        AsyncImage(
                            model = imgSrc,
                            contentDescription = title,
                            error = painterResource(R.drawable.thumbnail_loading),
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                        // Displaying the content description of the card.
                        Column (modifier = Modifier.padding(10.dp)) {
                            Text(title!!, fontWeight = FontWeight.Bold)
                            Text(date)
                        }
                    }
                }
            }  // --- end for each.

            // Left-right padding.
            item { Spacer(modifier = Modifier.width(5.dp)) }
        }

        /* Displaying redundant spacer for visual neatness. */
        Spacer(modifier = Modifier.height(20.dp))

    }

}
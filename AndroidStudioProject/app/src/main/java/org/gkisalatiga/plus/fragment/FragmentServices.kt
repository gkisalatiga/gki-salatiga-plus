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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    // The order of the navigation routes of all the displayed chips.
    private var chipRoutes = listOf(
        NavigationRoutes().SUB_KEBAKTIAN_UMUM,
        NavigationRoutes().SUB_KEBAKTIAN_ES,
    )

    // The state of the currently selected chip.
    // (The default is to set the first chip as the one initially selected.)
    private var selectedChip = listOf(
        mutableStateOf(GlobalSchema.lastServicesSubmenu.value == NavigationRoutes().SUB_KEBAKTIAN_UMUM),
        mutableStateOf(GlobalSchema.lastServicesSubmenu.value == NavigationRoutes().SUB_KEBAKTIAN_ES),
    )

    // The names of the chips.
    private var nameOfChip = listOf(
        (GlobalSchema.context).resources.getString(R.string.submenu_services_umum),
        (GlobalSchema.context).resources.getString(R.string.submenu_services_es),
    )

    // The icons of the chips.
    private var iconOfChip = listOf (
        Icons.Filled.Done,
        Icons.Filled.Call
    )

    @Composable
    public fun getComposable() {

        // Combine chips and video contents into one contiguous column.
        Column {
            // Display the top chips for selecting between different services.
            // SOURCE: https://developer.android.com/develop/ui/compose/components/chip
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(5.dp)
            ) {
                nameOfChip.forEachIndexed { index, item ->
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            // Set the current chip as selected and the rest of the other chips unselected.
                            selectedChip.forEach{ it.value = false }
                            selectedChip[index].value = true

                            // Save the current chip state globally.
                            GlobalSchema.lastServicesSubmenu.value = chipRoutes[index]
                        },
                        label = { Text(item) },
                        selected = selectedChip[index].value,
                        leadingIcon = { },
                    )
                }
            }

            // Setting the layout to center both vertically and horizontally
            // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
            // ---
            // Enabling vertical scrolling
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
                // Displaying a particular service section.
                if (selectedChip[0].value) {
                    getServicesUI("umum")
                } else if (selectedChip[1].value) {
                    getServicesUI("es")
                } else {
                    Text("It's gone!")
                }
            }
        }

    }

    /**
     * Obtains the composable UI of the "list of services".
     * The data displayed is based on the arguments supplied.
     * @param nodeName the name of the JSON metadata node that represents the desired service section.
     */
    @Composable
    private fun getServicesUI(nodeName: String) {

        // Get the application's JSON object.
        val json: JSONObject = AppDatabase().getMainData()

        // Navigate to this fragment's particular JSON node, and retrieve its array.
        val array: JSONArray = json.getJSONObject("yt-live").getJSONArray(nodeName)

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

        // Testing.
        Log.d("Groaker-Test", "[FragmentServices] Size of the JSONObject's parsed list is: ${cardsList.size}")
        cardsList.forEachIndexed { index, map ->
            Log.d("Groaker-Test", "[$index] ${map["title"]}")
            Log.d("Groaker-Test", "[$index] ${map["date"]}")
            Log.d("Groaker-Test", "[$index] ${map["link"]}")
            Log.d("Groaker-Test", "[$index] ${map["thumbnail"]}")
        }

        // Display the cards.
        cardsList.forEach {

            // Preparing the arguments.
            val title = it["title"]
            val url = it["link"]

            // Format the date.
            val date = StringFormatter().convertDateFromJSON(it["date"]!!)

            // Retrieving the video thumbnail.
            val imgSrc = StringFormatter().getYouTubeThumbnailFromUrl(url!!)

            Card (
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                onClick = {
                    Toast.makeText(GlobalSchema.context, "The card $title is clicked", Toast.LENGTH_SHORT).show()

                    // Trying to switch to the YouTube viewer and open the stream.
                    Log.d("Groaker", "Opening YouTube stream: $url.")
                    GlobalSchema.ytViewerParameters["yt-link"] = url
                    GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                    GlobalSchema.ytViewerParameters["title"] = title!!
                    GlobalSchema.ytViewerParameters["date"] = date
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_LIVE
                }
            ) {
                Column {
                    // Displaying the image.
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = title,
                        error = painterResource(R.drawable.thumbnail_loading),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                        )
                    // Displaying the content description of the card.
                    Column (modifier = Modifier.padding(10.dp)) {
                        Text(title!!, fontWeight = FontWeight.Bold)
                        Text(date)
                    }
                }
            }
        }

    }

}
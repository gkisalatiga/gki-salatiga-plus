/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.app.DownloadManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.DownloadAndSaveImageTask
import org.gkisalatiga.plus.lib.NavigationRoutes
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors

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
        (GlobalSchema.norender["context"] as Context).resources.getString(R.string.submenu_services_umum),
        (GlobalSchema.norender["context"] as Context).resources.getString(R.string.submenu_services_es),
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
                        leadingIcon = {
                            Icon(
                                imageVector = iconOfChip[index],
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
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
                    getRegularService()
                } else if (selectedChip[1].value) {
                    getEnglishService()
                } else {
                    Text("It's gone!")
                }
            }
        }

    }

    @Composable
    private fun getEnglishService() {

        // Get the application's JSON object
        val db = AppDatabase().loadRaw(GlobalSchema.norender["context"] as Context).getMainData()

        // Enlist the cards to be shown in this fragment
        val cardsList = listOf(
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es")
        )

        // Display the cards
        cardsList.forEach {
            val title = it.getString("title")
            val desc = it.getString("description")
            val imgSrc = it.getString("thumbnail")

            // Downloading the thumbnail and converting it to bitmap
            // val downloadedStream = mutableStateOf(InputStream.nullInputStream())
            // val bmp = BitmapFactory.decodeStream(downloadedStream.value)

            Card (
                onClick = {
                    Toast.makeText(GlobalSchema.norender["context"] as Context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                    val destination = "streaming_youtube"
                    // screenController.navigate("${NavigationRoutes().SCREEN_LIVE}/${destination}?${NavigationRoutes().SUB_KEBAKTIAN_ES}")
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column {
                    // Displaying the image
                    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/loading
                    // SOURCE: https://stackoverflow.com/a/69689287
                    // Image(
                    // bitmap = bmp.asImageBitmap(),
                    // contentDescription = "Desc here"
                    // )

                    /*
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = null,
                    )
                     */

                    // DownloadAndSaveImageTask(schema.value["context"] as Context).execute(imgSrc)

                    Image(
                        painter = painterResource(R.drawable.sample_thumbnail_youtube_5),
                        contentDescription = "Some name",
                        modifier = Modifier.fillMaxWidth(),
                        // SOURCE: https://stackoverflow.com/a/76274718
                        contentScale = ContentScale.FillWidth
                    )
                    Column (modifier = Modifier.padding(10.dp)) {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text("21 Juli 2003")
                    }
                }
            }
        }

    }

    @Composable
    private fun getRegularService() {

        // Get the application's JSON object
        val db = AppDatabase().loadRaw(GlobalSchema.norender["context"] as Context).getMainData()

        // Enlist the cards to be shown in this fragment
        val cardsList = listOf(
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum")
        )

        // Display the cards
        cardsList.forEach {
            val title = it.getString("title")
            val desc = it.getString("description")
            val imgSrc = it.getString("thumbnail")

            // Downloading the thumbnail and converting it to bitmap
            // val downloadedStream = mutableStateOf(InputStream.nullInputStream())
            // val bmp = BitmapFactory.decodeStream(downloadedStream.value)

            Card (
                onClick = {
                    Toast.makeText(GlobalSchema.norender["context"] as Context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                    val destination = "streaming_youtube"
                    Log.d("Groaker", "Opening YouTube stream on regular service.")
                    // Prevents random switching of screens when the user opens a different fragment or screen.
                    // screenController.saveState()
                    // screenController.clearBackStack(NavigationRoutes().SCREEN_MAIN)
                    // SOURCE: https://stackoverflow.com/a/76423168
                    //onFinish()
                    //screenController.popBackStack("", true)
                    /*
                    val navDestination = "${NavigationRoutes().SCREEN_LIVE}/${destination}?${NavigationRoutes().SUB_KEBAKTIAN_UMUM}"
                    screenController.navigate(navDestination) {
                        // Removes the previous back state and prevents random jiggling of screens before navigating.
                        // SOURCE: https://stackoverflow.com/a/68304038
                        popUpTo(navDestination) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }*/
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column {
                    // Displaying the image
                    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/loading
                    // SOURCE: https://stackoverflow.com/a/69689287
                    // Image(
                    // bitmap = bmp.asImageBitmap(),
                    // contentDescription = "Desc here"
                    // )

                    /*
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = null,
                    )
                     */

                    // DownloadAndSaveImageTask(context).execute(imgSrc)

                    Image(
                        painter = painterResource(R.drawable.sample_thumbnail_youtube_4),
                        contentDescription = "Some name",
                        modifier = Modifier.fillMaxWidth(),
                        // SOURCE: https://stackoverflow.com/a/76274718
                        contentScale = ContentScale.FillWidth
                    )
                    Column (modifier = Modifier.padding(10.dp)) {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text("21 Juli 2003")
                    }
                }
            }
        }

    }

    /*
    private val submenu = submenu
    private val onFinish = onFinish

    // The state of the currently selected chip.
    // (The default is to set the first chip as the one initially selected.)
    private var selectedChip = listOf(
        mutableStateOf(submenu == null || submenu == NavigationRoutes().SUB_KEBAKTIAN_UMUM),
        mutableStateOf(submenu != null && submenu == NavigationRoutes().SUB_KEBAKTIAN_ES)
    )

    // The names of the chips.
    private var nameOfChip = listOf(
        "Kebaktian Umum",
        "English Service"
    )

    // The icons of the chips.
    private var iconOfChip = listOf (
        Icons.Filled.Done,
        Icons.Filled.Call
    )

    /**
     * Navigation between screens
     * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
     */
    @Composable
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
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
                        },
                        label = { Text(item) },
                        selected = selectedChip[index].value,
                        leadingIcon = {
                            Icon(
                                imageVector = iconOfChip[index],
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
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
                Log.d("Groaker", "What submenu did you choose? [$submenu]")
                if (selectedChip[0].value) {
                    getRegularService(screenController, fragmentController, context)
                } else if (selectedChip[1].value) {
                    getEnglishService(screenController, fragmentController, context)
                } else {
                    Text("It's gone!")
                }
            }
        }
    }

    @Composable
    private fun getEnglishService(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        // Display the service liturgy.
        Card (
            onClick = { Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp).padding(horizontal = 5.dp)
        ) {
            Row ( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp) ) {
                Image(
                    painter = painterResource(R.drawable.baseline_book_24),
                    contentDescription = "Some name",
                    alignment = Alignment.Center,
                )
                Column {
                    Text("English Service Liturgy", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp), textAlign = TextAlign.Center)
                    Text("July 2nd, 2024", modifier = Modifier.padding(horizontal = 10.dp))
                    Text("Overturning the Temple Table", fontStyle = FontStyle.Italic, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }

        /* Add divider between contents. */
        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = DividerDefaults.Thickness, color = DividerDefaults.color)

        // Get the application's JSON object
        val db = AppDatabase().loadRaw(context).getMainData()

        // Enlist the cards to be shown in this fragment
        val cardsList = listOf(
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es")
        )

        // Display the cards
        cardsList.forEach {
            val title = it.getString("title")
            val desc = it.getString("description")
            val imgSrc = it.getString("thumbnail")

            // Downloading the thumbnail and converting it to bitmap
            // val downloadedStream = mutableStateOf(InputStream.nullInputStream())
            // val bmp = BitmapFactory.decodeStream(downloadedStream.value)

            Card (
                onClick = {
                    Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                    val destination = "streaming_youtube"
                    screenController.navigate("${NavigationRoutes().SCREEN_LIVE}/${destination}?${NavigationRoutes().SUB_KEBAKTIAN_ES}")
                          },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column {
                    // Displaying the image
                    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/loading
                    // SOURCE: https://stackoverflow.com/a/69689287
                    // Image(
                    // bitmap = bmp.asImageBitmap(),
                    // contentDescription = "Desc here"
                    // )

                    /*
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = null,
                    )
                     */

                    DownloadAndSaveImageTask(context).execute(imgSrc)

                    Image(
                        painter = painterResource(R.drawable.sample_thumbnail_youtube_5),
                        contentDescription = "Some name",
                        modifier = Modifier.fillMaxWidth(),
                        // SOURCE: https://stackoverflow.com/a/76274718
                        contentScale = ContentScale.FillWidth
                    )
                    Column (modifier = Modifier.padding(10.dp)) {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text("21 Juli 2003")
                    }
                }
            }
        }
    }

    @Composable
    private fun getRegularService(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        // Display the service liturgy.
        Card (
            onClick = { Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp).padding(horizontal = 5.dp)
        ) {
            Row ( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp) ) {
                Image(
                    painter = painterResource(R.drawable.baseline_book_24),
                    contentDescription = "Some name",
                    alignment = Alignment.Center
                )
                Column {
                    Text("Liturgi Kebaktian Umum", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp), textAlign = TextAlign.Center)
                    Text("14 Juli 2024", modifier = Modifier.padding(horizontal = 10.dp))
                    Text("Bela Rasa Sang Gembala", fontStyle = FontStyle.Italic, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }

        // Display the official church news.
        Card (
            onClick = { Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp).padding(horizontal = 5.dp)
        ) {
            Row ( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp) ) {
                Image(
                    painter = painterResource(R.drawable.baseline_newspaper_24),
                    contentDescription = "Some name",
                    alignment = Alignment.Center,
                )
                Column {
                    Text("Warta Jemaat", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp), textAlign = TextAlign.Center)
                    Text("14 Juli 2024", modifier = Modifier.padding(horizontal = 10.dp))
                    Text("Bela Rasa Sang Gembala", fontStyle = FontStyle.Italic, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }

        /* Add divider between contents. */
        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = DividerDefaults.Thickness, color = DividerDefaults.color)

        // Get the application's JSON object
        val db = AppDatabase().loadRaw(context).getMainData()

        // Enlist the cards to be shown in this fragment
        val cardsList = listOf(
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum")
        )

        // Display the cards
        cardsList.forEach {
            val title = it.getString("title")
            val desc = it.getString("description")
            val imgSrc = it.getString("thumbnail")

            // Downloading the thumbnail and converting it to bitmap
            // val downloadedStream = mutableStateOf(InputStream.nullInputStream())
            // val bmp = BitmapFactory.decodeStream(downloadedStream.value)

            Card (
                onClick = {
                    Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                    val destination = "streaming_youtube"
                    Log.d("Groaker", "Opening YouTube stream on regular service.")
                    // Prevents random switching of screens when the user opens a different fragment or screen.
                    // screenController.saveState()
                    // screenController.clearBackStack(NavigationRoutes().SCREEN_MAIN)
                    // SOURCE: https://stackoverflow.com/a/76423168
                    //onFinish()
                    //screenController.popBackStack("", true)
                    val navDestination = "${NavigationRoutes().SCREEN_LIVE}/${destination}?${NavigationRoutes().SUB_KEBAKTIAN_UMUM}"
                    screenController.navigate(navDestination) {
                        // Removes the previous back state and prevents random jiggling of screens before navigating.
                        // SOURCE: https://stackoverflow.com/a/68304038
                        popUpTo(navDestination) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                          },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column {
                    // Displaying the image
                    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/loading
                    // SOURCE: https://stackoverflow.com/a/69689287
                    // Image(
                    // bitmap = bmp.asImageBitmap(),
                    // contentDescription = "Desc here"
                    // )

                    /*
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = null,
                    )
                     */

                    DownloadAndSaveImageTask(context).execute(imgSrc)

                    Image(
                        painter = painterResource(R.drawable.sample_thumbnail_youtube_4),
                        contentDescription = "Some name",
                        modifier = Modifier.fillMaxWidth(),
                        // SOURCE: https://stackoverflow.com/a/76274718
                        contentScale = ContentScale.FillWidth
                    )
                    Column (modifier = Modifier.padding(10.dp)) {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text("21 Juli 2003")
                    }
                }
            }
        }
    }
     */
}
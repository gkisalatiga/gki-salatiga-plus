/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import java.io.File
import kotlin.math.ceil

class FragmentHome : ComponentActivity() {

    // The following defines the visible menu buttons shown in the main menu,
    // as well as their corresponding navigation targets.
    private val btnRoutes = listOf(
        NavigationRoutes().SCREEN_WARTA,
        NavigationRoutes().SCREEN_LITURGI,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_YKB,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_FORMS,
    )

    // The following defines the label of each visible menu button.
    private val btnLabels = listOf(
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_wj),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_liturgi),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_agenda),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_saren),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_ykb),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_kml),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_offertory),
        (GlobalSchema.context).resources.getString(R.string.btn_mainmenu_form),
    )

    // The following defines each visible menu button's icon description.
    private val btnDescriptions = listOf(
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_wj),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_liturgi),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_agenda),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_saren),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_ykb),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_kml),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_offertory),
        (GlobalSchema.context).resources.getString(R.string.btn_desc_mainmenu_form),
    )

    // The following defines the icons used for the visible menu buttons.
    private val btnIcons = listOf(
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
        R.drawable.baseline_newspaper_48,
    )

    @Composable
    public fun getComposable() {
        val ctx = LocalContext.current

        getPosterDialog()

        // The following defines each individual featured cover image of the menu.
        // (Only the top two menus are considered.)
        val btnFeaturedCover = listOf(
            R.drawable.menu_cover_wj,
            R.drawable.menu_cover_liturgi
        )

        // Enlist the banner sources for the horizontal "infinite" carousel.
        val carouselImageSources = GlobalSchema.carouselBannerBannerArray

        // "Infinite" pager page scrolling.
        // Please fill the following integer-variable with a number of pages
        // that the user won't bother scrolling.
        // SOURCE: https://stackoverflow.com/a/75469260
        val baseInfiniteScrollingPages = 256  // --- i.e., 2^8.

        // Necessary variables for the infinite-page carousel.
        // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
        val actualPageCount = carouselImageSources.size
        val carouselPageCount = actualPageCount * baseInfiniteScrollingPages
        val carouselPagerState = rememberPagerState(
            initialPage = carouselPageCount / 2,
            pageCount = { carouselPageCount }
        )

        /* Set-up the launched effect for auto-scrolling the horizontal carousel/pager. */
        // SOURCE: https://stackoverflow.com/a/67615616
        LaunchedEffect(carouselPagerState.settledPage) {
            launch {
                delay(2500)
                with(carouselPagerState) {
                    animateScrollToPage(
                        page = currentPage + 1,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }  // --- end of launched effect.

        /* --------------------------------------------------------------- */

        // Setting the layout to center both vertically and horizontally
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = GlobalSchema.fragmentHomeScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            /* Show the "infinite" horizontal carousel for CTA. */
            // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
            // SOURCE: https://stackoverflow.com/a/75469260
            // ---
            // Create the box boundary.
            Box (modifier = Modifier.height(232.dp).fillMaxWidth()) {

                /* Create the horizontal pager "carousel" */
                HorizontalPager(
                    state = carouselPagerState,
                    beyondViewportPageCount = 1,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Navigate to the current iteration's JSON node.
                    val currentNode = AppDatabase()
                        .getMainData()
                        .getJSONObject("carousel")
                        .getJSONObject(GlobalSchema.carouselBannerJSONNodeArray[it % actualPageCount])

                    /* Display the sample image. */
                    Surface (
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(LocalContext.current.resources.getDimension(R.dimen.banner_inner_padding).dp),
                        onClick = {
                            if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You are clicking carousel banner no. ${it % actualPageCount}!", Toast.LENGTH_SHORT).show()

                            /* Switch to a different screen or run a certain action based on the carousel banner type. */
                            when (GlobalSchema.carouselBannerTypeArray[it % actualPageCount]) {
                                "article" -> {
                                    // Preparing the WebView arguments.
                                    val url = currentNode.getString("article-url")
                                    val title = currentNode.getString("title")

                                    // Navigate to the WebView viewer.
                                    GlobalSchema.webViewTargetURL = url
                                    GlobalSchema.webViewTitle = title
                                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_WEBVIEW

                                    // Set this screen as the anchor point for "back"
                                    GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                                }
                                "poster" -> {
                                    showPosterDialog.value = true
                                    posterDialogTitle.value = currentNode.getString("title")
                                    posterDialogCaption.value = currentNode.getString("poster-caption")
                                    posterDialogImageSource.value = GlobalSchema.carouselBannerBaseFolderArray[it % actualPageCount] + "/" + currentNode.getString("poster-image")
                                }
                                "yt" -> {
                                    // Preparing the YouTube player arguments.
                                    val url = currentNode.getString("yt-link")
                                    val title = currentNode.getString("yt-title")
                                    val date = currentNode.getString("yt-date")
                                    val desc = currentNode.getString("yt-desc")

                                    // Trying to switch to the YouTube viewer and open the stream.
                                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Opening the YouTube stream: $url.")
                                    GlobalSchema.ytViewerParameters["yt-link"] = url
                                    GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                                    GlobalSchema.ytViewerParameters["title"] = title!!
                                    GlobalSchema.ytViewerParameters["date"] = StringFormatter().convertDateFromJSON(date)
                                    GlobalSchema.ytViewerParameters["desc"] = desc!!
                                    GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_LIVE
                                }
                            }
                        }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                File(carouselImageSources[it % actualPageCount])
                            ),
                            contentDescription = "Carousel Image ${it % actualPageCount }",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Create the pager indicator.
                // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
                Row(
                    modifier = Modifier.height(45.dp).fillMaxWidth().align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(actualPageCount) { iteration ->
                        val color = if (carouselPagerState.currentPage % actualPageCount == iteration) Color.White else Color.White.copy(alpha = 0.5f)

                        // The individual dot for indicating carousel page.
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp)
                        )
                    }
                }

            }

            /* Displaying the top two menus. */
            Row {
                btnRoutes.subList(0, 2).forEachIndexed { index, str ->
                    // The individual card item.
                    Card (modifier = Modifier.padding(10.dp).fillMaxWidth().weight(1f), onClick = {
                        // This will be triggered when the main menu button is clicked.
                        if (btnRoutes[index] != NavigationRoutes().SCREEN_BLANK) {
                            GlobalSchema.pushScreen.value = btnRoutes[index]
                        }
                    }) {
                        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp), modifier = Modifier.fillMaxWidth().height(200.dp)) {
                                Image(painter = painterResource(btnFeaturedCover[index]), "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                            Text(btnLabels[index], textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // The menu array after "popping" the first two elements.
            val subArray = btnRoutes.subList(2, btnRoutes.size)

            /* Displaying the main menu action buttons other than the first two. */
            // Assumes btnRoutes, btnLabels, and btnIcons all have the same size.
            val columns = 3
            val rows = ceil((subArray.size / columns).toDouble()).toInt()

            var index = 0
            for (j in 0 until rows) {
                Row {
                    while (index < subArray.size) {
                        val offsetIndex = index + 2

                        // Displaying the menu button.
                        Button (
                            onClick = {
                                // This will be triggered when the main menu button is clicked.
                                if (btnRoutes[offsetIndex] != NavigationRoutes().SCREEN_BLANK) {
                                    GlobalSchema.pushScreen.value = btnRoutes[offsetIndex]
                                }
                            },
                            modifier = Modifier.weight(1f).padding(5.dp).height(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            // The main menu element wrapper.
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                // The main menu action button icon.
                                Icon(
                                    painter = painterResource(btnIcons[offsetIndex]),
                                    contentDescription = btnDescriptions[offsetIndex],
                                    tint = Color.White
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                // The text.
                                Text(btnLabels[offsetIndex], textAlign = TextAlign.Center)
                            }
                        }

                        // Ensures that we have the right amount of columns.
                        index += 1
                        if (index % columns == 0) break
                    }
                }
            }  // --- end of for loop.
        }  // --- end of scrollable column.

    }  // --- end of getComposable().

    // The state of the current poster dialog.
    private val showPosterDialog = GlobalSchema.fragmentHomePosterDialogState
    private val posterDialogTitle = GlobalSchema.posterDialogTitle
    private val posterDialogCaption = GlobalSchema.posterDialogCaption
    private val posterDialogImageSource = GlobalSchema.posterDialogImageSource

    /**
     * This function displays the poster dialog.
     * SOURCE: https://www.composables.com/tutorials/dialogs
     * SOURCE: https://developer.android.com/develop/ui/compose/components/dialog
     */
    @Composable
    @SuppressLint("ComposableNaming")
    private fun getPosterDialog() {
        val ctx = LocalContext.current
        val verticalScrollState = rememberScrollState()
        if (showPosterDialog.value) {
            AlertDialog(
                onDismissRequest = { showPosterDialog.value = false },
                title = { Text(posterDialogTitle.value, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                text = {
                    Column(
                        modifier = Modifier.height(300.dp).verticalScroll(verticalScrollState)
                    ) {
                        Surface (modifier = Modifier.fillMaxWidth(), color = Color.Transparent, onClick = {
                            showPosterDialog.value = false
                            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_POSTER_VIEWER
                        }, shape = RoundedCornerShape(10.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    File(posterDialogImageSource.value)
                                ),
                                contentDescription = "Carousel Poster Image",
                                modifier = Modifier.height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(15.dp))
                        Text(posterDialogCaption.value)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPosterDialog.value = false }) {
                        Text(stringResource(R.string.poster_dialog_close_text).uppercase())
                    }
                },
                confirmButton = { }
            )
        }
    }

}
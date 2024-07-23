/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes

class FragmentHome() : ComponentActivity() {

    // The following defines the visible menu buttons shown in the main menu,
    // as well as their corresponding navigation targets.
    private val btnRoutes = listOf(
        NavigationRoutes().SCREEN_WARTA,
        NavigationRoutes().SCREEN_LITURGI,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_SAREN,
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

        // The following defines each individual featured cover image of the menu.
        // (Only the top two menus are considered.)
        val btnFeaturedCover = listOf(
            R.drawable.menu_cover_wj,
            R.drawable.menu_cover_liturgi
        )

        // Enlist the thumbnails for the horizontal "infinite" carousel.
        val imgSources = listOf(
            R.drawable.sample_welcome_banner,
            R.drawable.sample_thumbnail_youtube_2,
            R.drawable.sample_thumbnail_youtube_4
        )

        // "Infinite" pager page scrolling.
        // Please fill the following integer-variable with a number of pages
        // that the user won't bother scrolling.
        // SOURCE: https://stackoverflow.com/a/75469260
        val baseInfiniteScrollingPages = 16384  // --- 2^14.

        // Necessary variables for the infinite-page carousel.
        // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
        val actualPageCount = imgSources.size
        val carouselPageCount = actualPageCount * baseInfiniteScrollingPages
        val carouselPagerState = rememberPagerState(
            initialPage = carouselPageCount / 2,
            pageCount = { carouselPageCount }
        )

        /* Set-up the launched effect for auto-scrolling the horizontal carousel/pager. */
        // SOURCE: https://stackoverflow.com/a/67615616
        LaunchedEffect(carouselPagerState.settledPage) {
            launch {
                delay(1000)
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
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {

            // The span size of the vertical grid.
            val spanSize: Int = 3
            LazyVerticalGrid(
                columns = GridCells.Fixed(spanSize),
                modifier = Modifier.padding(20.dp).height(1000.dp),
                horizontalArrangement = Arrangement.Center,
            ) {

                /* Show the "infinite" horizontal carousel for CTA. */
                // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
                // SOURCE: https://stackoverflow.com/a/75469260
                // ---
                // Create the box boundary.
                item (span = { GridItemSpan(spanSize) }) {
                    Box (modifier = Modifier.fillMaxSize()) {

                        /* Create the horizontal pager "carousel" */
                        HorizontalPager(
                            state = carouselPagerState,
                            beyondViewportPageCount = 1,
                        ) {
                            /* Display the sample image. */
                            Surface (
                                shape = RoundedCornerShape(15.dp),
                                modifier = Modifier.padding(LocalContext.current.resources.getDimension(R.dimen.banner_inner_padding).dp)
                            ) {
                                Image(
                                    painter = painterResource(imgSources[it % actualPageCount]),
                                    contentDescription = "Carousel Image ${it % actualPageCount }",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.FillWidth
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
                }  // --- end of item.

                /* Display the church's building image. */
                /*item (span = { GridItemSpan(spanSize) }) {
                    val imgChurchSource = R.drawable.sample_welcome_banner
                    val imgChurchDescription = (GlobalSchema.context).resources.getString(R.string.info_church_img_description)
                    Surface (
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(LocalContext.current.resources.getDimension(R.dimen.banner_inner_padding).dp)
                    ) {
                        Image(
                            painter = painterResource(imgChurchSource),
                            contentDescription = imgChurchDescription,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }*/

                /* Displaying the top two menus. */
                item (span = { GridItemSpan(spanSize) }) {
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
                                    Surface(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp), modifier = Modifier.fillMaxWidth().height(250.dp)) {
                                        Image(painter = painterResource(btnFeaturedCover[index]), "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                    }
                                    Text(btnLabels[index], textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                    }
                }

                /* Displaying the main menu action buttons other than the first two. */
                // Assumes btnRoutes, btnLabels, and btnIcons all have the same size.
                btnRoutes.subList(2, btnRoutes.size).forEachIndexed { index, str ->
                    val offsetIndex = index + 2
                    item {
                        Button (
                            onClick = {
                                // This will be triggered when the main menu button is clicked.
                                if (btnRoutes[offsetIndex] != NavigationRoutes().SCREEN_BLANK) {
                                    GlobalSchema.pushScreen.value = btnRoutes[offsetIndex]
                                }
                                      },
                            modifier = Modifier.padding(5.dp).height(100.dp),
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
                    }
                }
            }

        }
    }

}
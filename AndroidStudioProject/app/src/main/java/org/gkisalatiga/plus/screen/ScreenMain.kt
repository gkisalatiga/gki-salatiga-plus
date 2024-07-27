/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * The main menu of the GKI Salatiga Plus app.
 * This sets up the Scaffolding of both top and bottom navigations,
 * as well as the menu feature select screen.
 *
 * ---
 *
 * REFERENCES USED:
 *
 * Using strings.xml in a Composable function
 * SOURCE: https://stackoverflow.com/a/65889036
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentInfo
import org.gkisalatiga.plus.fragment.FragmentServices
import org.gkisalatiga.plus.global.GlobalSchema

import org.gkisalatiga.plus.lib.NavigationRoutes
import kotlin.coroutines.CoroutineContext

class ScreenMain() : ComponentActivity() {

    // For "double-press back" to exit.
    private var backPressedTime: Long = 0

    // Enlists all of the fragments that will be displayed in this particular screen.
    private val fragRoutes = listOf(
        NavigationRoutes().FRAG_MAIN_HOME,
        NavigationRoutes().FRAG_MAIN_SERVICES,
        NavigationRoutes().FRAG_MAIN_INFO,
    )

    // Used by the bottom nav to command the scrolling of the horizontal pager.
    private var bottomNavPagerScrollTo = mutableIntStateOf(fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value))

    // Determines the top bar title.
    private var topBarTitle = (GlobalSchema.context).resources.getString(R.string.app_name_alias)

    // Controls the horizontal scrolling of the pager.
    private lateinit var horizontalPagerState: PagerState

    // Determines what background to show on the new top bar layout by user github.com/ujepx64.
    private var newTopBarBackground = mutableIntStateOf(GlobalSchema.lastNewTopBarBackground.value)

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        // Initializing the horizontal pager.
        horizontalPagerState = rememberPagerState ( pageCount = {fragRoutes.size}, initialPage = fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value) )

        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Current value of 'pushScreen': ${GlobalSchema.pushScreen.value}")
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Current value of 'lastMainScreenFragment': ${GlobalSchema.lastMainScreenPagerPage.value}")
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Current value of 'pagerstate.currentPage': ${horizontalPagerState.currentPage}")
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Current value of 'pagerstate.targetPage': ${horizontalPagerState.targetPage}")
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Current value of 'bottomNavPagerScrollTo': ${bottomNavPagerScrollTo.intValue}")

        // Listen to the bottom nav's request to change the horizontal pager page.
        listenNavBarScrollPager()

        // Listen to the change in the horizontal pager's page state.
        // Then change the global page state accordingly.
        key (horizontalPagerState.targetPage) {
            GlobalSchema.lastMainScreenPagerPage.value = fragRoutes[horizontalPagerState.targetPage]
        }

        Scaffold (
            bottomBar = { getBottomBar() },
            topBar = { /* The "top bar" is now merged with the scaffold content. */ },
            floatingActionButton =  { },
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            // Setting up the layout of all of the fragments.
            // Then wrapping each fragment in AnimatedVisibility so that we can manually control their visibility.
            Box (Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())) {
                Box {
                    // Shows the new top bar.
                    getTopBar()

                    // Shows the main content.
                    Surface (
                        modifier = Modifier.padding(top = LocalContext.current.resources.getDimension(R.dimen.new_topbar_content_top_y_offset).dp).fillMaxSize().zIndex(10f),
                        shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp)
                    ) {
                        // Enabling pager for managing and layouting multiple fragments in a given screen.
                        // SOURCE: https://www.composables.com/foundation/horizontalpager
                        HorizontalPager(
                            state = horizontalPagerState,
                            modifier = Modifier.fillMaxSize().padding(top = 0.dp),
                            // Without this property, the left-right page scrolling would be insanely laggy!
                            beyondViewportPageCount = 2
                        ) { page ->
                            when (page) {
                                0 -> FragmentHome().getComposable()
                                1 -> FragmentServices().getComposable()
                                2 -> FragmentInfo().getComposable()
                            }
                        }
                    }
                }
            }

            // Ensure that when we are at the first screen upon clicking "back",
            // the app is exited instead of continuing to navigate back to the previous screens.
            // SOURCE: https://stackoverflow.com/a/69151539
            val exitConfirm = stringResource(R.string.exit_confirmation_toast_string)
            val localContext = LocalContext.current
            BackHandler {
                val curRoute = GlobalSchema.lastMainScreenPagerPage.value
                if (curRoute == NavigationRoutes().FRAG_MAIN_HOME) {

                    // Ensure "double tap the back button to exit".
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        // Exit the application.
                        // SOURCE: https://stackoverflow.com/a/67402808
                        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(localContext, "You just clicked $curRoute and exited the app!", Toast.LENGTH_SHORT).show()
                        (localContext as ComponentActivity).finish()
                    } else {
                        Toast.makeText(localContext, exitConfirm , Toast.LENGTH_LONG).show()
                    }

                    backPressedTime = System.currentTimeMillis()

                } else if (
                    curRoute == NavigationRoutes().FRAG_MAIN_INFO ||
                    curRoute == NavigationRoutes().FRAG_MAIN_SERVICES
                ) {
                    // Since we are in the main screen but not at fragment one,
                    // navigate the app to fragment one.
                    bottomNavPagerScrollTo.intValue = 0
                } else {
                    // Do nothing.
                }
            }
        }

    }

    @Composable
    private fun getBottomBar() {

        // Defines the bottom nav tab names.
        val navItems = listOf(
            stringResource(R.string.bottomnav_menu_home),
            stringResource(R.string.bottomnav_menu_services),
            stringResource(R.string.bottomnav_menu_info),
        )

        // Enlists the bottom nav bar item icons. (On selected.)
        val bottomNavItemIconsSelected = listOf(
            Icons.Filled.Home,
            Icons.Filled.PlayArrow,
            Icons.Filled.Info
        )

        // Enlists the bottom nav bar item icons. (When inactive.)
        val bottomNavItemIconsInactive = listOf(
            Icons.Outlined.Home,
            Icons.Outlined.PlayArrow,
            Icons.Outlined.Info
        )

        BottomAppBar() {
            // Ensures that the nav bar stays at the bottom
            // SOURCE: https://stackoverflow.com/q/70904979
            Row(modifier = Modifier.weight(1f, false)) {

                // Ensures that the bottom nav and the horizontal pager are in sync.
                key(GlobalSchema.lastMainScreenPagerPage.value) {
                    navItems.forEachIndexed { index, item ->

                        NavigationBarItem(
                            icon = {
                                if (fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value) == index) {
                                    Icon(bottomNavItemIconsSelected[index], contentDescription = item)
                                } else {
                                    Icon(bottomNavItemIconsInactive[index], contentDescription = item)
                                }
                                   },
                            label = { Text(item) },
                            selected = fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value) == index,
                            onClick = {
                                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) {
                                    Log.d("Groaker", "Triggered bottom nav button into index: $index")
                                    Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'pushScreen': ${GlobalSchema.pushScreen.value}")
                                    Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'lastMainScreenFragment': ${GlobalSchema.lastMainScreenPagerPage.value}")
                                    Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'pagerstate.currentPage': ${horizontalPagerState.currentPage}")
                                    Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'pagerstate.targetPage': ${horizontalPagerState.targetPage}")
                                    Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'bottomNavPagerScrollTo': ${bottomNavPagerScrollTo.intValue}")
                                }

                                bottomNavPagerScrollTo.intValue = index
                            }
                        )
                    }
                }

            }
        }
    }

    @Composable
    private fun listenNavBarScrollPager() {
        // Force recomposition of the horizontal pager.
        // SOURCE: https://stackoverflow.com/a/77289069
        LaunchedEffect(bottomNavPagerScrollTo.intValue) {
            Log.d("Groaker", "Launching 'LaunchedEffect' on the horizontal pager state ...")

            // Scroll to the desired pager page.
            horizontalPagerState.animateScrollToPage(bottomNavPagerScrollTo.intValue)
        }
    }

    @Composable
    private fun getTopBar() {

        /* Drawing canvas for the new top bar layout. */
        Box ( modifier = Modifier
            .height(LocalContext.current.resources.getDimension(R.dimen.new_topbar_canvas_height).dp)
            .fillMaxWidth() ) {

            /* Drawing the top bar greetings banner background. */
            // SOURCE: https://stackoverflow.com/a/70965281
            Image (
                painter = painterResource(newTopBarBackground.intValue),
                contentDescription = "",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            /* To colorize the image. */
            Box (Modifier.background(Color(0x77fdb308)).matchParentSize()) {}

            /* Drawing the overlapping top bar transparent gradient. */
            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/draw/brush
            // SOURCE: https://stackoverflow.com/a/60479489
            val overlayGradient = Brush.verticalGradient(colorStops = arrayOf (
                // HSL (hue, saturation, lightness) must be in range (0..360, 0..1, 0..1)
                0.0f to Color.hsl(38f, 0.98f, 0.51f),
                0.5f to Color.Transparent,
                1.0f to Color.White
            ))
            Box (
                modifier = Modifier
                    .background(overlayGradient)
                    .matchParentSize()
                    .padding(LocalContext.current.resources.getDimension(R.dimen.new_topbar_canvas_padding).dp)
            ) {
                Column {

                    // Shadow.
                    // SOURCE: https://codingwithrashid.com/how-to-add-shadows-to-text-in-android-jetpack-compose/
                    val shadowTextStyle = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2.0f, 2.0f),
                            blurRadius = 5.0f
                        )
                    )

                    // This text will trigger the "About App" screen.
                    val ctx = LocalContext.current
                    Surface (
                        color = Color.Transparent,
                        modifier = Modifier.padding(bottom = 10.dp),
                        onClick = {
                            if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You will open the about app.", Toast.LENGTH_SHORT).show()

                            // Opens the "About app" screen.
                            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_ABOUT
                        }
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // The app title.
                            Text(topBarTitle, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)
                            Spacer(Modifier.width(20.dp))
                            // The "next" button.
                            Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "", tint = Color.White)
                        }
                    }

                    // The overlaying greetings text.
                    Text(stringResource(R.string.new_topbar_greetings), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White, style = shadowTextStyle)
                    Text(stringResource(R.string.new_topbar_person_name), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, style = shadowTextStyle)
                }
            }

        }

    }
}

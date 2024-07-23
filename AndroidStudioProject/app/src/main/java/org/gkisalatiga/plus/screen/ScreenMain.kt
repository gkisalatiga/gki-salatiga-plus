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
import androidx.compose.material.icons.filled.PlayArrow
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
    var backPressedTime: Long = 0

    // Enlists all of the fragments that will be displayed in this particular screen.
    private val fragRoutes = listOf(
        NavigationRoutes().FRAG_MAIN_HOME,
        NavigationRoutes().FRAG_MAIN_SERVICES,
        NavigationRoutes().FRAG_MAIN_INFO,
    )

    // Used by the bottom nav to command the scrolling of the horizontal pager.
    private var bottomNavPagerScrollTo = mutableIntStateOf(fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value))

    // Determines the top bar title.
    private var topBarTitle = mutableStateOf((GlobalSchema.context).resources.getString(R.string.app_name_alias))

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
                        HorizontalPager( state = horizontalPagerState, modifier = Modifier.fillMaxSize().padding(top = 0.dp) ) { page ->
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

        BottomAppBar() {
            // Ensures that the nav bar stays at the bottom
            // SOURCE: https://stackoverflow.com/q/70904979
            Row(modifier = Modifier.weight(1f, false)) {

                // Ensures that the bottom nav and the horizontal pager are in sync.
                key(GlobalSchema.lastMainScreenPagerPage.value) {
                    navItems.forEachIndexed { index, item ->

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                            label = { Text(item) },
                            selected = fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value) == index,
                            onClick = {
                                Log.d("Groaker", "Triggered bottom nav button into index: $index")

                                Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'pushScreen': ${GlobalSchema.pushScreen.value}")
                                Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'lastMainScreenFragment': ${GlobalSchema.lastMainScreenPagerPage.value}")
                                Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'pagerstate.currentPage': ${horizontalPagerState.currentPage}")
                                Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'pagerstate.targetPage': ${horizontalPagerState.targetPage}")
                                Log.d("Groaker", "[ScreenMain.getBottomBar] Current value of 'bottomNavPagerScrollTo': ${bottomNavPagerScrollTo.intValue}")

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
                            Text(stringResource(R.string.app_name_alias), fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)
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

/*
/**
 * @param menu optionally determines which tab to open upon display.
 */
class ScreenMain2(menu: String?, submenu: String?) : ComponentActivity() {

    // Stores the passed arguments class-wide.
    private val menu = menu
    private val submenu = submenu

    // Enlists all of the fragments that will be displayed in this particular screen.
    private val fragRoutes = listOf(
        NavigationRoutes().FRAG_MAIN_HOME,
        NavigationRoutes().FRAG_MAIN_SERVICES,
        NavigationRoutes().FRAG_BLANK,
        NavigationRoutes().FRAG_MAIN_NEWS,
        NavigationRoutes().FRAG_MAIN_EVENTS
    )

    // This is the control variable for showing/hiding the screen's fragments.
    // We use this in place of NavHost navigation because, somehow, nested NavHosts isn't yet supported in Composable.
    private var fragmentVisibility = listOf(
        // Default value, display the first fragment upon first load.
        mutableStateOf(menu == null || menu == NavigationRoutes().FRAG_MAIN_HOME),
        mutableStateOf(menu != null && menu == NavigationRoutes().FRAG_MAIN_SERVICES),
        mutableStateOf(false),
        mutableStateOf(menu != null && menu == NavigationRoutes().FRAG_MAIN_NEWS),
        mutableStateOf(menu != null && menu == NavigationRoutes().FRAG_MAIN_EVENTS)
    )

    // Ensures that we don't manually create each and every one of the navigation bar item.
    // We save the state of the index of the currently selected fragment in this screen.
    private var selectedFragItem = mutableStateOf(0)

    // The coroutine that will change the current page of the horizontal pager.
    // private val pagerCoroutineScope = rememberCoroutineScope()

    // Save the current pager page. This ensures that we can update and show the correct bottom nav selected menu.
    // private var horizontalPagerState = rememberPagerState { 4 }
    private var currentPagerPage = mutableStateOf(0)

    // Set this value to an appropriate integer to scroll the pager to a different page.
    private var scrollPagerToPage = mutableStateOf(0)

    // Set this value to "!doScrollPager" to trigger horizontal pager page scroll.
    private var doScrollPager = mutableStateOf(false)

    // Granulary set the visibility of the main screen display
    private var showMainDisplay = mutableStateOf(true)

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Log.d("Groaker", "Well, in ScreenMain, you passed: $menu")
        Log.d("Groaker", "You got $submenu submenu")

        // By default, we set it to the value of "fragmentVisibility" item that equals "true".
        fragmentVisibility.forEachIndexed { index, item ->
            if (item.value) { selectedFragItem.value = index }
        }

        Scaffold (
            bottomBar = { getBottomBar(screenController, fragmentController, context) },
            topBar = { getTopBar(screenController, fragmentController, context) },
            floatingActionButton =  { getFloatingActionButton(screenController, fragmentController, context) },
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            // We need granularity so that we can control whether to display/hide the main composable element.
            AnimatedVisibility( visible = showMainDisplay.value ) {

                // This bottom sheet displays all features and menus of the app.
                // It will only be shown when the FAB is clicked.
                getBottomSheet(screenController, fragmentController, context)

                // Setting up the layout of all of the fragments.
                // Then wrapping each fragment in AnimatedVisibility so that we can manually control their visibility.
                Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                    /*
                    AnimatedVisibility(visible = fragmentVisibility[0].value) {
                        FragmentHome().getComposable(screenController, fragmentController, context)
                    }
                    AnimatedVisibility(visible = fragmentVisibility[1].value) {
                        FragmentServices(submenu).getComposable(screenController, fragmentController, context)
                    }
                    AnimatedVisibility(visible = fragmentVisibility[3].value) {
                        FragmentNews().getComposable(screenController, fragmentController, context)
                    }
                    AnimatedVisibility(visible = fragmentVisibility[4].value) {
                        FragmentEvents().getComposable(screenController, fragmentController, context)
                    }*/

                    // Enabling pager for managing and layouting multiple fragments in a given screen.
                    // SOURCE: https://www.composables.com/foundation/horizontalpager
                    val horizontalPagerState = rememberPagerState ( initialPage = 0, pageCount = {4} )
                    HorizontalPager( state = horizontalPagerState, modifier = Modifier.fillMaxSize() ) { page ->
                        when (page) {
                            0 -> FragmentHome().getComposable(screenController, fragmentController, context)
                            1 -> FragmentServices(submenu).getComposable(screenController, fragmentController, context)
                            2 -> FragmentNews().getComposable(screenController, fragmentController, context)
                            3 -> FragmentEvents().getComposable(screenController, fragmentController, context)
                        }
                    }

                    // For updating the state of the bottom nav.
                    currentPagerPage.value = horizontalPagerState.currentPage

                    // Force recomposition of the horizontal pager.
                    // SOURCE: https://stackoverflow.com/a/77289069
                    LaunchedEffect(doScrollPager.value) {
                        // Scroll to the desired pager page.
                        horizontalPagerState.animateScrollToPage(scrollPagerToPage.value)
                    }
                }

                // Ensure that when we are at the first screen upon clicking "back",
                // the app is exited instead of continuing to navigate back to the previous screens.
                // SOURCE: https://stackoverflow.com/a/69151539
                BackHandler {
                    val curRoute = fragRoutes[selectedFragItem.value]
                    if (curRoute == NavigationRoutes().FRAG_MAIN_HOME) {
                        Toast.makeText(context, "You just clicked $curRoute and exited the app!", Toast.LENGTH_SHORT).show()

                        // Exit the application.
                        // SOURCE: https://stackoverflow.com/a/67402808
                        (context as ComponentActivity).finish()
                    } else if (
                        curRoute == NavigationRoutes().FRAG_MAIN_EVENTS ||
                        curRoute == NavigationRoutes().FRAG_MAIN_NEWS ||
                        curRoute == NavigationRoutes().FRAG_MAIN_SERVICES
                    ) {
                        // If we are in the main screen but not at fragment one, navigate the app to fragment one.
                        selectedFragItem.value = 0

                        // After selecting the value of the destination fragment,
                        // show the destination fragment and hide the rest of other fragments in the current screen.
                        fragmentVisibility.forEach { it.value = false }
                        fragmentVisibility[selectedFragItem.value].value = true
                    } else {
                        // Do nothing.
                    }
                }
            }
        }

        // The link confirmation dialog.
        this.getLinkConfirmationDialog(screenController, fragmentController, context)

        // Ensure that when we click "back" to a certain screen (or when the screen is arbitrarily changed),
        // the bottom navigation menu item is highlighted
        // SOURCE: https://stackoverflow.com/a/77403140
        fragmentController.addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination, arguments -> run {
            selectedFragItem.value = fragRoutes.indexOf(destination.route)
        }
        })
    }

    @Composable
    private fun getBottomBar(screenController: NavHostController, fragmentController: NavHostController, context: Context) {

        // Leave the third (n = 2) element empty, because it is FAB's place.
        val navItems = listOf(
            stringResource(R.string.bottomnav_menu_home),
            stringResource(R.string.bottomnav_menu_services),
            stringResource(R.string.empty_string),
            stringResource(R.string.bottomnav_menu_3),
            stringResource(R.string.bottomnav_menu_events)
        )

        BottomAppBar() {
            // Ensures that the nav bar stays at the bottom
            // SOURCE: https://stackoverflow.com/q/70904979
            Row ( modifier = Modifier.weight(1f, false) ) {
                navItems.forEachIndexed { index, item ->
                    if (index == 2) {
                        // Create an empty nav item in place of the docked FAB
                        // SOURCE: https://stackoverflow.com/a/67536003
                        NavigationBarItem(
                            icon = {},
                            label = {},
                            selected = false,
                            onClick = {},
                            enabled = false
                        )
                    } else {
                        // Getting the current pager page.
                        var curPage = currentPagerPage.value
                        Log.d("Groaker", "CurrentPage: $curPage")

                        // Offsetting, since the third (n = 2) bottom nav menu is empty, replaced with FAB.
                        if (curPage >= 2) { curPage += 1 }

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                            label = { Text(item) },
                            selected = curPage == index,
                            onClick = {
                                // Navigate to a certain pager page, according to the selected bottom nav menu.
                                var offsetIndex = index
                                if (index >= 2) { offsetIndex -= 1}
                                scrollPagerToPage.value = offsetIndex

                                // Trigger the expression to immediately change the horizontal pager page.
                                doScrollPager.value = !doScrollPager.value
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun getFloatingActionButton(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        FloatingActionButton (
            onClick = { showBottomSheet.value = true },
            shape = CircleShape,
            modifier = Modifier.scale(1.5f).offset(0.dp, 30.dp)
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = "Add FAB")
        }
    }

    // Controls, from an outside composable, whether to display the link confirmation dialog.
    private var showLinkConfirmationDialog = mutableStateOf(false)

    // This becomes the "parameters" of the link confirmation dialog.
    // private var paramLinkConfirmationDialog = mutableStateOf<ParameterSchema>(ParameterSchema("", "", "", "", "", this.baseContext))
    private var paramLinkConfirmationDialog = mutableStateOf(false)

        /**
     * This function displays the confirmation dialog that asks the user
     * whether the user wants to proceed opening a certain link.
     * SOURCE: https://www.composables.com/tutorials/dialogs
     * SOURCE: https://developer.android.com/develop/ui/compose/components/dialog
     */
    @Composable
    private fun getLinkConfirmationDialog(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        if (showLinkConfirmationDialog.value) {
            AlertDialog(
                onDismissRequest = { showLinkConfirmationDialog.value = false },
                title = { Text(paramLinkConfirmationDialog.value.title) },
                text = {
                    Column {
                        Text("Do you want to open the following link?")
                        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = paramLinkConfirmationDialog.value.url,
                                onValueChange = { /* NOTHING */ },
                                label = { Text("-") },
                                enabled = false
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLinkConfirmationDialog.value = false }) {
                        Text("Proceed".uppercase())
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLinkConfirmationDialog.value = false }) {
                        Text("Back".uppercase())
                    }
                }
            )
        }
    }

    // Controls whether the bottom sheet should be displayed or not
    private var showBottomSheet = mutableStateOf(false)

    @Composable
    @ExperimentalMaterial3Api
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getBottomSheet(screenController: NavHostController, fragmentController: NavHostController, context: Context) {

        // Enable the bottom sheet that scrolls overflown content upon full expansion.
        // SOURCE: https://stackoverflow.com/q/78756531/8101395
        val scope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()
        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxHeight(),
            ) {
                // This determines the span size of the following scrollable container.
                val spanSize = 3

                LazyVerticalGrid( columns = GridCells.Fixed(spanSize), modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.Center) {
                    /* Archival action menu section. */
                    item(span = { GridItemSpan(spanSize) }) {
                        Text("Arsip", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 20.dp).padding(top = 30.dp), textAlign = TextAlign.Center)
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_newspaper_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Arsip Warta Jemaat", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_book_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Arsip Liturgi Umum", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_book_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("English Service Liturgy Archive", textAlign = TextAlign.Center)
                            }
                        }
                    }

                    /* Textual devotion action menu section. */
                    item(span = { GridItemSpan(spanSize) }) {
                        Text("Renungan YKB", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 20.dp).padding(top = 30.dp), textAlign = TextAlign.Center)
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                            // Opens the relevant WebView destination URL.
                            val destination = "some_url"
                            screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_child_care_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Renungan Kiddy", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                            // Opens the relevant WebView destination URL.
                            val destination = "some_url"
                            screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_face_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Teens for Christ", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                            // Opens the relevant WebView destination URL.
                            val destination = "some_url"
                            screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_person_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Youth for Christ", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                            // Opens the relevant WebView destination URL.
                            val destination = "some_url"
                            screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_family_restroom_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Renungan Wasiat", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton (onClick = {
                            // Hides the modal sheet.
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                            // Opens the relevant WebView destination URL.
                            val destination = "some_url"
                            screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                        }, modifier = Modifier.padding(5.dp).height(125.dp), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(5.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_elderly_woman_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Renungan Usia Indah", textAlign = TextAlign.Center)
                            }
                        }
                    }

                    /* Church form action menu section. */
                    item(span = { GridItemSpan(spanSize) }) {
                        Text("Formulir", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp).padding(top = 20.dp), textAlign = TextAlign.Center)
                    }
                    item() {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Opens the relevant WebView destination URL.
                                val destination = "some_url"
                                screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_water_drop_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Baptis Anak", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Opens the relevant WebView destination URL.
                                val destination = "some_url"
                                screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_water_drop_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Baptis Dewasa dan Sidi", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Opens the relevant WebView destination URL.
                                val destination = "some_url"
                                screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_group_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Bina Pranikah", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Opens the relevant WebView destination URL.
                                val destination = "some_url"
                                screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_favorite_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Pemberkatan Nikah", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Opens the relevant WebView destination URL.
                                val destination = "some_url"
                                screenController.navigate("${NavigationRoutes().SCREEN_WEBVIEW}/${destination}")
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_person_add_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Atestasi", textAlign = TextAlign.Center)
                            }
                        }
                    }

                    /* External links and social media channels. */
                    item(span = { GridItemSpan(spanSize) }) {
                        Text("Tautan Luar", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp).padding(top = 20.dp), textAlign = TextAlign.Center)
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Invoke the link confirmation dialog, to open the specific external link.
                                paramLinkConfirmationDialog.value.title = "Membangun Koloni"
                                paramLinkConfirmationDialog.value.url = "mailto:giga@purbalingga.com"
                                showLinkConfirmationDialog.value = true
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.remixicon_at_fill_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("E-Surat", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Invoke the link confirmation dialog, to open the specific external link.
                                paramLinkConfirmationDialog.value.title = "Membangun Koloni"
                                paramLinkConfirmationDialog.value.url = "mailto:giga@purbalingga.com"
                                showLinkConfirmationDialog.value = true
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.remixicon_wordpress_fill_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Situs Web", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Invoke the link confirmation dialog, to open the specific external link.
                                paramLinkConfirmationDialog.value.title = "Membangun Koloni"
                                paramLinkConfirmationDialog.value.url = "mailto:giga@purbalingga.com"
                                showLinkConfirmationDialog.value = true
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.remixicon_youtube_fill_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("YouTube", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Invoke the link confirmation dialog, to open the specific external link.
                                paramLinkConfirmationDialog.value.title = "Membangun Koloni"
                                paramLinkConfirmationDialog.value.url = "mailto:giga@purbalingga.com"
                                showLinkConfirmationDialog.value = true
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.remixicon_facebook_box_fill_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Facebook", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Invoke the link confirmation dialog, to open the specific external link.
                                paramLinkConfirmationDialog.value.title = "Membangun Koloni"
                                paramLinkConfirmationDialog.value.url = "mailto:giga@purbalingga.com"
                                showLinkConfirmationDialog.value = true
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.remixicon_instagram_fill_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Instagram", textAlign = TextAlign.Center)
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                // Hides the modal sheet.
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) { showBottomSheet.value = false } }

                                // Invoke the link confirmation dialog, to open the specific external link.
                                paramLinkConfirmationDialog.value.title = "Membangun Koloni"
                                paramLinkConfirmationDialog.value.url = "mailto:giga@purbalingga.com"
                                showLinkConfirmationDialog.value = true
                            },
                            modifier = Modifier.padding(5.dp).height(125.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.remixicon_whatsapp_fill_48),
                                    contentDescription = "Some name",
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("WhatsApp", textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    "GKI Salatiga+",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = { },
            actions = {
                IconButton(onClick = { Toast.makeText(context, "NavIcon cliked", Toast.LENGTH_SHORT).show() }) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = ""
                    )
                }
                val coroutineScope = rememberCoroutineScope()
                IconButton(onClick = {
                    // Prevents random trigger of animation upon switching to a different screen.
                    // fragmentVisibility.forEach { it.value = false }
                    coroutineScope.launch {
                        // currentPagerPage.value = 3
                        showMainDisplay.value = false
                        delay(1000L)
                        screenController.navigate(NavigationRoutes().SCREEN_ABOUT)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = ""
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}*/
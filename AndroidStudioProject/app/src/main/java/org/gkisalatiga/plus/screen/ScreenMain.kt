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
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentInfo
import org.gkisalatiga.plus.fragment.FragmentServices
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.Downloader
import org.gkisalatiga.plus.lib.ImagePainterFromFile

import org.gkisalatiga.plus.lib.NavigationRoutes

class ScreenMain() : ComponentActivity() {

    // Enlists all of the fragments that will be displayed in this particular screen.
    private val fragRoutes = listOf(
        NavigationRoutes().FRAG_MAIN_HOME,
        NavigationRoutes().FRAG_MAIN_SERVICES,
        NavigationRoutes().FRAG_MAIN_INFO,
    )

    // Used by the bottom nav to command the scrolling of the horizontal pager.
    private var bottomNavPagerScrollTo = mutableStateOf(fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value))

    // Determines the top bar title.
    private var topBarTitle = mutableStateOf((GlobalSchema.context).resources.getString(R.string.app_name_alias))

    // Controls the horizontal scrolling of the pager.
    private lateinit var horizontalPagerState: PagerState

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        // Initializing the horizontal pager.
        horizontalPagerState = rememberPagerState ( pageCount = {fragRoutes.size}, initialPage = fragRoutes.indexOf(GlobalSchema.lastMainScreenPagerPage.value) )

        Log.d("Groaker", "Current value of 'pushScreen': ${GlobalSchema.pushScreen.value}")
        Log.d("Groaker", "Current value of 'lastMainScreenFragment': ${GlobalSchema.lastMainScreenPagerPage.value}")
        Log.d("Groaker", "Current value of 'pagerstate.currentPage': ${horizontalPagerState.currentPage}")
        Log.d("Groaker", "Current value of 'pagerstate.targetPage': ${horizontalPagerState.targetPage}")
        Log.d("Groaker", "Current value of 'bottomNavPagerScrollTo': ${bottomNavPagerScrollTo.value}")

        // Listen to the bottom nav's request to change the horizontal pager page.
        key(bottomNavPagerScrollTo.value) {
            doNavBarScrollPager()
        }

        // Listen to the change in the horizontal pager's page state.
        // Then change the global page state accordingly.
        key (horizontalPagerState.targetPage) {
            GlobalSchema.lastMainScreenPagerPage.value = fragRoutes[horizontalPagerState.targetPage]
        }

        Scaffold (
            bottomBar = { getBottomBar() },
            topBar = { getTopBar() },
            floatingActionButton =  { },
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            // Setting up the layout of all of the fragments.
            // Then wrapping each fragment in AnimatedVisibility so that we can manually control their visibility.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {

                // Enabling pager for managing and layouting multiple fragments in a given screen.
                // SOURCE: https://www.composables.com/foundation/horizontalpager
                HorizontalPager( state = horizontalPagerState, modifier = Modifier.fillMaxSize() ) { page ->
                    when (page) {
                        0 -> FragmentHome().getComposable()
                        1 -> FragmentServices().getComposable()
                        2 -> FragmentInfo().getComposable()
                    }
                }
            }

            // Ensure that when we are at the first screen upon clicking "back",
            // the app is exited instead of continuing to navigate back to the previous screens.
            // SOURCE: https://stackoverflow.com/a/69151539
            BackHandler {
                val curRoute = GlobalSchema.lastMainScreenPagerPage.value
                if (curRoute == NavigationRoutes().FRAG_MAIN_HOME) {
                    Toast.makeText(GlobalSchema.context, "You just clicked $curRoute and exited the app!", Toast.LENGTH_SHORT).show()

                    // Exit the application.
                    // SOURCE: https://stackoverflow.com/a/67402808
                    (GlobalSchema.context as ComponentActivity).finish()
                } else if (
                    curRoute == NavigationRoutes().FRAG_MAIN_INFO ||
                    curRoute == NavigationRoutes().FRAG_MAIN_SERVICES
                ) {
                    // Since we are in the main screen but not at fragment one,
                    // navigate the app to fragment one.
                    bottomNavPagerScrollTo.value = 0
                } else {
                    // Do nothing.
                }
            }
        }

        /* TODO */
        key(GlobalSchema.pathToDownloadedPrivateFile.value) {
            val downloadedPath = GlobalSchema.pathToDownloadedPrivateFile.value
            if (downloadedPath.isNotEmpty()) {
                Log.d("Groaker", "Downloaded to $downloadedPath successfully!")

                // Reading the data.
                // SOURCE: https://stackoverflow.com/a/45202002
                // val file = File(downloadedPath)
                // val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }
                // Log.d("Groaker-Dump", inputAsString)

                /* Displaying the image. */
                showLinkConfirmationDialog.value = true
                if (showLinkConfirmationDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showLinkConfirmationDialog.value = false },
                        title = { "Test" },
                        text = {
                            Image(
                                painter = ImagePainterFromFile(downloadedPath),
                                contentDescription = ""
                            )
                        },
                        dismissButton = {
                            TextButton(onClick = { showLinkConfirmationDialog.value = false }) {
                                Text("Keluar".uppercase())
                            }
                        },
                        confirmButton = { }
                    )
                }
            }
        }

        // The link confirmation dialog.
        // getLinkConfirmationDialog()
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
                                bottomNavPagerScrollTo.value = index
                            }
                        )
                    }
                }

            }
        }
    }

    @Composable
    private fun doNavBarScrollPager() {
        // Force recomposition of the horizontal pager.
        // SOURCE: https://stackoverflow.com/a/77289069
        LaunchedEffect(bottomNavPagerScrollTo.value) {
            Log.d("Groaker", "Launching 'LaunchedEffect' on the horizontal pager state ...")

            // Scroll to the desired pager page.
            horizontalPagerState.animateScrollToPage(bottomNavPagerScrollTo.value)
        }
    }

    // Controls, from an outside composable, whether to display the link confirmation dialog.
    private var showLinkConfirmationDialog = mutableStateOf(false)

    /**
     * This function displays the confirmation dialog that asks the user
     * whether the user wants to proceed opening a certain link.
     * SOURCE: https://www.composables.com/tutorials/dialogs
     * SOURCE: https://developer.android.com/develop/ui/compose/components/dialog
     */
    /*@Composable
    private fun getLinkConfirmationDialog() {
        if (showLinkConfirmationDialog.value) {
            AlertDialog(
                onDismissRequest = { showLinkConfirmationDialog.value = false },
                title = { Text(GlobalSchema.norender["linkConfirmTitle"].toString()) },
                text = {
                    Column {
                        Text("Do you want to open the following link?")
                        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = GlobalSchema.norender["linkConfirmURL"].toString(),
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
    }*/

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    topBarTitle.value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = { },
            actions = {
                IconButton(
                    onClick = {
                        Toast.makeText(GlobalSchema.context, "NavIcon cliked", Toast.LENGTH_SHORT).show()

                        /* TODO */
                        // Attempt to download.
                        // ---
                        // Get filename.
                        // SOURCE: https://stackoverflow.com/a/26570321
                        val fileToDownload = "https://ewarta.gkiserpong.org/wp-content/ewarta/eWarta-20240630.jpg"
                        val filename = fileToDownload.substring(fileToDownload.lastIndexOf("/") + 1)
                        Log.d("Groaker", "We want to download: $fileToDownload")
                        Log.d("Groaker", "It will be saved as: $filename")
                        // Downloader().asPrivateFile("https://raw.githubusercontent.com/groaking/groaking.github.io/main/playground/ewarta.json", "sample.json")
                        // Downloader().asPrivateFile("https://ewarta.gkiserpong.org/wp-content/ewarta/eWarta-20240630.jpg", "sampleimage.jpg")
                        Downloader().asPrivateFile(fileToDownload, filename)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "Localiszes desc"
                    )
                }
                val coroutineScope = rememberCoroutineScope()
                IconButton(onClick = {
                    // Opens the "About app" screen.
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_ABOUT
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Localiszes desc"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
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
                        contentDescription = "Localiszes desc"
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
                        contentDescription = "Localiszes desc"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}*/
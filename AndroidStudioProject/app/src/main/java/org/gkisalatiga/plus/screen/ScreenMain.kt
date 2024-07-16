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
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.GridView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.fragment.FragmentAbout
import org.gkisalatiga.plus.fragment.FragmentBlank
import org.gkisalatiga.plus.fragment.FragmentEvents
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentNews
import org.gkisalatiga.plus.fragment.FragmentServices

import org.gkisalatiga.plus.lib.NavigationRoutes

class ScreenMain : ComponentActivity() {

    // Enlists all of the fragments that will be displayed in this particular screen.
    private val fragRoutes = listOf(
        NavigationRoutes().FRAG_MAIN_HOME,
        NavigationRoutes().FRAG_MAIN_SERVICES,
        NavigationRoutes().FRAG_BLANK,
        NavigationRoutes().FRAG_MAIN_NEWS,
        NavigationRoutes().FRAG_MAIN_EVENTS
    )

    // Ensures that we don't manually create each and every one of the navigation bar item.
    // We save the state of the index of the currently selected fragment in this screen.
    private var selectedFragItem = mutableStateOf(0)

    // This is the control variable for showing/hiding the screen's fragments.
    // We use this in place of NavHost navigation because, somehow, nested NavHosts isn't yet supported in Composable.
    private var fragmentVisibility = listOf(
        // Default value, display the first fragment upon first load.
        mutableStateOf(true),
        mutableStateOf(false),
        mutableStateOf(false),
        mutableStateOf(false),
        mutableStateOf(false)
    )

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Scaffold (
            bottomBar = { this.getBottomBar(screenController, fragmentController, context) },
            topBar = { this.getTopBar(screenController, fragmentController, context) },
            floatingActionButton =  { this.getFloatingActionButton(screenController, fragmentController, context) },
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            // This bottom sheet displays all features and menus of the app.
            // It will only be shown when the FAB is clicked.
            this.getBottomSheet(screenController, fragmentController, context)

            // Setting up the layout of all of the fragments.
            // Then wrapping each fragment in AnimatedVisibility so that we can manually control their visibility.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                AnimatedVisibility(visible = fragmentVisibility[0].value) {
                    FragmentHome().getComposable(screenController, fragmentController, context)
                }
                AnimatedVisibility(visible = fragmentVisibility[1].value) {
                    FragmentServices().getComposable(screenController, fragmentController, context)
                }
                AnimatedVisibility(visible = fragmentVisibility[3].value) {
                    FragmentNews().getComposable(screenController, fragmentController, context)
                }
                AnimatedVisibility(visible = fragmentVisibility[4].value) {
                    FragmentEvents().getComposable(screenController, fragmentController, context)
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
            stringResource(R.string.bottomnav_menu_1),
            stringResource(R.string.bottomnav_menu_2),
            stringResource(R.string.empty_string),
            stringResource(R.string.bottomnav_menu_3),
            stringResource(R.string.bottomnav_menu_4)
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
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedFragItem.value == index,
                            onClick = {
                                selectedFragItem.value = index

                                // Navigate between fragments when the nav item is clicked
                                fragmentVisibility.forEach { it.value = false }
                                fragmentVisibility[index].value = true
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
                IconButton(onClick = {
                    screenController.navigate(NavigationRoutes().SCREEN_ABOUT)
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
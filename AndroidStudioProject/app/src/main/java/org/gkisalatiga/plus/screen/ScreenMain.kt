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
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R

import org.gkisalatiga.plus.lib.NavigationRoutes

class ScreenMain : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

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
        }

        // Ensure that when we click "back" to a certain screen (or when the screen is arbitrarily changed),
        // the bottom navigation menu item is highlighted
        // SOURCE: https://stackoverflow.com/a/77403140
        fragmentController.addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination, arguments -> run {
            selectedFragItem.value = fragRoutes.indexOf(destination.route)
        }
        })
    }

    // Ensures that we don't manually create each and every one of the navigation bar item
    private var selectedFragItem = mutableStateOf(0)
    private val fragRoutes = listOf(
        NavigationRoutes().FRAG_MAIN_HOME,
        NavigationRoutes().FRAG_MAIN_SERVICES,
        "",
        NavigationRoutes().FRAG_MAIN_NEWS,
        NavigationRoutes().FRAG_MAIN_EVENTS
    )

    @Composable
    private fun getBottomBar(screenController: NavHostController, fragmentController: NavHostController, context: Context) {

        // Leave the third (n = 2) element empty, because it is FAB's place.
        val navItems = listOf(
            stringResource(R.string.bottomnav_menu_1),
            stringResource(R.string.bottomnav_menu_2),
            "",
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

                                // Navigate between screens when the nav item is clicked
                                fragmentController.navigate(fragRoutes[index])
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
        // SOURCE: https://developer.android.com/develop/ui/compose/components/bottom-sheets
        val scope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()
        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = sheetState
            ) {
                Button (
                    onClick = {
                        // Hides the modal sheet
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    }
                ) {
                    Text("Close the modal sheet")
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
                    fragmentController.navigate(NavigationRoutes().FRAG_ABOUT)
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
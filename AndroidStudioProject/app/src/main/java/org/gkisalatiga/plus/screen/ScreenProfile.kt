/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display church profile, information on the board and committee member of the church,
 * and pastorate of the current year.
 * The information shown will depend on the argument passed.
 * SOURCE: https://www.composables.com/tutorials/navigation-tutorial
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.fragment.FragmentAbout
import org.gkisalatiga.plus.fragment.FragmentAssembly
import org.gkisalatiga.plus.fragment.FragmentChurchProfile
import org.gkisalatiga.plus.fragment.FragmentEvents
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentMinistry
import org.gkisalatiga.plus.fragment.FragmentNews
import org.gkisalatiga.plus.fragment.FragmentPastorate
import org.gkisalatiga.plus.fragment.FragmentServices
import org.gkisalatiga.plus.lib.AppDatabase

import org.gkisalatiga.plus.lib.NavigationRoutes

/**
 * @param frag the destination ScreenProfile fragment that needs to be displayed
 */
class ScreenProfile(private val frag: String?) : ComponentActivity() {
    // This is the control variable for showing/hiding the screen's fragments.
    // We use this in place of NavHost navigation because, somehow, nested NavHosts isn't yet supported in Composable.
    // The default value is "no visibility". Items are shown based on the above passed arguments.
    private var fragmentVisibility = listOf(
        mutableStateOf(false),
        mutableStateOf(false),
        mutableStateOf(false),
        mutableStateOf(false)
    )

    // This is the control array for checking the fragment navigation name of each fragment.
    private val fragmentNavCaller = listOf(
        NavigationRoutes().FRAG_PROFILE_CHURCH,
        NavigationRoutes().FRAG_PROFILE_PASTOR,
        NavigationRoutes().FRAG_PROFILE_ASSEMBLY,
        NavigationRoutes().FRAG_PROFILE_MINISTRY
    )

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Scaffold (
            topBar = { this.getTopBar(screenController, fragmentController, context) }
                ) {
            // Hiding/displaying a certain fragment based on the passed argument.
            Toast.makeText(context, "Called fragment: $frag", Toast.LENGTH_SHORT).show()
            if (frag != null) {
                fragmentNavCaller.forEach { l ->
                    fragmentVisibility[fragmentNavCaller.indexOf(l)].value = l == frag.toString()
                }
            }

            // Setting up the layout of all of the fragments.
            // Then wrapping each fragment in AnimatedVisibility so that we can manually control their visibility.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                AnimatedVisibility(visible = fragmentVisibility[0].value) {
                    FragmentChurchProfile().getComposable(screenController, fragmentController, context)
                }
                AnimatedVisibility(visible = fragmentVisibility[1].value) {
                    FragmentPastorate().getComposable(screenController, fragmentController, context)
                }
                AnimatedVisibility(visible = fragmentVisibility[2].value) {
                    FragmentAssembly().getComposable(screenController, fragmentController, context)
                }
                AnimatedVisibility(visible = fragmentVisibility[3].value) {
                    FragmentMinistry().getComposable(screenController, fragmentController, context)
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(R.string.screenprofile_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    screenController.navigate(NavigationRoutes().SCREEN_MAIN)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }
}
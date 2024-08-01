/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display meta-application information about GKI Salatiga Plus.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes


class ScreenAbout() : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        val ctx = LocalContext.current

        Log.d("Groaker", "Last selected fragment of main screen: ${GlobalSchema.lastMainScreenPagerPage.value}")

        // Obtain the app's essential information.
        // SOURCE: https://stackoverflow.com/a/6593822
        val pInfo: PackageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0)
        val vName = pInfo.versionName
        val vCode = pInfo.versionCode

        // Get app name.
        // SOURCE: https://stackoverflow.com/a/15114434
        val applicationInfo: ApplicationInfo = ctx.getApplicationInfo()
        val stringId = applicationInfo.labelRes
        val appName = if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else ctx.getString(stringId)

        Scaffold (
            topBar = { getTopBar() }
                ) {

            val scrollState = rememberScrollState()
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
                    .verticalScroll(scrollState)) {
                /* Show app logo, name, and version. */
                Box (Modifier.padding(vertical = 15.dp)) {
                    Surface (
                        shape = CircleShape,
                        modifier = Modifier.size(100.dp),
                        onClick = {
                            /* You know what this is. */
                            if (GlobalSchema.DEBUG_ENABLE_EASTER_EGG) {
                                Toast.makeText(ctx, "\uD83D\uDC23", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Image(painterResource(R.mipmap.ic_launcher_foreground), "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Text(appName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(vName, fontSize = 18.sp)

                Spacer(Modifier.height(20.dp))
                Text("Dibuat oleh Sam, Jeffrey, dan Joaquim")
                Text("Semoga di-approve sama MJ \uD83D\uDDFF :v")
            }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(R.string.screenabout_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
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
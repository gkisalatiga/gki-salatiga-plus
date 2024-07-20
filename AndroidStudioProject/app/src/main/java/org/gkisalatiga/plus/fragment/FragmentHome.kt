/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.content.Context
import android.provider.Settings.Global
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes

class FragmentHome() : ComponentActivity() {

    // The following defines the visible menu buttons shown in the main menu,
    // as well as their corresponding navigation targets.
    private val btnRoutes = listOf(
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
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

        // Setting the layout to center both vertically and horizontally
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            /* Displaying the welcome banner in the main menu. */
            Surface (
                shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sample_welcome_banner),
                    contentDescription = "Some name",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }

            /* Displaing the main menu action buttons. */
            // Assumes btnRoutes, btnLabels, and btnIcons all have the same size.
            val spanSize: Int = 4
            LazyVerticalGrid(
                columns = GridCells.Fixed(spanSize),
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                btnRoutes.forEachIndexed { index, str ->
                    item {
                        OutlinedButton (
                            onClick = {
                                // This will be triggered when the main menu button is clicked.
                                if (btnRoutes[index] != NavigationRoutes().SCREEN_BLANK) {
                                    GlobalSchema.pushScreen.value = btnRoutes[index]
                                }
                                      },
                            modifier = Modifier.padding(5.dp).height(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            // The main menu element wrapper.
                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp)) {
                                // The main menu action button icon.
                                Image(
                                    painter = painterResource(btnIcons[index]),
                                    contentDescription = btnDescriptions[index],
                                    alignment = Alignment.Center,
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                // The text.
                                Text(btnLabels[index], textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

        }
    }

}
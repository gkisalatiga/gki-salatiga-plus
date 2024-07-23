/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.app.DownloadManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.abstract.GlobalClass
import org.gkisalatiga.plus.global.GlobalSchema
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.NavigationRoutes
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors

/**
 * @param submenu determines which service tab is opened upon load.
 */
class FragmentInfo() : ComponentActivity() {

    // Defines the routing of each "church info" card.
    private val cardRoutes = listOf(
        NavigationRoutes().FRAG_PROFILE_CHURCH,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
    )

    // Defines the label of each "church info" card.
    private val cardLabels = listOf(
        (GlobalSchema.context).resources.getString(R.string.card_info_church_profile),
        (GlobalSchema.context).resources.getString(R.string.card_info_church_pastor),
        (GlobalSchema.context).resources.getString(R.string.card_info_church_assembly),
        (GlobalSchema.context).resources.getString(R.string.card_info_church_ministry),
        (GlobalSchema.context).resources.getString(R.string.card_info_church_contact),
    )

    // Defines the icon description in each of the "church info" card.
    private val cardIconDescriptions = listOf(
        (GlobalSchema.context).resources.getString(R.string.card_desc_info_church_profile),
        (GlobalSchema.context).resources.getString(R.string.card_desc_info_church_pastor),
        (GlobalSchema.context).resources.getString(R.string.card_desc_info_church_assembly),
        (GlobalSchema.context).resources.getString(R.string.card_desc_info_church_ministry),
        (GlobalSchema.context).resources.getString(R.string.card_desc_info_church_contact),
    )

    // Defines and locates the icon for each card in the "church info" fragment.
    private val cardIcons = listOf(
        R.drawable.baseline_person_48,
        R.drawable.baseline_flaky_256,
        R.drawable.baseline_flaky_256,
        R.drawable.baseline_flaky_256,
        R.drawable.baseline_flaky_256,
    )

    @Composable
    public fun getComposable() {
        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            /* Display the individual "church info" card. */
            Column ( modifier = Modifier.padding(top = 10.dp) ) {
                // Assumes cardRoutes, cardIcons, cardLabels, and cardIconDescriptions all have the same size.
                cardRoutes.forEachIndexed { index, str ->
                    Card(
                        onClick = {
                            Toast.makeText((GlobalSchema.context), "You just clicked: $str!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.padding(bottom = 7.dp).height(80.dp)
                        ) {
                        Row ( modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically ) {
                            // The card icon.
                            Image(
                                painter = painterResource(cardIcons[index]),
                                contentDescription = cardIconDescriptions[index],
                                modifier = Modifier.padding(5.dp),
                                contentScale = ContentScale.Fit,
                            )
                            Spacer( modifier = Modifier.width(20.dp) )
                            // The card label.
                            Text(cardLabels[index], fontSize = 16.sp)
                            Spacer( modifier = Modifier.weight(1f) )
                            Icon(Icons.AutoMirrored.Default.ArrowForward, "", modifier = Modifier.padding(end = 5.dp))
                        }
                    }
                }
            }
        }

    }


}
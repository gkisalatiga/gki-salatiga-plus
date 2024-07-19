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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
        NavigationRoutes().SCREEN_BLANK,
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
                                Toast.makeText((GlobalSchema.context), "You may have clicked: $str!", Toast.LENGTH_SHORT).show()
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

    /*
    /**
     * Navigation between screens
     * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
     */
    @Composable
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Column {
            // Setting the layout to center both vertically and horizontally
            // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
            val scrollState = rememberScrollState()
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
            ) {
                /* Displaying the welcome banner in the main menu. */
                Surface (
                    shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.sample_welcome_banner),
                        contentDescription = "Some name",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }

                /* Displaying the "main menu" options. */
                LazyRow ( modifier = Modifier.padding(vertical = 10.dp).padding(horizontal = 0.dp) ) {
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                    // The main menu "main" content.
                    items (4) { index ->
                        Button(onClick = {
                            screenController.navigate("${NavigationRoutes().SCREEN_PROFILE}/${profileItemDestination[index]}")
                        }, modifier = Modifier.padding(horizontal = 5.dp).wrapContentWidth(), shape = RoundedCornerShape(10.dp)) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                                // Ensures that the drawable image to be used matches with theme accent color.
                                // SOURCE: https://stackoverflow.com/a/15266893
                                // getDrawable(profileItemIcon[index])?.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC)

                                Image(
                                    painter = painterResource(profileItemIcon[index]),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(profileItemText[index])
                            }
                        }
                    }
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                }
                Spacer(modifier = Modifier.height(10.dp))

                /* Displaying the daily Bible verses. */
                Column ( modifier = Modifier.padding(10.dp) ) {
                    Text("Inspirasi Harian", fontWeight = FontWeight.Bold, fontSize = 26.sp, modifier = Modifier.padding(bottom = 5.dp))
                    Row {
                        Text("Mazmur 203:1-1002", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("[TB1]")
                        Text("Senin, 32 Juli 2024", fontStyle = FontStyle.Italic, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                    }
                    Text("Sungguh alangkah baiknya, sungguh alangkah indahnya, bila saudara semua hidup rukun bersama." +
                            "Seperti minyak di kepala Harun yang ke janggut dan jubahnya turun." +
                            "Seperti embun yang dari Hermon mengalir ke bukit Sion.")
                    Spacer(modifier = Modifier.height(20.dp))
                }

                /* Displaying the daily morning devotion. */
                Row (modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Sapaan dan Renungan Pagi", fontWeight = FontWeight.Bold, fontSize = 26.sp)
                    // SOURCE: https://stackoverflow.com/a/69278397
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Some desc") }
                }
                LazyRow {
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                    // The actual content.
                    items (5) {
                        Card (
                            onClick = {
                                Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                                val destination = "abcd5dasar_$it"
                                screenController.navigate("${NavigationRoutes().SCREEN_VIDEO}/${destination}")
                                      },
                            modifier = Modifier.fillMaxHeight().width(300.dp).padding(horizontal = 5.dp),
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(R.drawable.sample_thumbnail_youtube),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Column (modifier = Modifier.padding(5.dp)) {
                                    Text("YouTube Video Title Wkwkwk :v", fontWeight = FontWeight.Bold)
                                    Text("14 Juli 2024")
                                }
                            }
                        }
                    }
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                }
                Spacer(modifier = Modifier.height(20.dp))

                /* Displaying the choir production videos. */
                Row (modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("GKI Salatiga Choir", fontWeight = FontWeight.Bold, fontSize = 26.sp)
                    // SOURCE: https://stackoverflow.com/a/69278397
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Some desc") }
                }
                LazyRow {
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                    // The actual content.
                    items (5) {
                        Card (
                            onClick = {
                                Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                                val destination = "abcd5dasar_$it"
                                screenController.navigate("${NavigationRoutes().SCREEN_VIDEO}/${destination}")
                                      },
                            modifier = Modifier.fillMaxHeight().width(300.dp).padding(horizontal = 5.dp),
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(R.drawable.sample_thumbnail_youtube_2),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Column (modifier = Modifier.padding(5.dp)) {
                                    Text("YouTube Video Title Wkwkwk :v", fontWeight = FontWeight.Bold)
                                    Text("14 Juli 2024")
                                }
                            }
                        }
                    }
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                }
                Spacer(modifier = Modifier.height(20.dp))

                /* Displaying the music videos produced by Komisi Musik dan Liturgi (KML). */
                Row (modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("KML Production", fontWeight = FontWeight.Bold, fontSize = 26.sp)
                    // SOURCE: https://stackoverflow.com/a/69278397
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Some desc") }
                }
                LazyRow {
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                    // The actual content.
                    items (5) {
                        Card (
                            onClick = {
                                Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show()
                                val destination = "abcd5dasar_$it"
                                screenController.navigate("${NavigationRoutes().SCREEN_VIDEO}/${destination}")
                                      },
                            modifier = Modifier.fillMaxHeight().width(300.dp).padding(horizontal = 5.dp),
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(R.drawable.sample_thumbnail_youtube_3),
                                    contentDescription = "Some name",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Column (modifier = Modifier.padding(5.dp)) {
                                    Text("YouTube Video Title Wkwkwk :v", fontWeight = FontWeight.Bold)
                                    Text("14 Juli 2024")
                                }
                            }
                        }
                    }
                    // Left-right padding.
                    item { Spacer(modifier = Modifier.width(5.dp)) }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Below spacer needs to be added a little bit more,
                // to mitigate bottom FAB.
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
     */
}
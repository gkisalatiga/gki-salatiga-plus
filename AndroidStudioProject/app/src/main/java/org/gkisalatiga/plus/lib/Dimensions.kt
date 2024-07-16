/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's dimension constants.
 * Somehow, Jetpack Composable cannot access integer values in dimens.xml
 * SOURCE: https://stackoverflow.com/a/73722401
 */

package org.gkisalatiga.plus.lib

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(val screenSize: String) {
    val top_bar_height_in_dp: Dp = 75.dp
}
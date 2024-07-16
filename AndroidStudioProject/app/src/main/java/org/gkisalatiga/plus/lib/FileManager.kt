/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's internal file and folder structure, reading, and writing.
 */

package org.gkisalatiga.plus.lib

import android.net.Uri

class FileManager {
    private val appPackageName: String = "org.salatiga.plus"

    /**
     * Returns the Uri path of a specific resource in the app.
     * SOURCE: https://stackoverflow.com/a/31217414
     */
    public fun getResourceUri(resId: Int): Uri {
        return Uri.parse("android.resource://$appPackageName/$resId")
    }
}
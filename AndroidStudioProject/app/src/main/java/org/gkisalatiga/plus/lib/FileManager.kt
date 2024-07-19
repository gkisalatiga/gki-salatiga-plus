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
import java.io.File
import java.io.FileInputStream

class FileManager {
    private val appPackageName: String = "org.salatiga.plus"

    /**
     * Returns the Uri path of a specific resource in the app.
     * SOURCE: https://stackoverflow.com/a/31217414
     */
    public fun getResourceUri(resId: Int): Uri {
        return Uri.parse("android.resource://$appPackageName/$resId")
    }

    /**
     * Reads a file in the private storage and convert the file's content as a string.
     * SOURCE: https://stackoverflow.com/a/45202002
     *
     * @param privateFilePath the private file's full path.
     * @return the file's content as string.
     */
    public fun readPrivateFileAsString(privateFilePath: String): String {
        val privateFileRead = File(privateFilePath)
        return FileInputStream(privateFileRead).bufferedReader().use { it.readText() }
    }
}
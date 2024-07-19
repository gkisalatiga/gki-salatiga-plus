/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 * This class gets around "network on main thread" error and allow for the downloading of files
 * from internet sources.
 */

package org.gkisalatiga.plus.lib

import android.content.Context
import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * Attempts to download an online data.
 * SOURCE: https://stackoverflow.com/a/53128216
 */
class Downloader() {

    /**
     * Downloads a file and save it as a private file in the app's private storage.
     * SOURCE: https://www.geeksforgeeks.org/how-to-load-any-image-from-url-without-using-any-dependency-in-android/
     *
     * @param streamURL the internet link to download.
     * @param saveFileAs the name of the file to save as in the app's private storage.
     * @return nothing. the downloaded file path is stored in "GlobalSchema.pathToDownloadedPrivateFile.value" upon successful download.
     */
    public fun asPrivateFile(streamURL: String, saveFileAs: String) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // The return status of the download process.
        var downloadStatus: Boolean = false

        // The absolute path of the downloaded private file.
        var downloadedAbsolutePath: String = ""

        // Fetching the data
        executor.execute {

            // Notify anyone that the download is ongoing.
            GlobalSchema.pathToDownloadedPrivateFile.value = ""
            GlobalSchema.isPrivateDownloadComplete.value = false

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(streamURL).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = (GlobalSchema.context).getDir("Downloads", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, saveFileAs)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                Log.d("Groaker", "Download successful into ${privateFile.absolutePath}")
                downloadedAbsolutePath = privateFile.absolutePath
                downloadStatus = true

            } catch (e: Exception) {
                Log.d("Groaker", "Error encountered during download: $e")
                downloadStatus = false
            }

            // Break free from this thread. Save download path.
            GlobalSchema.pathToDownloadedPrivateFile.value = downloadedAbsolutePath
            GlobalSchema.isPrivateDownloadComplete.value = true
            executor.shutdown()
        }
    }

    /**
     * Downloads and initiates the metadata JSON source file from the CDN.
     * This function will then assign the downloaded JSON path to the appropriate global variable.
     * Requires no argument and does not return any return value.
     */
    public fun initMetaData() {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Log.d("Groaker", "Attempting to download the JSON metadata file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GlobalSchema.JSONSource).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = (GlobalSchema.context).getDir("Downloads", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, GlobalSchema.JSONSavedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GlobalSchema.absolutePathToJSONMetaData = privateFile.absolutePath
                GlobalSchema.isJSONMetaDataInitialized.value = true

                Log.d("Groaker", "JSON metadata was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: Exception) {
                Log.d("Groaker", "Error encountered during download: $e")
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

}
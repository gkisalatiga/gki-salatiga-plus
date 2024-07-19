/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 * This class gets around "network on main thread" error and allow for the downloading of files
 * from internet sources.
 */

package org.gkisalatiga.plus.lib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import kotlinx.coroutines.GlobalScope
import org.gkisalatiga.plus.global.GlobalSchema
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.util.concurrent.Executors
import java.util.logging.Logger

/*class Downloader {
    public fun download(url: String, rememberReturnArray: MutableState<InputStream>): Downloader {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread
        val executor = Executors.newSingleThreadExecutor()

        // This is the main loop object to ensure that updates on each individual thread
        // can be shown to the user
        val handler = Handler(Looper.getMainLooper())

        // Fetching the data
        executor.execute {
            // The downloaded input stream
            var downloadedStream: InputStream

            // Try to get the file on the internet
            try {
                downloadedStream = URL(url).openStream()

                // Use the handler to make any change to the UI from the multithread
                handler.post {
                    rememberReturnArray.value = downloadedStream
                }
            } catch (e: Exception) {
                println("Error detected!")
            }
        }

        return this
    }
}*/

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

        // This is the main loop object to ensure that updates on each individual thread
        // can be shown to the user.
        val handler = Handler(Looper.getMainLooper())

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
                val fileCreator = (GlobalSchema.norender["context"] as Context).getDir("Downloads", Context.MODE_PRIVATE)
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

        // Use the handler to make any change to the UI from the multithread.
        /*handler.post {
            key(GlobalSchema.isPrivateDownloadComplete.value) {
                if (executor.isShutdown && downloadStatus) GlobalSchema.pathToDownloadedPrivateFile.value = downloadedAbsolutePath
                else GlobalSchema.pathToDownloadedPrivateFile.value = ""
            }
        }*/
    }
/*
    override fun doInBackground(vararg params: String?) {
        val url = params[0]

        mContext.get()?.let {
            try {
                var file = it.getDir("Images", Context.MODE_PRIVATE)
                file = File(file, "img.jpg")
                val out = FileOutputStream(file)
                out.flush()
                out.close()
                Log.i("Seiggailion", "Image saved.")
            } catch (e: Exception) {
                Log.i("Seiggailion", "Failed to save image.")
            }
        }
    }*/
}
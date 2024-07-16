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
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.util.concurrent.Executors
import java.util.logging.Logger

class Downloader {
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
}

/**
 * SOURCE: https://stackoverflow.com/a/53128216
 */
class DownloadAndSaveImageTask(context: Context) : AsyncTask<String, Unit, Unit>() {
    private var mContext: WeakReference<Context> = WeakReference(context)

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
    }
}
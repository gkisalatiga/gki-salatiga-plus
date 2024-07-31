/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 * This class allows to extract zip archives.
 */

package org.gkisalatiga.plus.lib

import android.content.Context
import android.util.Log
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.external.UnzipUtils
import java.io.File
import java.io.FileOutputStream
import java.net.UnknownHostException
import java.util.concurrent.Executors

class Extractor(private val ctx: Context) {

    /**
     * Stores and pre-initializes essential string values and variables related to the profile info.
     * This function assumes that the JSON schema has been initialized beforehand.
     */
    private fun initVariables() {

        // Reset the arrays, to prevent stacking multiple static menus.
        GlobalSchema.staticDataTitleArray.clear()
        GlobalSchema.staticDataJSONNodeArray.clear()
        GlobalSchema.staticDataIndexHTMLArray.clear()
        GlobalSchema.staticDataBannerArray.clear()

        val baseExtractedData = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
        val parentNode = AppDatabase().getMainData().getJSONObject("static")
        for (l in parentNode.keys()) {
            // Extract the static data's title list and node names.
            GlobalSchema.staticDataTitleArray.add(parentNode.getString(l))
            GlobalSchema.staticDataJSONNodeArray.add(l)

            // Determine the static data's banner image and index.html paths.
            // e.g.: /data/user/0/org.gkisalatiga.plus/app_Archive/static/40_pa_wilayah/index.html
            GlobalSchema.staticDataIndexHTMLArray.add("$baseExtractedData/static/$l/index.html")
            GlobalSchema.staticDataBannerArray.add("$baseExtractedData/static/$l/banner.webp")
        }
    }

    /**
     * Extracts the app's fallback static data, compressed into a zip file.
     * SOURCE: https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
     */
    public fun initFallbackStaticData() {

        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Log.d("Groaker", "[Extractor.initFallbackStaticData] Attempting to extract the static ZIP data ...")
        executor.execute {

            // Opening the zip file.
            val zipInputStream = ctx.resources.openRawResource(R.raw.fallback_static_data)

            // Convert InputStream to File.
            // SOURCE: https://www.perplexity.ai/search/what-is-the-recommended-way-to-hN7TmOn5TtS31c_ebAmLyA
            val savedFilename = GlobalSchema.staticDataSavedFilename
            val zipOutputFile = File(ctx.getFileStreamPath(savedFilename).absolutePath)
            val zipOutputStream = FileOutputStream(zipOutputFile)
            zipInputStream.copyTo(zipOutputStream)
            zipInputStream.close(); zipOutputStream.close()

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initFallbackStaticData] Saved zip path: ${ctx.getFileStreamPath(savedFilename).absolutePath}")

            // This is now the zip file as "File" object.
            val staticZipFile = zipOutputFile

            // Creating the private extractor folder.
            val extractFolder = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initFallbackStaticData] Extract zip folder: $extractFolder")

            // Extracting the zip file.
            UnzipUtils().unzip(staticZipFile, extractFolder)

            // Notify all the other functions about the unzipping of the static data.
            GlobalSchema.isStaticDataExtracted.value = true
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initFallbackStaticData] The static ZIP data has been successfully unzipped!")

            // Initialize the global variables.
            initVariables()

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Fetch the online static data and extarct them into the app's internal storage.
     * SOURCE: https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
     */
    public fun initStaticData() {

        // Reset the value.
        GlobalSchema.isStaticDataDownloaded.value = false
        GlobalSchema.isStaticDataExtracted.value = false

        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        executor.execute {

            Log.d("Groaker-Zip", "[Extractor.initStaticData] Downloading the static ZIP data ...")
            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GlobalSchema.staticDataSource).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = ctx.getDir("Archive", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, GlobalSchema.staticDataSavedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GlobalSchema.absolutePathToStaticData = privateFile.absolutePath
                GlobalSchema.isStaticDataDownloaded.value = true

                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] The online static data was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet = false
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] Network unreachable during download: $e")
            }

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] Attempting to extract the static ZIP data ...")

            // This is now the zip file as "File" object.
            val savedFilename = GlobalSchema.staticDataSavedFilename
            val staticZipFile = File(ctx.getFileStreamPath(savedFilename).absolutePath)
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] Saved zip path: ${ctx.getFileStreamPath(savedFilename).absolutePath}")

            // Creating the private extractor folder.
            val extractFolder = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] Extract zip folder: $extractFolder")

            // Extracting the zip file.
            UnzipUtils().unzip(staticZipFile, extractFolder)

            // Notify all the other functions about the unzipping of the static data.
            GlobalSchema.isStaticDataExtracted.value = true
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] The static ZIP data has been successfully unzipped!")

            // Initialize the global variables.
            initVariables()

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Initializes the location of the extraction of the static zip file.
     */
    public fun initExtractLocation(): String {
        val fileCreator = ctx.getDir("Archive", Context.MODE_PRIVATE)
        val extractPath = File(fileCreator, GlobalSchema.staticDataSavedFilename).absolutePath
        GlobalSchema.absolutePathToStaticData = extractPath

        // Initializing the extract variables pointing to the cached static files.
        initVariables()

        return extractPath
    }

}
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
    private fun initStaticVariables() {

        // Reset the arrays, to prevent stacking multiple static menus.
        GlobalSchema.staticDataTitleArray.clear()
        GlobalSchema.staticDataJSONNodeArray.clear()
        GlobalSchema.staticDataIndexHTMLArray.clear()
        GlobalSchema.staticDataBannerArray.clear()

        val baseExtractedData = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
        val parentNode = GlobalSchema.globalJSONObject!!.getJSONObject("static")
        for (l in parentNode.keys()) {
            // Extract the static data's node names.
            GlobalSchema.staticDataJSONNodeArray.add(l)
        }
        // Sorting.
        GlobalSchema.staticDataJSONNodeArray.sort()

        for (l in GlobalSchema.staticDataJSONNodeArray) {
            // Extract the static data's title list and node names.
            GlobalSchema.staticDataTitleArray.add(parentNode.getString(l))

            // Determine the static data's banner image and index.html paths.
            // e.g.: /data/user/0/org.gkisalatiga.plus/app_Archive/static/40_pa_wilayah/index.html
            GlobalSchema.staticDataIndexHTMLArray.add("$baseExtractedData/static/$l/index.html")
            GlobalSchema.staticDataBannerArray.add("$baseExtractedData/static/$l/banner.webp")
        }
    }

    /**
     * Stores and pre-initializes essential string values and variables related to the carousel banner.
     * This function assumes that the JSON schema has been initialized beforehand.
     */
    private fun initCarouselVariables() {

        // Reset the arrays, to prevent stacking multiple static menus.
        GlobalSchema.carouselBannerJSONNodeArray.clear()
        GlobalSchema.carouselBannerBannerArray.clear()
        GlobalSchema.carouselBannerTypeArray.clear()
        GlobalSchema.carouselBannerBaseFolderArray.clear()

        val baseExtractedData = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
        val parentNode = GlobalSchema.globalJSONObject!!.getJSONObject("carousel")
        for (l in parentNode.keys()) {
            // Extract the static data's node names.
            GlobalSchema.carouselBannerJSONNodeArray.add(l)
        }

        // Sorting by ascending alphanumerical order.
        // (Please don't turn this on; the banner order should be according to the JSON schema's key order)
        // GlobalSchema.carouselBannerJSONNodeArray.sort()

        for (l in GlobalSchema.carouselBannerJSONNodeArray) {
            val registeredBannerFile = parentNode.getJSONObject(l).getString("banner")

            // Determine the carousel's image path.
            // e.g.: /data/user/0/org.gkisalatiga.plus/app_Archive/carousel/00_komisi_anak/banner.jpeg
            val carouselImagePath = "$baseExtractedData/carousel/$l/$registeredBannerFile"
            GlobalSchema.carouselBannerBannerArray.add(carouselImagePath)
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Added new carousel image path: $carouselImagePath")

            // Determine the carousel's banner type.
            val bannerType = parentNode.getJSONObject(l).getString("type")
            GlobalSchema.carouselBannerTypeArray.add(bannerType)

            // Append the base folder.
            val baseFolder = "$baseExtractedData/carousel/$l"
            GlobalSchema.carouselBannerBaseFolderArray.add(baseFolder)
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
            initStaticVariables()

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Extracts the app's fallback carousel banner, compressed into a zip file.
     * SOURCE: https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
     */
    public fun initFallbackCarouselBanner() {

        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Log.d("Groaker-Carousel", "[Extractor.initFallbackCarouselBanner] Attempting to extract the static ZIP data ...")
        executor.execute {

            // Opening the zip file.
            val zipInputStream = ctx.resources.openRawResource(R.raw.fallback_carousel_banner)

            // Convert InputStream to File.
            // SOURCE: https://www.perplexity.ai/search/what-is-the-recommended-way-to-hN7TmOn5TtS31c_ebAmLyA
            val savedFilename = GlobalSchema.carouselBannerSavedFilename
            val zipOutputFile = File(ctx.getFileStreamPath(savedFilename).absolutePath)
            val zipOutputStream = FileOutputStream(zipOutputFile)
            zipInputStream.copyTo(zipOutputStream)
            zipInputStream.close(); zipOutputStream.close()

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Carousel", "[Extractor.initFallbackCarouselBanner] Saved zip path: ${ctx.getFileStreamPath(savedFilename).absolutePath}")

            // This is now the zip file as "File" object.
            val carouselZipFile = zipOutputFile

            // Creating the private extractor folder.
            val extractFolder = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Carousel", "[Extractor.initFallbackCarouselBanner] Extract zip folder: $extractFolder")

            // Extracting the zip file.
            UnzipUtils().unzip(carouselZipFile, extractFolder)

            // Notify all the other functions about the unzipping of the static data.
            GlobalSchema.isCarouselBannerExtracted.value = true
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Carousel", "[Extractor.initFallbackCarouselBanner] The static ZIP data has been successfully unzipped!")

            // Initialize the global variables.
            initCarouselVariables()

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
                initStaticVariables()

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet = false
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initStaticData] Network unreachable during download: $e")

                // Use cached data.
                initStaticExtractLocation()
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Fetch the online static data and extarct them into the app's internal storage.
     * SOURCE: https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
     */
    public fun initCarouselData() {

        // Reset the value.
        GlobalSchema.isCarouselBannerDownloaded.value = false
        GlobalSchema.isCarouselBannerExtracted.value = false

        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        executor.execute {

            Log.d("Groaker-Zip", "[Extractor.initCarouselData] Downloading the carousel ZIP data ...")
            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GlobalSchema.carouselBannerSource).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = ctx.getDir("Archive", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, GlobalSchema.carouselBannerSavedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GlobalSchema.absolutePathToCarouselBanner = privateFile.absolutePath
                GlobalSchema.isCarouselBannerDownloaded.value = true

                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initCarouselData] The online carousel banner ZIP archive was successfully downloaded into: ${privateFile.absolutePath}")

                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initCarouselData] Attempting to extract the carousel ZIP data ...")

                // This is now the zip file as "File" object.
                val savedFilename = GlobalSchema.carouselBannerSavedFilename
                val carouselZipFile = File(fileCreator, GlobalSchema.carouselBannerSavedFilename)
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initCarouselData] Saved zip path: ${ctx.getFileStreamPath(savedFilename).absolutePath}")

                // Creating the private extractor folder.
                val extractFolder = ctx.getDir("Archive", Context.MODE_PRIVATE).absolutePath
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initCarouselData] Extract zip folder: $extractFolder")

                // Extracting the zip file.
                UnzipUtils().unzip(carouselZipFile, extractFolder)

                // Notify all the other functions about the unzipping of the static data.
                GlobalSchema.isCarouselBannerExtracted.value = true
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initCarouselData] The carousel ZIP data has been successfully unzipped!")

                // Initialize the global variables.
                initCarouselVariables()

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet = false
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[Extractor.initCarouselData] Network unreachable during download: $e")

                // Use cached data.
                initCarouselExtractLocation()
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Initializes the location of the extraction of the static zip file
     * in order to access the cached data.
     */
    public fun initStaticExtractLocation(): String {
        val fileCreator = ctx.getDir("Archive", Context.MODE_PRIVATE)
        val extractPath = File(fileCreator, GlobalSchema.staticDataSavedFilename).absolutePath
        GlobalSchema.absolutePathToStaticData = extractPath

        // Initializing the extract variables pointing to the cached static files.
        initStaticVariables()

        return extractPath
    }

    /**
     * Initializes the location of the extraction of the static zip file
     * in order to access the cached data.
     */
    public fun initCarouselExtractLocation(): String {
        val fileCreator = ctx.getDir("Archive", Context.MODE_PRIVATE)
        val extractPath = File(fileCreator, GlobalSchema.carouselBannerSavedFilename).absolutePath
        GlobalSchema.absolutePathToCarouselBanner = extractPath

        // Initializing the extract variables pointing to the cached static files.
        initCarouselVariables()

        return extractPath
    }

}
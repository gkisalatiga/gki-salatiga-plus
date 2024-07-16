/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's database and information retrieval, whether stored in the APK
 * or downloaded online.
 */

package org.gkisalatiga.plus.lib

import android.content.Context
import org.gkisalatiga.plus.R
import org.json.JSONObject
import java.io.InputStream

class AppDatabase {

    private var _JSONString: String = ""

    /**
     * Loads the debug JSON file for testing with the app's database dynamics.
     * SOURCE: https://stackoverflow.com/a/2856501
     * SOURCE: https://stackoverflow.com/a/39500046
     */
    public fun loadRaw(context: Context): AppDatabase {
        val input: InputStream = context.resources.openRawResource(R.raw.debug_schema_sample)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Variable assignments
        this._JSONString = inputAsString

        // Return this class so that it can be attached with other functions
        return this
    }

    /**
     * Experimental; please only use this function in debug mode.
     */
    public fun getRawDumped(): String {
        return this._JSONString
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     */
    public fun getMainData(): JSONObject {
        return JSONObject(_JSONString).getJSONObject("main-data")
    }
}
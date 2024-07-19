/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * Implements a global declaration of variables, which can be accessed across classes.
 * SOURCE: https://tutorial.eyehunts.com/android/declare-android-global-variable-kotlin-example/
 * SOURCE: https://stackoverflow.com/a/52844621
 */

package org.gkisalatiga.plus.abstract

import android.app.Application

class GlobalClass : Application() {
    companion object {
        var globalVar = "Utopia"
    }

    override fun onCreate() {
        super.onCreate()
    }
}
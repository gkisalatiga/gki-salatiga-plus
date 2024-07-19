/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Cleans and reset a given app's schema's parameter route to avoid conflict.
 */

package org.gkisalatiga.plus.lib

import org.gkisalatiga.plus.global.GlobalSchema

class ResetSchema {
    /**
     * Assumes "param" is mapped to a string.
     *
     * @param schema the schema variable to deal with.
     * @param param the parameter that will be reset.
     */
    public fun reset(param: String) {
        // GlobalSchema.schema.value[param] = ""
    }
}
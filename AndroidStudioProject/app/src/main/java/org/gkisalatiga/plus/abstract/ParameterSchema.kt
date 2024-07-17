/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 * This class is the standardized schema for determining public function parameters
 * and passing payload between functions or methods.
 * This schema is especially useful for mutable objects and variables.
 * The class structure is adapted from the following,
 * SOURCE: https://stackoverflow.com/a/66178263
 * SOURCE: https://stackoverflow.com/a/69411013
 * SOURCE: https://developer.android.com/develop/ui/compose/performance/stability
 */

package org.gkisalatiga.plus.abstract

/**
 * @author github.com/groaking
 * @param title the title that describes this payload
 * @param url any arbitrary internet URL (whether in HTTP, HTTPS, FTP, or even Android Asset protocol)
 */
data class ParameterSchema (
    var title: String,
    var url: String,
)
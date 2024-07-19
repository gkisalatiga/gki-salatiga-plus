package org.gkisalatiga.plus.splashscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import org.gkisalatiga.plus.splashscreen.ui.theme.GKISalatigaPlusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable on-the-fly edit of drawable SVG vectors.
        // SOURCE: https://stackoverflow.com/a/38418049
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Preamble logging to the terminal.
        Log.d("Groaker", "Starting app: GKI Salatiga+")

        super.onCreate(savedInstanceState)
        setContent {
            GKISalatigaPlusTheme {
                Text("Hello World")
            }
        }
    }
}
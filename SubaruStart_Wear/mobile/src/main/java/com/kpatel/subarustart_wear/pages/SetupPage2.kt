package com.kpatel.subarustart_wear.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.Uri.fromParts
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kpatel.subarustart_wear.DataStoreRepo
import kotlinx.coroutines.runBlocking


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetupScreen2(
    datastore: DataStoreRepo,
    context: Context,
    navController: NavController,
    onSubmitNavRoute: () -> Unit
) {
    val safeContext = LocalContext.current

    var askedForeground by remember { mutableStateOf(false) }
    var shouldAskBackground by remember { mutableStateOf(false) }
    var openWeatherAPI by remember { mutableStateOf("") }

    val backgroundPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    val foregroundPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // Step 2: Background location launcher
    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(safeContext, "Background location granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(safeContext, "Background location denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Step 1: Foreground location launcher
    val foregroundLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            // Foreground granted → now launch background location (only on Android 10!)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                backgroundLocationLauncher.launch(backgroundPermission)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ requires settings redirect
                Toast.makeText(
                    safeContext,
                    "Go to Settings to enable background location",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = fromParts("package", safeContext.packageName, null)
                }
                safeContext.startActivity(intent)
            }
        } else {
            Toast.makeText(safeContext, "Foreground permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    // Automatically request on composition
    LaunchedEffect(Unit) {
        if (!askedForeground) {
            askedForeground = true
            foregroundLocationLauncher.launch(foregroundPermissions)
        }
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize(), // Fill the screen
            contentAlignment = Alignment.Center // Center content inside the Box
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    OutlinedTextField(
                        value = openWeatherAPI,
                        onValueChange = { openWeatherAPI = it },
                        label = { Text("OpenWeatherAPI Key") }
                    )
                }
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(onClick = { runBlocking { datastore.setOpenWeatherAPIKey(openWeatherAPI) } }) {
                    Text("Submit API Key")
                }
            }
        }
    }
}









@Preview(showBackground = true)
@Composable
fun SetupPreview2() {

}
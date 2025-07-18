package com.kpatel.subarustart_wear.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
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
    val uriHandler = LocalUriHandler.current
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {

        val str = "You need an API key to pull weather info. " +
                "If you need information on how to obtain an API Key, click here"
        val startIndex = str.indexOf("click here")
        val endIndex = startIndex + 10
        append(str)
        addStyle( style = SpanStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            textDecoration = TextDecoration.None
        ), start = 0, end = str.length)
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = endIndex
        )

        // attach a string annotation that stores a URL to the text "link"
        addStringAnnotation(
            tag = "URL",
            annotation = "https://home.openweathermap.org/api_keys",
            start = startIndex,
            end = endIndex
        )

    }
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
                    "Go to Settings to enable background location at all times",
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
                    Spacer(modifier = Modifier.size(45.dp))
                    ClickableText(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        text = annotatedLinkString,
                        onClick = {
                            annotatedLinkString
                                .getStringAnnotations("URL", it, it)
                                .firstOrNull()?.let { stringAnnotation ->
                                    uriHandler.openUri(stringAnnotation.item)
                                }
                        }
                    )
                }
                Row {
                    OutlinedTextField(
                        value = openWeatherAPI,
                        onValueChange = { openWeatherAPI = it },
                        label = { Text("OpenWeatherAPI Key") }
                    )
                }
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(onClick = { runBlocking { datastore.setOpenWeatherAPIKey(openWeatherAPI) }
                    onSubmitNavRoute();
                }) {
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
package com.kpatel.subarustart_wear.pages

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kpatel.subarustart_wear.DataStoreRepo
import com.kpatel.subarustart_wear.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking


fun isValidDeviceID(input: String): Boolean {
    return input.isNotEmpty() && input.length == 13 && input.all { it.isDigit() }
}

fun isValidVehicleKey(input: String?): Boolean {
    return !input.isNullOrEmpty() && input.length == 7 && input.all { it.isDigit() }
}
fun isValidPin(input: String?): Boolean {
    return !input.isNullOrEmpty() && input.length == 4 && input.all { it.isDigit() }
}

fun validateInputs(
    username: String,
    password: String,
    deviceID: String,
    vehicleKey: String,
    pin: String
): Boolean {
    val usernameRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    val correctUsername = username.matches(usernameRegex.toRegex())
    val goodPass = password.isNotEmpty()
    val validDeviceId = isValidDeviceID(deviceID)
    val isValidVehicleKey = isValidVehicleKey(vehicleKey)
    val isValidPin = isValidPin(pin)
    return (correctUsername && validDeviceId && goodPass  && isValidVehicleKey && isValidPin)
}


@OptIn(DelicateCoroutinesApi::class)
fun submitData(
    onSubmitNavRoute: () -> Unit, username: String, password: String, vehicleKey: String,
    deviceID: String,
    datastore: DataStoreRepo,
    context: Context,
    pin: String,
    controller: NavController
){

    if(validateInputs(username, password, deviceID, vehicleKey, pin)) {
        runBlocking {
            datastore.setFirstSetup(false)
            datastore.setUsername(username)
            datastore.setPassword(password)
            datastore.setVehicleKey(vehicleKey)
            datastore.setDeviceID(deviceID)
            datastore.setPin(pin)
        }
        controller.popBackStack()
        onSubmitNavRoute()
    }else{
        Toast.makeText(context, "Some fields are entered incorrectly", Toast.LENGTH_LONG).show()
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetupScreen(datastore: DataStoreRepo, context: Context, navController: NavController, onSubmitNavRoute: () -> Unit){
    var username by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var vehicleKey by remember { mutableStateOf("")}
    var deviceID by remember { mutableStateOf("")}
    var pin by remember { mutableStateOf("")}
    val uriHandler = LocalUriHandler.current
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {

        val str = "If you need information on how to obtain the device ID and vehicle key, click here"
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
            annotation = "https://github.com/krishnapatel02/BetterSubaruRemoteStart?tab=readme-ov-file#to-obtain-deviceid-and-vehicle-key-to-setup-login",
            start = startIndex,
            end = endIndex
        )

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(

        ) {
            Column(modifier = Modifier
                .padding(it)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                ){
                val focusManager = LocalFocusManager.current
                Column {
                    Row( verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info",
                            Modifier
                                .size(50.dp)
                                .padding())
                        Text(text = stringResource(id = R.string.setup_info), textAlign = TextAlign.Left, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, start = 10.dp)
                        )



                    }
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

                }


                Spacer(modifier = Modifier.padding(top = 20.dp))
                Column(modifier = Modifier.width(with(LocalDensity.current) { 250.dp },),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(value = username,
                        onValueChange = { username = it },
                        placeholder = { Text(stringResource(id = R.string.username_placeholder)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text
                        ), keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Next)
                        }, singleLine = true, label = {Text("Username")})
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    OutlinedTextField(value = password, onValueChange = {password = it},
                        placeholder = { Text(stringResource(id = R.string.password_placeholder)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text
                        ),singleLine = true,keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Next)
                        }, label = {Text("Password")})
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    OutlinedTextField(value = vehicleKey, onValueChange = {vehicleKey = it},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(id = R.string.vehicle_key_placeholder)) },singleLine = true,keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Next)
                        }, label = {Text("Vehicle Key")}

                    )
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    OutlinedTextField(value = deviceID, onValueChange = {deviceID = it},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(id = R.string.deviceid_placeholder)) },
                        singleLine = true,keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Next)
                        }, label = {Text("Device ID")}
                    )
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    OutlinedTextField(value = pin, onValueChange = {pin = it},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(id = R.string.pin_placeholder)) },
                        singleLine = true,keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                        }, label = {Text("Pin")}
                    )
                    Spacer(modifier = Modifier.padding(top = 25.dp))
                }


                Button(onClick = {submitData(onSubmitNavRoute, username, password,
                    vehicleKey, deviceID, datastore, context, pin, navController)}) {
                    Text(text = "Submit")
                }

            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun SetupPreview() {

}
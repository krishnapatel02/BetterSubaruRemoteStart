package com.kpatel.subarustart_wear.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kpatel.subarustart_wear.DataStoreSingleton
import kotlinx.coroutines.runBlocking


fun onSubmitCardChanges(navController: NavHostController, temp: Int, engineRuntime: Int, ventSetting: String, rearDefrosterSetting: Boolean, airRecirc: String,
                        presetName: String, cardNumber: Int
){
    val dataStoreRepo = DataStoreSingleton.getDataStoreRepo()
    runBlocking {
        dataStoreRepo.setCardTemp(cardNumber, temp)
        dataStoreRepo.setCardRuntime(cardNumber, engineRuntime)
        dataStoreRepo.setCardRearDefrost(cardNumber, rearDefrosterSetting)
        dataStoreRepo.setCardVentSetting(cardNumber, ventSetting)
        dataStoreRepo.setCardAirCirc(cardNumber, airRecirc)
        dataStoreRepo.setNameCard(cardNumber, presetName)
    }
    navController.navigateUp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePresetScreen(cardNumber: Int, applicationContext: Context, navController: NavHostController) {
    val dataStore = DataStoreSingleton.getDataStoreRepo()
    var temperature by remember { mutableIntStateOf(runBlocking { dataStore.getCardTemp(cardNumber) }) }
    var presetName by remember {
        mutableStateOf(runBlocking{dataStore.getNameCard(cardNumber)})
    }
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    var runtime_expanded by remember { mutableStateOf(false) }
    var engineRuntime by remember { mutableIntStateOf(runBlocking { dataStore.getCardRuntime(cardNumber) }) }
    var defrost_expanded by remember { mutableStateOf(false) }
    var air_recirc by remember { mutableStateOf(runBlocking { dataStore.getCardAirCirc(cardNumber) })}
    var air_recirc_exanded by remember { mutableStateOf(false  )}
    var ventSetting by remember {
        mutableStateOf(runBlocking { dataStore.getCardVentSetting(cardNumber) })
    }
    var rearDefrostSetting by remember {
        mutableStateOf(runBlocking { dataStore.getCardRearDefrost(cardNumber) })
    }
    var rearDefrostSettingTextValue = rememberUpdatedState(if (rearDefrostSetting) "On" else "Off")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = presetName,
            onValueChange = { presetName = it },
            label = { Text("Preset Name") },
            placeholder = { Text(presetName) },
            singleLine = true,keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            }
        )

    Spacer(modifier = Modifier.size(20.dp))
    Text(text = "Interior Temperature")
    Spacer(modifier = Modifier.size(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(onClick = {
            if (temperature > 60) {
                temperature--
            } else {
                Toast.makeText(
                    applicationContext,
                    "Temperature cannot be less than 60 ℉",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = "Decrease Temp")
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "$temperature \u2109", fontSize = 30.sp)
        Spacer(modifier = Modifier.size(10.dp))
        OutlinedButton(onClick = {
            if (temperature < 85) {
                temperature++
            } else {
                Toast.makeText(
                    applicationContext,
                    "Temperature cannot be more than 85 ℉",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = "Increase Temp")
        }
    }
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Vent Setting")
        Spacer(modifier = Modifier.size(10.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = it}) {
            OutlinedTextField(value = ventSetting, onValueChange = {}, readOnly = true, trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }, colors = ExposedDropdownMenuDefaults.textFieldColors(), modifier = Modifier.menuAnchor())
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Front Defroster") }, onClick = {
                    ventSetting="WINDOW"
                    expanded = false})
                DropdownMenuItem(text = { Text("Front Facing") }, onClick = {
                    ventSetting="FACE"
                    expanded = false})
                DropdownMenuItem(text = { Text("Front Facing & Foot") }, onClick = {
                    ventSetting="FEET_FACE_BALANCED"
                    expanded = false})
                DropdownMenuItem(text = { Text("Foot") }, onClick = {
                    ventSetting="FEET"
                    expanded = false})
                DropdownMenuItem(text = { Text("Foot & Front Defroster") }, onClick = {
                    ventSetting="FEET_WINDOW"
                    expanded = false})
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Rear Defroster")
        Spacer(modifier = Modifier.size(10.dp))
        ExposedDropdownMenuBox(expanded = defrost_expanded, onExpandedChange = {defrost_expanded = it}) {
            OutlinedTextField(value = rearDefrostSettingTextValue.value , onValueChange = {}, readOnly = true, trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = defrost_expanded)
            }, colors = ExposedDropdownMenuDefaults.textFieldColors(), modifier = Modifier.menuAnchor())
            ExposedDropdownMenu(expanded = defrost_expanded, onDismissRequest = { defrost_expanded = false }) {
                DropdownMenuItem(text = { Text("On") }, onClick = {
                    rearDefrostSetting = true
                    defrost_expanded = false})
                DropdownMenuItem(text = { Text("Off") }, onClick = {
                    rearDefrostSetting = false
                    defrost_expanded = false})
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Engine Runtime")
        Spacer(modifier = Modifier.size(10.dp))
        ExposedDropdownMenuBox(expanded = runtime_expanded, onExpandedChange = {runtime_expanded = it}) {
            OutlinedTextField(value = "$engineRuntime minutes", onValueChange = {}, readOnly = true, trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = runtime_expanded)
            }, colors = ExposedDropdownMenuDefaults.textFieldColors(), modifier = Modifier.menuAnchor())
            ExposedDropdownMenu(expanded = runtime_expanded, onDismissRequest = { runtime_expanded = false }) {
                DropdownMenuItem(text = { Text("5 minutes") }, onClick = {
                    engineRuntime = 5
                    runtime_expanded = false})
                DropdownMenuItem(text = {Text("10 minutes")}, onClick = {
                    engineRuntime = 10
                    runtime_expanded = false})
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        ExposedDropdownMenuBox(expanded = air_recirc_exanded, onExpandedChange = {air_recirc_exanded = it}) {
            OutlinedTextField(value = air_recirc, onValueChange = {}, readOnly = true, trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = runtime_expanded)
            }, colors = ExposedDropdownMenuDefaults.textFieldColors(), modifier = Modifier.menuAnchor())
            ExposedDropdownMenu(expanded = air_recirc_exanded, onDismissRequest = { air_recirc_exanded = false }) {
                DropdownMenuItem(text = { Text("Recirculation") }, onClick = {
                    air_recirc = "recirculation"
                    air_recirc_exanded = false})
                DropdownMenuItem(text = {Text("Outside Air")}, onClick = {
                    air_recirc = "outsideAir"
                    air_recirc_exanded = false})
            }
        }
        Spacer(modifier = Modifier.size(40.dp))
        OutlinedButton(onClick = {
            onSubmitCardChanges(navController, temperature, engineRuntime, ventSetting, rearDefrostSetting, air_recirc, presetName, cardNumber)

        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Submit Changes", fontSize = 15.sp)
                Spacer(modifier = Modifier.size(10.dp))
                Icon(Icons.Outlined.Check, contentDescription = "Submit")
            }
        }
    }
}



@Composable
@Preview
fun previewScreen(){
    UpdatePresetScreen(cardNumber = 1, LocalContext.current, NavHostController(LocalContext.current))
}
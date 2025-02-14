package com.kpatel.subarustart_wear.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kpatel.subarustart_wear.DataStoreRepo
import com.kpatel.subarustart_wear.R
import kotlinx.coroutines.runBlocking



fun onSubmitWearChanges(navController: NavHostController, temp: Int, engineRuntime: Int, ventSetting: String, rearDefrosterSetting: Boolean, airRecirc: String,
                        dataStoreRepo: DataStoreRepo){
    runBlocking {
        dataStoreRepo.setWearTemp(temp)
        dataStoreRepo.setWearRuntime(engineRuntime)
        dataStoreRepo.setWearRearDefroster(rearDefrosterSetting)
        dataStoreRepo.setWearVentSetting(ventSetting)
        dataStoreRepo.setWearAirRecirc(airRecirc)
    }
    navController.navigateUp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateWearScreen(applicationContext: Context, navController: NavHostController, dataStoreRepo: DataStoreRepo) {
    var expanded by remember { mutableStateOf(false) }
    var temperature by remember { mutableIntStateOf(runBlocking { dataStoreRepo.getWearTemp() }) }
    var runtime_expanded by remember { mutableStateOf(false) }
    var engineRuntime by remember { mutableIntStateOf(runBlocking { dataStoreRepo.getWearRuntime() }) }
    var defrost_expanded by remember { mutableStateOf(false) }
    var air_recirc by remember { mutableStateOf(runBlocking { dataStoreRepo.getWearAirRecirc() })}
    var air_recirc_exanded by remember { mutableStateOf(false  )}
    var ventSetting by remember {
        mutableStateOf(runBlocking { dataStoreRepo.getWearVentSetting() })
    }
    var rearDefrostSetting by remember {
        mutableStateOf(runBlocking{dataStoreRepo.getWearRearDefroster()})
    }
    var rearDefrostSettingTextValue = rememberUpdatedState(if (rearDefrostSetting) "On" else "Off")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row( verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info",
                Modifier
                    .size(50.dp)
                    .padding())
            Text(text = stringResource(id = R.string.wear_setup_info), textAlign = TextAlign.Left, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, start = 10.dp)
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Vent Setting")
        Spacer(modifier = Modifier.size(10.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = it}) {
            TextField(value = ventSetting, onValueChange = {}, readOnly = true, trailingIcon = {
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
            TextField(value = rearDefrostSettingTextValue.value , onValueChange = {}, readOnly = true, trailingIcon = {
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
        Text(text = "Interior Temperature")
        Spacer(modifier = Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
            OutlinedButton(onClick = {
                if(temperature > 60){
                    temperature --
                }else{
                    Toast.makeText(applicationContext, "Temperature cannot be less than 60 ℉", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = "Decrease Temp")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "$temperature \u2109", fontSize = 30.sp)
            Spacer(modifier = Modifier.size(10.dp))
            OutlinedButton(onClick = {
                if(temperature < 85){
                    temperature ++
                }else{
                    Toast.makeText(applicationContext, "Temperature cannot be more than 85 ℉", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = "Increase Temp")
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Engine Runtime")
        Spacer(modifier = Modifier.size(10.dp))
        ExposedDropdownMenuBox(expanded = runtime_expanded, onExpandedChange = {runtime_expanded = it}) {
            TextField(value = "$engineRuntime minutes", onValueChange = {}, readOnly = true, trailingIcon = {
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
            TextField(value = air_recirc, onValueChange = {}, readOnly = true, trailingIcon = {
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
        Button(onClick = { onSubmitWearChanges(navController, temperature, engineRuntime, ventSetting, rearDefrostSetting, air_recirc,  dataStoreRepo) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Submit Changes", fontSize = 15.sp)
                Spacer(modifier = Modifier.size(10.dp))
                Icon(Icons.Outlined.Check, contentDescription = "Submit")
            }
        }
    }
}



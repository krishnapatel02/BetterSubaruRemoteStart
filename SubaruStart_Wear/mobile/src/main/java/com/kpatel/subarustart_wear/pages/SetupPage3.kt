package com.kpatel.subarustart_wear.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kpatel.subarustart_wear.DataStoreRepo
import com.kpatel.subarustart_wear.R
import com.kpatel.subarustart_wear.TempSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun SetupScreen3(
    datastore: DataStoreRepo,
    context: Context = LocalContext.current,
    navController: NavController,
    onSubmitNavRoute: (() -> Unit)? = null
) {
    val options = listOf("<40°F", "40–60°F", "60–70°F", "70–80°F", "80°F+")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val tempSettingsMap = remember { mutableStateMapOf<String, TempSettings>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, label ->
                FilterChip(
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    label = { Text(label, fontSize = 12.sp) },
                    selected = pagerState.currentPage == index,
                    leadingIcon = if (pagerState.currentPage == index) {
                        {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Selected",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            count = options.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val label = options[page]
            TemperatureSettingsContent(
                label = label,
                navController = navController,
                dataStoreRepo = datastore,
                onTempSettingsChanged = { updatedSettings ->
                    tempSettingsMap[label] = updatedSettings
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Text("Back")
            }

            if (pagerState.currentPage == options.lastIndex) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            tempSettingsMap.forEach { (label, setting) ->
                                datastore.saveTempSettings(label, setting)
                            }
                            datastore.setFirstSetup(true)
                            onSubmitNavRoute?.invoke()
                        }
                    }
                ) {
                    Text("Submit")
                }
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(options.lastIndex))
                        }
                    },
                    enabled = pagerState.currentPage < options.lastIndex
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureSettingsContent(
    label: String,
    navController: NavController,
    dataStoreRepo: DataStoreRepo,
    onTempSettingsChanged: (TempSettings) -> Unit
) {
    val applicationContext = LocalContext.current
    var tempSettings by remember {
        mutableStateOf(runBlocking { dataStoreRepo.getTempSettings(label) })
    }

    var expanded by remember { mutableStateOf(false) }
    var defrost_expanded by remember { mutableStateOf(false) }
    var runtime_expanded by remember { mutableStateOf(false) }
    var air_recirc_expanded by remember { mutableStateOf(false) }

    var temperature by remember { mutableIntStateOf(tempSettings.interiorTemp) }
    var engineRuntime by remember { mutableIntStateOf(tempSettings.engineRuntime) }
    var air_recirc by remember { mutableStateOf(tempSettings.airRecirculation) }
    var ventSetting by remember { mutableStateOf(tempSettings.ventSetting) }
    var rearDefrostSetting by remember { mutableStateOf(tempSettings.rearDefrost) }

    val rearDefrostSettingText = if (rearDefrostSetting) "On" else "Off"

    fun updateTempSettings() {
        tempSettings = tempSettings.toBuilder()
            .setInteriorTemp(temperature)
            .setEngineRuntime(engineRuntime)
            .setAirRecirculation(air_recirc)
            .setVentSetting(ventSetting)
            .setRearDefrost(rearDefrostSetting)
            .build()
        onTempSettingsChanged(tempSettings)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                modifier = Modifier.size(50.dp).padding()
            )
            Text(
                text = stringResource(id = R.string.wear_setup_info),
                textAlign = TextAlign.Left,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, start = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Vent Setting")
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            TextField(
                value = ventSetting,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf(
                    "WINDOW" to "Front Defroster",
                    "FACE" to "Front Facing",
                    "FEET_FACE_BALANCED" to "Front Facing & Foot",
                    "FEET" to "Foot",
                    "FEET_WINDOW" to "Foot & Front Defroster"
                ).forEach { (value, label) ->
                    DropdownMenuItem(text = { Text(label) }, onClick = {
                        ventSetting = value
                        expanded = false
                        updateTempSettings()
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Rear Defroster")
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = defrost_expanded, onExpandedChange = { defrost_expanded = it }) {
            TextField(
                value = rearDefrostSettingText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(defrost_expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = defrost_expanded, onDismissRequest = { defrost_expanded = false }) {
                DropdownMenuItem(text = { Text("On") }, onClick = {
                    rearDefrostSetting = true
                    defrost_expanded = false
                    updateTempSettings()
                })
                DropdownMenuItem(text = { Text("Off") }, onClick = {
                    rearDefrostSetting = false
                    defrost_expanded = false
                    updateTempSettings()
                })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Interior Temperature")
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {
                if (temperature > 60) {
                    temperature--
                    updateTempSettings()
                } else {
                    Toast.makeText(applicationContext, "Temperature cannot be less than 60 ℉", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = "Decrease Temp")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("$temperature ℉", fontSize = 30.sp)
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedButton(onClick = {
                if (temperature < 85) {
                    temperature++
                    updateTempSettings()
                } else {
                    Toast.makeText(applicationContext, "Temperature cannot be more than 85 ℉", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = "Increase Temp")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Engine Runtime")
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = runtime_expanded, onExpandedChange = { runtime_expanded = it }) {
            TextField(
                value = "$engineRuntime minutes",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(runtime_expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = runtime_expanded, onDismissRequest = { runtime_expanded = false }) {
                listOf(5, 10).forEach { time ->
                    DropdownMenuItem(text = { Text("$time minutes") }, onClick = {
                        engineRuntime = time
                        runtime_expanded = false
                        updateTempSettings()
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Air Recirculation")
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = air_recirc_expanded, onExpandedChange = { air_recirc_expanded = it }) {
            TextField(
                value = air_recirc,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(air_recirc_expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = air_recirc_expanded, onDismissRequest = { air_recirc_expanded = false }) {
                listOf("recirculation" to "Recirculation", "outsideAir" to "Outside Air").forEach { (value, label) ->
                    DropdownMenuItem(text = { Text(label) }, onClick = {
                        air_recirc = value
                        air_recirc_expanded = false
                        updateTempSettings()
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SetupPreview3() {
    // Preview not functional due to missing NavController/DataStore
}

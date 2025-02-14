package com.kpatel.subarustart_wear

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.kpatel.subarustart_wear.presentation.theme.SubaruStart_WearTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun MainApp(
    onQueryOtherDevicesClicked: () -> Unit,
    lockCar: () -> Unit,
    unlockCar: () -> Unit,
    startEngine: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var lockEnabled by remember { mutableStateOf(true) }
    var unlockEnabled by remember { mutableStateOf(true) }
    var startEngineEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(lockEnabled) {
        if (lockEnabled) return@LaunchedEffect
        else delay(20000)
        lockEnabled = true
    }
    LaunchedEffect(unlockEnabled) {
        if (unlockEnabled) return@LaunchedEffect
        else delay(20000)
        unlockEnabled = true
    }
    LaunchedEffect(startEngineEnabled) {
        if (startEngineEnabled) return@LaunchedEffect
        else delay(20000)
        startEngineEnabled = true
    }
    SubaruStart_WearTheme {
        val focusRequester = remember { FocusRequester() }
        val scrollState = rememberScalingLazyListState()
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            timeText = { TimeText() }, modifier = Modifier.background(Color.Black)
        ) {
            ScalingLazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 32.dp
            ),  modifier = Modifier.onRotaryScrollEvent {
                coroutineScope.launch {
                    scrollState.scrollBy(it.verticalScrollPixels)
                }
                true // it means that we are handling the event with this callback
            }
                .focusRequester(focusRequester)
                .focusable(),
                state = scrollState,
            ) {
                item {
                    Button(onClick = {
                        startEngine()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        startEngineEnabled = false
                                     }, modifier = Modifier.fillMaxWidth(), enabled = startEngineEnabled) {
                        Text(text = "Start Engine")
                    }
                }

                item {
                    Button(onClick = {lockCar()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        lockEnabled = false
                    }, modifier = Modifier.fillMaxWidth(), enabled = lockEnabled) {
                        Text(text = "Lock All Doors")
                    }
                }
                item {
                    Button(onClick = { unlockCar()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        unlockEnabled = false
                    }, modifier = Modifier.fillMaxWidth(), enabled = unlockEnabled) {
                        Text(text = "Unlock All Doors")
                    }
                }
                item {
                    Button(
                        onClick = onQueryOtherDevicesClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Query Devices")
                    }
                }
            }
            LaunchedEffect(Unit){
                focusRequester.requestFocus()
            }
        }

    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun MainAppPreviewEvents() {
    MainApp(
        onQueryOtherDevicesClicked = {},
        lockCar = {},
        unlockCar = {},
        startEngine = {}
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun MainAppPreviewEmpty() {
    MainApp(
        onQueryOtherDevicesClicked = {},
        lockCar = {},
        unlockCar = {},
        startEngine = {}
    )
}

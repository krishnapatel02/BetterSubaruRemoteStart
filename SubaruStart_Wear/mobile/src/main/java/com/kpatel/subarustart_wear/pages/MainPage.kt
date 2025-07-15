package com.kpatel.subarustart_wear.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kpatel.subarustart_wear.DataStoreSingleton
import com.kpatel.subarustart_wear.R
import com.kpatel.subarustart_wear.Screens
import com.kpatel.subarustart_wear.execute
import com.kpatel.subarustart_wear.executeMobile
import com.kpatel.subarustart_wear.ui.theme.SubaruStart_WearTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


@Composable
fun PresetCard(modifier: Modifier = Modifier, cardNumber: Int, context: Context, navController: NavHostController){
    /*
    * A card UI element that can be attached to a UI preset, when pressed will start the car with
    * the selected parameters.
    * */

    val dataStore = DataStoreSingleton.getDataStoreRepo()
    var cardName = runBlocking {  dataStore.getNameCard(cardNumber)}
    var temp = runBlocking { dataStore.getCardTemp(cardNumber) }
    val haptic = LocalHapticFeedback.current
    var engineStart by remember{ mutableStateOf(true)}
    LaunchedEffect(engineStart) { //A delay on commands so that commands aren't spammed...
        if (engineStart) return@LaunchedEffect
        else delay(20000)
        engineStart = true
    }
    Card (modifier = Modifier
        .clickable {
            if (engineStart) {
                executeMobile(cardNumber, context)
            } else {
                Toast
                    .makeText(context, "Command already sent, please wait", Toast.LENGTH_SHORT)
                    .show()
            }
            engineStart = false
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        .width(180.dp)
        .height(100.dp)){
        Column {
            Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) {
                Box(modifier = Modifier
                    .width(150.dp)
                    .height(25.dp)) {
                    Text(
                        text = cardName, overflow = TextOverflow.Ellipsis, fontSize = 15.sp,
                        style = MaterialTheme.typography.bodyMedium, maxLines = 1,
                        modifier = Modifier.padding(
                            start = 16.dp
                        )
                    )
                }
                Spacer(Modifier.weight(1f))

                IconButton(onClick = { //Looks at PresetCard's number assignment to find correct config
                    when(cardNumber){
                        1-> {
                            navController.navigate(Screens.EditPreset1.name)
                        }
                        2->{
                            navController.navigate(Screens.EditPreset2.name)
                        }
                        3->{
                            navController.navigate(Screens.EditPreset3.name)
                        }
                        4->{
                            navController.navigate(Screens.EditPreset4.name)
                        }
                    }


                },
                    Modifier
                        .size(30.dp)
                        .padding()) {
                    Icon(Icons.Outlined.Create, "Edit")
                }
            }
            Spacer(Modifier.size(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$temp \u2109",
                    modifier = Modifier.padding(
                        start = 16.dp, end=35.dp), fontSize = 30.sp)
                Icon(Icons.Outlined.Refresh, "Start",
                    Modifier
                        .size(35.dp)
                        .padding(end = 5.dp))
            }
        }
    }

}

fun onClickWearButton(navController: NavHostController, useLocation: Boolean = false){
    if(useLocation){
        navController.navigate(Screens.Setup3.name)

    }else {
        navController.navigate(Screens.WearConfig.name)
    }
}



@OptIn(DelicateCoroutinesApi::class)
suspend fun onClickLock(context: Context, haptic: HapticFeedback){
    val datastore = DataStoreSingleton.getDataStoreRepo()
    execute(context, "lock", datastore)
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun onClickUnlock(context: Context, haptic: HapticFeedback){

    val datastore = DataStoreSingleton.getDataStoreRepo()
    execute(context, "unlock", datastore)
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

}

fun launchMapsIntent(context: Context, lat: Double =44.0483, lon: Double =-123.1425) {
    //For future if adding vehicle location
    //val gmmIntentUri = Uri.parse("geo:44.0483, -123.1425?q=44.0483, -123.1425")
    Toast.makeText(context, "Launching directions", Toast.LENGTH_SHORT).show()
    val gmmIntentUri = Uri.parse("google.navigation:q=$lat, $lon&mode=w")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mapIntent.setPackage("com.google.android.apps.maps")
    context.startActivity(mapIntent)

}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun MainPage(
    onQueryOtherDevicesClicked: () -> Unit,
    innerPadding: PaddingValues,
    navController: NavHostController,
    context: Context
){
    /*
    * Layout the cards on a UI Page.
    * */
    val haptic = LocalHapticFeedback.current
    var locked by remember{ mutableStateOf(true)}
    var unlocked by remember{ mutableStateOf(true)}
    val dataStore = DataStoreSingleton.getDataStoreRepo()
    var useLocation = runBlocking { dataStore.getLocationSetting() }
    LaunchedEffect(locked) {
        if (locked) return@LaunchedEffect
        else delay(20000)
        locked = true
    }

    LaunchedEffect(unlocked) {
        if (unlocked) return@LaunchedEffect
        else delay(20000)
        unlocked = true
    }

    Column(
        modifier = androidx.compose.ui.Modifier
            .padding()
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.Info, contentDescription = "")
            Spacer(Modifier.size(5.dp))
            Text(text = "Edit Watch Preset")
            Spacer(Modifier.size(10.dp))
            Button(onClick = { onClickWearButton(navController, useLocation) }, shape = CircleShape, modifier= Modifier.size(50.dp),
                contentPadding = PaddingValues(0.dp)) {
                Icon(painter = painterResource(
                    R.drawable.watch
                ), contentDescription = "Watch Preset" )
            }
            Spacer(Modifier.size(10.dp))
            
        }
        Spacer(Modifier.size(10.dp))
        Spacer(Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.Info, contentDescription = "")
            Spacer(Modifier.size(5.dp))
            Text(text = "Edit login")
            Spacer(Modifier.size(10.dp))
            Button(onClick = { navController.navigate(Screens.Setup.name) }, shape = CircleShape, modifier= Modifier.size(50.dp), contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Outlined.Settings, "Settings")
            }
            Spacer(Modifier.size(10.dp))
        }
        Spacer(Modifier.size(10.dp))
        /*Row(verticalAlignment = Alignment.CenterVertically){
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(5.dp))
            Text(text = "Auto Lock @ Midnight?")
            Spacer(Modifier.size(10.dp))
            Checkbox(checked = runBlocking { dataStore.getLockService() }, onCheckedChange = {})
            Spacer(Modifier.size(10.dp))
        }*/
        Spacer(modifier = Modifier.size(40.dp))
        Row {
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { runBlocking{onClickUnlock(context, haptic)}
                                 unlocked = false}, enabled = unlocked, shape = CircleShape, modifier= Modifier.size(70.dp),
                    contentPadding = PaddingValues(0.dp)) {
                    Icon(
                        painter = painterResource(
                            R.drawable.lock_open), contentDescription = "Unlock Car", modifier = Modifier.size(40.dp) )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = "Unlock Car")
            }
            
            Spacer(Modifier.size(30.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {
                    runBlocking {
                        onClickLock(context, haptic)
                    }
                    locked = false
                }, enabled = locked, shape = CircleShape, modifier= Modifier.size(70.dp),
                    contentPadding = PaddingValues(0.dp)) {
                    Icon(
                        Icons.Outlined.Lock, contentDescription = "Lock Car", modifier = Modifier.size(40.dp) )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = "Lock Car")
            }
            Spacer(Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.size(80.dp))
        Row {
            Text(text = "Remote Start Presets", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.size(25.dp))
        Column {
            Row {
                PresetCard(cardNumber = 1, context = context, navController = navController)
                Spacer(Modifier.size(20.dp))
                PresetCard(cardNumber = 2, context = context, navController = navController)
            }
            Spacer(modifier = Modifier.size(30.dp))
            Row {
                PresetCard(cardNumber = 3, context = context, navController = navController)
                Spacer(Modifier.size(20.dp))
                PresetCard(cardNumber = 4, context = context, navController = navController)
            }
        }


    }
}


@Composable
@Preview
fun MainPagePreview(){
    SubaruStart_WearTheme {
        MainPage(
            onQueryOtherDevicesClicked = { },
            innerPadding = PaddingValues(),
            navController = rememberNavController(),
            context = LocalContext.current
        )

    }
}
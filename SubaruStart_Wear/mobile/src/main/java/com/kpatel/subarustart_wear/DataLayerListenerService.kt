package com.kpatel.subarustart_wear
/*
*
* File handles background service needed to handle events sent by the watch over the listener service.
*
* */
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

class DataLayerListenerService(): WearableListenerService() {
    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        val datastore = DataStoreSingleton.getDataStoreRepoWear(this)
        var username = ""
        var password = ""
        var vehicleKey = ""
        var deviceID = ""
        var pin = ""
        var airCirc = ""
        var climateZoneAirMode = ""
        var heatedRearWindow = ""
        var engineRuntime = ""
        var temperature = ""
        var toastText = ""
        var init = DataStoreSingleton.isInitialized //Use singleton, cannot have more than one instance of datastore
        runBlocking {
            //Fetch all info from datastore
            username = datastore.getUsername()
            password = datastore.getPassword()
            vehicleKey = datastore.getVehicleKey()
            deviceID = datastore.getDeviceID()
            pin = datastore.getPin()
            airCirc = datastore.getWearAirRecirc()
            climateZoneAirMode = datastore.getWearVentSetting()
            temperature = datastore.getWearTemp().toString()
            heatedRearWindow = datastore.getWearRearDefroster().toString()
            engineRuntime = datastore.getWearRuntime().toString()
        }
        //Toast.makeText(this, "LOCKED", Toast.LENGTH_SHORT).show()
        println(p0.path)
        //Run watch request depending on which is selected.
        when(p0.path){
            "LockAll" -> {
                toastText = execute(this, "lock", datastore)
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
            "StartEngine"->{
                toastText = execute(this, "start", datastore)
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
            "UnlockAll"->{
                toastText = execute(this, "unlock", datastore)
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

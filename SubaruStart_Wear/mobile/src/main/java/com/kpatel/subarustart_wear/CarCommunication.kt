package com.kpatel.subarustart_wear
/*
*
* File handles all communication with the car. Execute function handles requests from phone, ExecuteMobile function
* handles requests from watch.
*
* */
import android.content.Context
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import java.io.IOException

class CookieTracker : CookieJar {
    //OkHttp doesn't keep the cookies for the session, this allows cookies to stay persistent after logging in.
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies.toMutableList()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }
}

class loginStatus(var client: OkHttpClient, var status: Boolean, var toastText: String)

suspend fun login(username: String, password: String, vehicleKey: String, deviceId: String, context: Context): Boolean {
    //Not used
    val cookieTracker = CookieTracker()
    val client = OkHttpClient().newBuilder().cookieJar(cookieTracker).build()
    val url = "https://www.mysubaru.com/login"
    val body = FormBody.Builder()
    var status = false
    var success = false
    var toastText = ""
    body.add("username", username)
        .add("password", password)
        .add("lastSelectedVehicleKey", vehicleKey)
        .add("deviceId", deviceId)

    val form = body.build()

    val request = Request.Builder().url(url).post(form)
        .build()

    lateinit var response: Response

    response = client.newCall(request).await()

    if(checkResponsePattern(response.toString())){
        toastText = "LOGIN UNSUCCESSFUL, re-enter login"
        status = false
    }else{
        status = response.isSuccessful
    }



    return status
}





fun executeMobile(cardNumber: Int, context: Context){
    /*
    *
    * Execute function for watch start parameters.
    *
    * */
    val cookieTracker = CookieTracker()
    val client = OkHttpClient().newBuilder().cookieJar(cookieTracker).build()
    val datastore = DataStoreSingleton.getDataStoreRepo()
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
    val url = "https://www.mysubaru.com/login"
    var toastText = ""
    val body = FormBody.Builder()
    var status = false
    runBlocking {
        username = datastore.getUsername()
        password = datastore.getPassword()
        vehicleKey = datastore.getVehicleKey()
        deviceID = datastore.getDeviceID()
        pin = datastore.getPin()
        airCirc = datastore.getCardAirCirc(cardNumber)
        climateZoneAirMode = datastore.getCardVentSetting(cardNumber)
        temperature = datastore.getCardTemp(cardNumber).toString()
        heatedRearWindow = datastore.getCardRearDefrost(cardNumber).toString()
        engineRuntime = datastore.getCardRuntime(cardNumber).toString()
    }
    body.add("username", username)
        .add("password", password)
        .add("lastSelectedVehicleKey", vehicleKey)
        .add("deviceId", deviceID)

    val form = body.build()

    val request = Request.Builder().url(url = url).post(form).build()

    client.newCall(request).enqueue(object : Callback{
        //Have to enqueue the request, can't execute otherwise thread gets blocked... enqueue automatically creates a background thread.
        override fun onFailure(call: Call, e: IOException) {
            Looper.prepare()
            Toast.makeText(context, "ERROR: Check Internet Connection/Login", Toast.LENGTH_SHORT).show()
        }
        override fun onResponse(call: Call, response: Response) {
            status = response.isSuccessful
            if(status) {
                startEngine(username, password, vehicleKey, deviceID, pin, temperature,
                    engineRuntime, climateZoneAirMode, "7", airCirc, heatedRearWindow, context, client)


            }
        }
    })

}

fun execute(context: Context, action: String, datastore: DataStoreRepo): String {
    //Handles car requests.
    val cookieTracker = CookieTracker()
    val client = OkHttpClient().newBuilder().cookieJar(cookieTracker).build()
    val url = "https://www.mysubaru.com/login"
    val body = FormBody.Builder()
    var status = false
    var success = false
    var toastText = ""
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

    runBlocking {
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

    //create form.
    body.add("username", username)
        .add("password", password)
        .add("lastSelectedVehicleKey", vehicleKey)
        .add("deviceId", deviceID)

    val form = body.build()
    //build form and request
    val request = Request.Builder().url(url = url).post(form).build()

    client.newCall(request).enqueue(object : Callback{
        override fun onFailure(call: Call, e: IOException) {
            Looper.prepare()
            Toast.makeText(context, "ERROR: Check Internet Connection/Login", Toast.LENGTH_SHORT).show()
        }
        override fun onResponse(call: Call, response: Response) {
            Looper.prepare()
            status = response.isSuccessful
            //if successful response we want to create a new request with the action the user wants.
            if(status) {
                when (action) {
                    "unlock" -> {
                        unlockCar(
                            username,
                            password,
                            vehicleKey,
                            deviceID,
                            pin,
                            context,
                            client
                        )
                    }
                    "lock" -> {
                        lockCar(username, password, vehicleKey, deviceID, pin, context, client)
                    }
                    "start" -> {
                        startEngine(username, password, vehicleKey, deviceID, pin, temperature,
                            engineRuntime, climateZoneAirMode, "7", airCirc, heatedRearWindow, context, client)
                    }
                }
            }
        }
    })



    return toastText



}


fun checkResponsePattern(responseString: String): Boolean {
    //Check for errors in URL
    val pattern = Regex("""
        ^url=(https://www\.mysubaru\.com/login\.html\?errorCode=MYS\d{3})\}$
    """.trimIndent())
    val match = pattern.find(responseString)
    if(match != null){
        return false
    }else{
        return true
    }
}

fun startEngine(
    //Command to build and send start engine form
    username: String, password: String, vehicleKey: String, deviceId: String, pin: String, frontTemp: String,
    runTimeMinutes: String, FrontAirMode: String, FrontAirVolume: String,
    outerAirCirculation: String, rearWindow: String, context: Context, client: OkHttpClient): String {
    lateinit var loginStatus: loginStatus
    lateinit var response: Response


    val dataPayload = HashMap<String, String>()
    val urlString = "https://www.mysubaru.com/service/g2/engineStart/execute.json"
    dataPayload["now"] = (System.currentTimeMillis()).toString()
    dataPayload["pin"] = pin
    dataPayload["horn"] = "true"
    dataPayload["delay"] = "0"
    dataPayload["startEngineSetting"] = "on"
    dataPayload["runTimeMinutes"] = runTimeMinutes.toString()
    dataPayload["climateZoneFrontTemp"] = frontTemp.toString()
    dataPayload["outerAirCirculation"] = outerAirCirculation
    dataPayload["climateZoneFrontAirMode"] = FrontAirMode
    dataPayload["climateZoneFrontAirVolume"] = FrontAirVolume
    dataPayload["airConditionOn"] = "false"
    dataPayload["heatedRearWindowActive"] = rearWindow
    dataPayload["heatedSeatFrontLeft"] = "OFF"
    dataPayload["heatedSeatFrontRight"] = "OFF"
    dataPayload["startConfiguration"] = "START_ENGINE_ALLOW_KEY_IN_IGNITION"


    val formBuild = FormBody.Builder()
    for ((key, value) in dataPayload) {
        formBuild.add(key, value)
    }
    val form = formBuild.build()
    val request = Request.Builder().url(urlString).post(form).build()
    var success = "REQUEST FAILED"
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Looper.prepare()
            Toast.makeText(context, "ERROR: Check Internet Connection/Login", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call, response: Response) {
            Looper.prepare()
            Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show()

            success = "REQUEST SUCCESS"

        }
    })
    return success

}

fun unlockCar(username: String, password: String, vehicleKey: String, deviceId: String, pin: String, context: Context, client: OkHttpClient): String {
    //Function to send request to unlock car, must be ran with a client that has a logged in session
    lateinit var loginStatus: loginStatus
    lateinit var response: Response
    val dataPayload = HashMap<String, String>()
    dataPayload["now"] = (System.currentTimeMillis()).toString()
    dataPayload["pin"] = pin
    dataPayload["delay"] = "0"
    dataPayload["horn"] = "true"
    dataPayload["unlockDoorType"] = "ALL_DOORS_CMD"

    val urlString = "https://www.mysubaru.com/service/g2/unlock/execute.json"
    val formBuild =  FormBody.Builder()
    for((key, value ) in dataPayload){
        formBuild.add(key, value)
    }
    val form = formBuild.build()
    val request = Request.Builder().url(urlString).post(form).build()
    var success = "REQUEST FAILED"
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Looper.prepare()
            Toast.makeText(context, "ERROR: Check Internet Connection/Login", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call, response: Response) {
            Looper.prepare()
            Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show()

            // Handle successful response
            success = "REQUEST SUCCESS"

            }

    })
    return success
}


fun lockCar(username: String, password: String, vehicleKey: String, deviceId: String, pin: String, context: Context, client: OkHttpClient): String {
    //Function to send request to lock car, must be ran with a client that has a logged in session
    val dataPayload = HashMap<String, String>()
    dataPayload["now"] = (System.currentTimeMillis()).toString()
    dataPayload["pin"] = pin
    dataPayload["delay"] = "0"
    dataPayload["horn"] = "true"
    dataPayload["unlockDoorType"] = "ALL_DOORS_CMD"

    val urlString = "https://www.mysubaru.com/service/g2/lock/execute.json"
    val formBuild =  FormBody.Builder()
    for((key, value ) in dataPayload){
        formBuild.add(key, value)
    }
    val form = formBuild.build()
    val request = Request.Builder().url(urlString).post(form).build()
    var success = "REQUEST FAILED"
    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            // Handle failure
            Looper.prepare()
            Toast.makeText(context, "ERROR: Check Internet Connection/Login", Toast.LENGTH_SHORT).show()
            //println(e.message)
            success = "REQUEST FAILED"
        }

        override fun onResponse(call: Call, response: Response) {
            Looper.prepare()
            Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show()
            val body = response.body
            if (response.isSuccessful) {
                // Handle successful response
                success = "REQUEST SUCCESS"
            } else {
                // Handle unsuccessful response
                success =  "ERROR CHECK INTERNET/LOGIN"
            }
        }
    })
    return success

}


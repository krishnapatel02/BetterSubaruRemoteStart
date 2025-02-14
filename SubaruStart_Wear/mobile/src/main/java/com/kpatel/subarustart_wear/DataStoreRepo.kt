package com.kpatel.subarustart_wear

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

const val PREFERENCE_NAME = "user_settings"

class DataStoreRepo(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)
    private val dataStore = context.dataStore
    private  object  PreferenceKeys{
        //Set all preference keys up...
        val firstSetup = booleanPreferencesKey("first_setup")
        val lockBackgroundService = booleanPreferencesKey("lock_background_service")
        val username = stringPreferencesKey("username")
        val password = stringPreferencesKey("password")
        val deviceID = stringPreferencesKey("deviceID")
        val vehicleKey =  stringPreferencesKey("vehicleKey")
        val pin =  stringPreferencesKey("pin")
        val nameCard1 = stringPreferencesKey("nameCard1")
        val nameCard2 = stringPreferencesKey("nameCard2")
        val nameCard3 = stringPreferencesKey("nameCard3")
        val nameCard4 = stringPreferencesKey("nameCard4")

        /*CARD TEMPERATURES*/
        val cardTemp1 = intPreferencesKey("cardTemp1")
        val cardTemp2 = intPreferencesKey("cardTemp2")
        val cardTemp3 = intPreferencesKey("cardTemp3")
        val cardTemp4 = intPreferencesKey("cardTemp4")

        /*CARD RUNTIMES*/
        val cardRuntime1 = intPreferencesKey("cardRuntime1")
        val cardRuntime2 = intPreferencesKey("cardRuntime2")
        val cardRuntime3 = intPreferencesKey("cardRuntime3")
        val cardRuntime4 = intPreferencesKey("cardRuntime4")

        /*CARD VENT SETTINGS*/
        val cardVentSetting1 = stringPreferencesKey("cardVentSetting1")
        val cardVentSetting2 = stringPreferencesKey("cardVentSetting2")
        val cardVentSetting3 = stringPreferencesKey("cardVentSetting3")
        val cardVentSetting4 = stringPreferencesKey("cardVentSetting4")

        /*CARD REAR DEFROST SETTINGS*/
        val cardRearDefrostSetting1 = booleanPreferencesKey("cardRearDefrostSetting1")
        val cardRearDefrostSetting2 = booleanPreferencesKey("cardRearDefrostSetting2")
        val cardRearDefrostSetting3 = booleanPreferencesKey("cardRearDefrostSetting3")
        val cardRearDefrostSetting4 = booleanPreferencesKey("cardRearDefrostSetting4")

        /*CARD AIR RECIRC SETTINGS*/

        val cardAirCircSetting1 = stringPreferencesKey("cardAirCircSetting1")
        val cardAirCircSetting2 = stringPreferencesKey("cardAirCircSetting2")
        val cardAirCircSetting3 = stringPreferencesKey("cardAirCircSetting3")
        val cardAirCircSetting4 = stringPreferencesKey("cardAirCircSetting4")


        /* WEAR SETTINGS */

        val wearTemp = intPreferencesKey("wearTemp")
        val wearRuntime = intPreferencesKey("wearRuntime")
        val wearVentSetting = stringPreferencesKey("wearVentSetting")
        val wearRearDefrostSetting = booleanPreferencesKey("rearDefrostSetting")
        val wearAirCircSetting = stringPreferencesKey("wearAirCircSetting")
    }
    fun stopDatastore(){

    }

    //Various functions to set/get all settings, defaults returned if user has not set any...
    suspend fun setWearTemp(temp: Int){
        dataStore.edit { preferences->
            preferences[PreferenceKeys.wearTemp] = temp
        }
    }

    suspend fun getWearTemp(): Int {
        return  context.dataStore.data.first()[PreferenceKeys.wearTemp] ?: 70
    }

    suspend fun setWearRuntime(runtime: Int){
        dataStore.edit { preferences->
            preferences[PreferenceKeys.wearRuntime] = runtime
        }
    }

    suspend fun getWearRuntime(): Int {
        return  context.dataStore.data.first()[PreferenceKeys.wearRuntime] ?: 5
    }

    suspend fun setWearVentSetting(ventSetting: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.wearVentSetting] = ventSetting
        }
    }

    suspend fun getWearVentSetting(): String{
        return  context.dataStore.data.first()[PreferenceKeys.wearVentSetting] ?: "FACE"

    }

    suspend fun setWearAirRecirc(wearAirCircSetting: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.wearAirCircSetting] = wearAirCircSetting
        }
    }

    suspend fun getWearAirRecirc(): String{
        return  context.dataStore.data.first()[PreferenceKeys.wearAirCircSetting] ?: "recirculation"

    }

    suspend fun getWearRearDefroster(): Boolean {
        return  context.dataStore.data.first()[PreferenceKeys.wearRearDefrostSetting] ?: true
    }

    suspend fun setWearRearDefroster(rearDefroster: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.wearRearDefrostSetting] = rearDefroster
        }
    }

    suspend fun setUsername(username: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.username] = username
        }
    }

    suspend fun getUsername(): String {
        return  context.dataStore.data.first()[PreferenceKeys.username] ?: ""

    }
    suspend fun getPassword(): String{
        return context.dataStore.data.first()[PreferenceKeys.password] ?: ""
    }

    suspend fun getDeviceID(): String{
        return context.dataStore.data.first()[PreferenceKeys.deviceID] ?: ""
    }

    suspend fun getVehicleKey(): String{
        return context.dataStore.data.first()[PreferenceKeys.vehicleKey] ?: ""
    }
    suspend fun getPin(): String{
        return context.dataStore.data.first()[PreferenceKeys.pin] ?: ""
    }

    suspend fun setPassword(password: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.password] = password
        }
    }
    suspend fun setDeviceID(deviceID: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.deviceID] = deviceID
        }
    }
    suspend fun setVehicleKey(vehicleKey: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.vehicleKey] = vehicleKey
        }
    }

    suspend fun setPin(pin: String){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.pin] = pin
        }
    }

    suspend fun setFirstSetup(setup: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.firstSetup] = setup
        }
    }

    suspend fun getFirstSetup(): Boolean {
        return context.dataStore.data.first()[PreferenceKeys.firstSetup] ?: true
    }

    suspend fun setNameCard(cardNumber: Int, name: String) {
        dataStore.edit { preferences ->
            when(cardNumber){
                1 -> {
                    preferences[PreferenceKeys.nameCard1] = name
                }
                2 -> {
                    preferences[PreferenceKeys.nameCard2] = name
                }
                3 -> {
                    preferences[PreferenceKeys.nameCard3] = name
                }
                4 -> {
                    preferences[PreferenceKeys.nameCard4] = name
                }

            }
        }
    }

    suspend fun getNameCard(cardNumber: Int): String {
        when (cardNumber){
            1 -> {
                return context.dataStore.data.first()[PreferenceKeys.nameCard1] ?: "Heat & Defrost"
            }
            2->{
                return context.dataStore.data.first()[PreferenceKeys.nameCard2] ?: "Cool Windshield"
            }
            3->{
                return context.dataStore.data.first()[PreferenceKeys.nameCard3] ?: "Heat Front"

            }
            4 -> {
                return context.dataStore.data.first()[PreferenceKeys.nameCard4] ?: "Cool Front"

            }else -> {
                return "ERROR"
            }
        }

    }


    suspend fun setCardTemp(cardNumber: Int, temp: Int) {
        dataStore.edit { preferences ->
            when(cardNumber){
                1 -> {
                    preferences[PreferenceKeys.cardTemp1] = temp
                }
                2 -> {
                    preferences[PreferenceKeys.cardTemp2] = temp
                }
                3 -> {
                    preferences[PreferenceKeys.cardTemp3] = temp
                }
                4 -> {
                    preferences[PreferenceKeys.cardTemp4] = temp
                }

            }
        }
    }

    suspend fun getCardTemp(cardNumber: Int): Int {
        when (cardNumber){
            1 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardTemp1] ?: 80
            }
            2-> {
                return context.dataStore.data.first()[PreferenceKeys.cardTemp2] ?: 70
            }
            3->{
                return context.dataStore.data.first()[PreferenceKeys.cardTemp3] ?: 80

            }
            4 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardTemp4] ?: 70

            }else -> {
                return 70
             }
        }

    }

    suspend fun setCardRuntime(cardNumber: Int, runtime: Int) {
        dataStore.edit { preferences ->
            when(cardNumber){
                1 -> {
                    preferences[PreferenceKeys.cardRuntime1] = runtime
                }
                2 -> {
                    preferences[PreferenceKeys.cardRuntime2] = runtime
                }
                3 -> {
                    preferences[PreferenceKeys.cardRuntime3] = runtime
                }
                4 -> {
                    preferences[PreferenceKeys.cardRuntime4] = runtime
                }

            }
        }
    }

    suspend fun getCardRuntime(cardNumber: Int): Int {
        when (cardNumber){
            1 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardRuntime1] ?: 10
            }
            2-> {
                return context.dataStore.data.first()[PreferenceKeys.cardRuntime2] ?: 10
            }
            3->{
                return context.dataStore.data.first()[PreferenceKeys.cardRuntime3] ?: 10

            }
            4 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardRuntime4] ?: 10

            }else -> {
                return 10
            }
        }

    }

    suspend fun setCardVentSetting(cardNumber: Int, ventSetting: String) {
        dataStore.edit { preferences ->
            when(cardNumber){
                1 -> {
                    preferences[PreferenceKeys.cardVentSetting1] = ventSetting
                }
                2 -> {
                    preferences[PreferenceKeys.cardVentSetting2] = ventSetting
                }
                3 -> {
                    preferences[PreferenceKeys.cardVentSetting3] = ventSetting
                }
                4 -> {
                    preferences[PreferenceKeys.cardVentSetting4] = ventSetting
                }

            }
        }
    }

    suspend fun getCardVentSetting(cardNumber: Int): String {
        when (cardNumber){
            1 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardVentSetting1] ?: "WINDOW"
            }
            2->{
                return context.dataStore.data.first()[PreferenceKeys.cardVentSetting2] ?: "WINDOW"
            }
            3->{
                return context.dataStore.data.first()[PreferenceKeys.cardVentSetting3] ?: "FACE"

            }
            4 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardVentSetting4] ?: "FACE"

            }else -> {
            return "WINDOW"
        }
        }

    }

    suspend fun setCardRearDefrost(cardNumber: Int, defroster: Boolean) {
        dataStore.edit { preferences ->
            when(cardNumber){
                1 -> {
                    preferences[PreferenceKeys.cardRearDefrostSetting1] = defroster
                }
                2 -> {
                    preferences[PreferenceKeys.cardRearDefrostSetting2] = defroster
                }
                3 -> {
                    preferences[PreferenceKeys.cardRearDefrostSetting3] = defroster
                }
                4 -> {
                    preferences[PreferenceKeys.cardRearDefrostSetting4] = defroster
                }

            }
        }
    }

    suspend fun getCardRearDefrost(cardNumber: Int): Boolean {
        when (cardNumber){
            1 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardRearDefrostSetting1] ?: true
            }
            2-> {
                return context.dataStore.data.first()[PreferenceKeys.cardRearDefrostSetting2] ?: false
            }
            3->{
                return context.dataStore.data.first()[PreferenceKeys.cardRearDefrostSetting3] ?: true

            }
            4 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardRearDefrostSetting4] ?: false

            }else -> {
                return false
            }
        }

    }

    suspend fun setCardAirCirc(cardNumber: Int, air:String) {
        dataStore.edit { preferences ->
            when(cardNumber){
                1 -> {
                    preferences[PreferenceKeys.cardAirCircSetting1] = air
                }
                2 -> {
                    preferences[PreferenceKeys.cardAirCircSetting2] = air
                }
                3 -> {
                    preferences[PreferenceKeys.cardAirCircSetting3] = air
                }
                4 -> {
                    preferences[PreferenceKeys.cardAirCircSetting4] = air
                }

            }
        }
    }

    suspend fun getCardAirCirc(cardNumber: Int): String {
        when (cardNumber){
            1 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardAirCircSetting1] ?: "recirculation"
            }
            2-> {
                return context.dataStore.data.first()[PreferenceKeys.cardAirCircSetting2] ?: "recirculation"
            }
            3->{
                return context.dataStore.data.first()[PreferenceKeys.cardAirCircSetting3] ?: "recirculation"

            }
            4 -> {
                return context.dataStore.data.first()[PreferenceKeys.cardAirCircSetting4] ?: "recirculation"

            }else -> {
            return "recirculation"
            }
        }

    }
    suspend fun setLockService(lock: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.lockBackgroundService] = lock
        }
    }
    suspend fun getLockService(): Boolean {
        return context.dataStore.data.first()[PreferenceKeys.lockBackgroundService] ?: false

    }

}
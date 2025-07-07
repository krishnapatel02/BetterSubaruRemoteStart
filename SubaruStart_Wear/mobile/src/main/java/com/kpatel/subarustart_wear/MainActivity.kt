package com.kpatel.subarustart_wear

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.kpatel.subarustart_wear.pages.MainPage
import com.kpatel.subarustart_wear.pages.SetupScreen
import com.kpatel.subarustart_wear.pages.SetupScreen2
import com.kpatel.subarustart_wear.pages.SetupScreen3
import com.kpatel.subarustart_wear.pages.UpdatePresetScreen
import com.kpatel.subarustart_wear.pages.UpdateWearScreen
import com.kpatel.subarustart_wear.ui.theme.SubaruStart_WearTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException


enum class Screens(@StringRes val title: Int){
    Setup(title=R.string.setup_screen_title),
    Setup2(title=R.string.setup2_screen_title),
    Setup3(title=R.string.setup3_screen_title),
    Main(title = R.string.main_screen_title),
    WearConfig(title = R.string.wear_config_screen),
    EditPreset1(title = R.string.edit_preset_1),
    EditPreset2(title = R.string.edit_preset_2),
    EditPreset3(title = R.string.edit_preset_3),
    EditPreset4(title = R.string.edit_preset_4)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(modifier: Modifier = Modifier, currentScreen: Screens){
    Row {
        TopAppBar(title = { Text(stringResource(currentScreen.title))})
    }

}

object DataStoreSingleton {
    /*
    * Datastore crashes the app if there is more than one instance of datastore... to get the datastore to
    * work on the watch app if mobile app is in background we need a singleton object.
    * */

    private lateinit var dataStoreRepo: DataStoreRepo
    var isInitialized = false
    fun initialize(dataStore: DataStoreRepo) {
        if(!isInitialized) {
            dataStoreRepo = dataStore
        }
        isInitialized = true
    }

    fun getDataStoreRepoWear(context: Context): DataStoreRepo {
        if(!isInitialized) { //Check if repo is initialized to avoid crash on background request...
            dataStoreRepo = DataStoreRepo(context)
            isInitialized = true
        }
        return dataStoreRepo
    }

    fun getDataStoreRepo(): DataStoreRepo {
        return dataStoreRepo
    }
}



@Composable
fun MainScreen(
    onQueryOtherDevicesClicked: () -> Unit,
    dataStore: DataStoreRepo,
    isFirstSetup: Boolean,
    mainActivity: MainActivity
){

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    var currentScreen = Screens.valueOf(
        backStackEntry?.destination?.route ?: Screens.Setup.name
        //Check datastore if app has been setup before to show home screen.
    )
    var currentScreenName = "Setup"
    val startDest = if(isFirstSetup){
        Screens.Setup.name
    }else{
        Screens.Main.name
    }
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { AppBar(currentScreen = currentScreen)}
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDest,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ){
                composable(route = Screens.Setup.name){
                    currentScreenName = Screens.Setup.name
                     currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name
                    )
                    SetupScreen3 (dataStore, mainActivity.applicationContext, navController) {
                        val location = runBlocking { dataStore.getLocationSetting() }
                        if(location){
                            navController.navigate(Screens.Setup2.name)
                        }else{
                            navController.navigate(Screens.Main.name)

                        }
                    }
                }
                composable(route = Screens.Setup2.name){
                    currentScreenName = Screens.Setup2.name
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup2.name
                    )
                    SetupScreen2(dataStore, mainActivity.applicationContext, navController) {
                        navController.navigate(Screens.Setup3.name)
                    }
                }
                composable(route = Screens.Setup2.name){
                    currentScreenName = Screens.Setup2.name
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup2.name
                    )
                    SetupScreen3(dataStore, mainActivity.applicationContext, navController) {
                        navController.navigate(Screens.Setup3.name)
                    }
                }
                composable(route = Screens.Main.name){
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name)

                    currentScreenName = Screens.Main.name
                    MainPage(onQueryOtherDevicesClicked = onQueryOtherDevicesClicked, innerPadding = innerPadding, navController,
                        mainActivity.applicationContext)
                }
                composable(route = Screens.WearConfig.name) {
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name)
                    currentScreenName = Screens.WearConfig.name
                    UpdateWearScreen(mainActivity.applicationContext, navController, dataStore)

                }
                composable(route = Screens.EditPreset1.name) {
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name)
                    currentScreenName = Screens.EditPreset1.name
                    UpdatePresetScreen(1, mainActivity.applicationContext, navController)

                }
                composable(route = Screens.EditPreset2.name) {
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name)
                    currentScreenName = Screens.EditPreset2.name
                    UpdatePresetScreen(2, mainActivity.applicationContext, navController)

                }
                composable(route = Screens.EditPreset3.name) {
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name)
                    currentScreenName = Screens.EditPreset3.name
                    UpdatePresetScreen(3, mainActivity.applicationContext, navController)

                }
                composable(route = Screens.EditPreset4.name) {
                    currentScreen = Screens.valueOf(
                        backStackEntry?.destination?.route ?: Screens.Setup.name)
                    currentScreenName = Screens.EditPreset4.name
                    UpdatePresetScreen(4, mainActivity.applicationContext, navController)

                }
            }
        }
    }
}
@OptIn(DelicateCoroutinesApi::class)
fun setupGlobalScope(){
    GlobalScope.launch( Dispatchers.IO) {
        Looper.prepare()
    }
}


fun scheduleDailyTask(context: Context){
    val now = Calendar.getInstance();

    val scheduleTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(now)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val initialDelay = scheduleTime.timeInMillis - now.timeInMillis

    val dailyWorkRequest = PeriodicWorkRequestBuilder<BackgroundDailyLockWorkerClass>(24, TimeUnit.HOURS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "LockCarDaily",
        ExistingPeriodicWorkPolicy.REPLACE,
        dailyWorkRequest
    )
}

@Composable
fun ScheduleTask(context: Context) {
    // Call the function to schedule the task (you can decide when to trigger this)
    scheduleDailyTask(context)
}

class MainActivity : ComponentActivity() {
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private lateinit var dataStore: DataStoreRepo
    private var isFirstSetup: Boolean = true
    private var isRunLockBackgroundService: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = DataStoreRepo(this)
        DataStoreSingleton.initialize((dataStore))
        setupGlobalScope()
        runBlocking  {
            // Fetch data from DataStore and set the variable
            isFirstSetup = dataStore.getFirstSetup()
            isRunLockBackgroundService = dataStore.getLockService()
            //println("isFirstSetup: $isFirstSetup")
        }

        //println("Firstsetup: $isFirstSetup")



        setContent {
           // if(isRunLockBackgroundService){
           //     ScheduleTask(context = this);
           // }
            SubaruStart_WearTheme {
                MainScreen(::onQueryOtherDevicesClicked, dataStore, isFirstSetup, this@MainActivity)
            }
        }
    }
    private fun onQueryOtherDevicesClicked() {
        lifecycleScope.launch {
            try {
                val nodes = getCapabilitiesForReachableNodes()
                    .filterValues { MOBILE_CAPABILITY in it || WEAR_CAPABILITY in it }.keys
                displayNodes(nodes)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Querying nodes failed: $exception")
            }
        }
    }
    private suspend fun getCapabilitiesForReachableNodes(): Map<Node, Set<String>> =
        capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE)
            .await()
            // Pair the list of all reachable nodes with their capabilities
            .flatMap { (capability, capabilityInfo) ->
                capabilityInfo.nodes.map { it to capability }
            }
            // Group the pairs by the nodes
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
            // Transform the capability list for each node into a set
            .mapValues { it.value.toSet() }

    private fun displayNodes(nodes: Set<Node>) {
        val message = if (nodes.isEmpty()) {
            "No_devices"
        } else {
            nodes.joinToString(", ") { it.displayName }
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        private const val TAG = "MainActivity"
        private const val LOCK_ALL = "LockAll"
        private const val CAMERA_CAPABILITY = "camera"
        private const val WEAR_CAPABILITY = "wear"
        private const val MOBILE_CAPABILITY = "mobile"
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SubaruStart_WearTheme {
    }
}
/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.kpatel.subarustart_wear

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainApp(
                onQueryOtherDevicesClicked = ::onQueryOtherDevicesClicked,
                lockCar = ::lockCar,
                unlockCar = ::unlockCar,
                startEngine = ::startEngine
            )
        }
    }
    private fun lockCar(){
        lifecycleScope.launch {
            val nodes = capabilityClient
                .getCapability(MOBILE_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                .await()
                .nodes
            nodes.map {
                    node ->
                async {
                    messageClient.sendMessage(node.id, LOCK_ALL, byteArrayOf())
                }
            }.awaitAll()
        }
    }
    private fun unlockCar(){
        lifecycleScope.launch {
            val nodes = capabilityClient
                .getCapability(MOBILE_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                .await()
                .nodes
            nodes.map {
                    node ->
                async {
                    messageClient.sendMessage(node.id, UNLOCK_ALL, byteArrayOf())
                }
            }.awaitAll()
        }
    }
    private fun startEngine(){
        lifecycleScope.launch {
            val nodes = capabilityClient
                .getCapability(MOBILE_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                .await()
                .nodes
            nodes.map {
                    node ->
                async {
                    messageClient.sendMessage(node.id, ENGINE_START, byteArrayOf())
                }
            }.awaitAll()
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

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    companion object {
        private const val TAG = "MainActivity"
        private const val LOCK_ALL = "LockAll"
        private const val UNLOCK_ALL = "UnlockAll"
        private const val ENGINE_START = "StartEngine"
        private const val CAMERA_CAPABILITY = "camera"
        private const val WEAR_CAPABILITY = "wear"
        private const val MOBILE_CAPABILITY = "mobile"
    }
}

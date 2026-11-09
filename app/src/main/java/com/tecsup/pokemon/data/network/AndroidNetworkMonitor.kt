package com.tecsup.pokemon.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.tecsup.pokemon.domain.repository.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidNetworkMonitor(context: Context) : NetworkMonitor {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val _isOnline = MutableStateFlow(checkOnline())
    override val isOnline = _isOnline.asStateFlow()

    init {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) { _isOnline.value = checkOnline() }
                override fun onLost(network: Network) { _isOnline.value = checkOnline() }
                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    _isOnline.value = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            }
        )
    }

    private fun checkOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

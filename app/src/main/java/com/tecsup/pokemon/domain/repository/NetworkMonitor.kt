package com.tecsup.pokemon.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
}

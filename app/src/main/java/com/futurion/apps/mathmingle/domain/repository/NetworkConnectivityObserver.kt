package com.futurion.apps.mathmingle.domain.repository

import com.futurion.apps.mathmingle.domain.model.NetworkStatus
import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectivityObserver {

  val networkStatus: StateFlow<NetworkStatus>


}
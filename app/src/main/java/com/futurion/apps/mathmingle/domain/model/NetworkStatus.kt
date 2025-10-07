package com.futurion.apps.mathmingle.domain.model

sealed class NetworkStatus {

    data object Connected: NetworkStatus()
    data object DisConnected: NetworkStatus()

}
package com.futurion.apps.mathmingle

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.futurion.apps.mathmingle.domain.model.NetworkStatus
import com.futurion.apps.mathmingle.domain.repository.NetworkConnectivityObserver
import com.futurion.apps.mathmingle.presentation.navigation.SetUpNavGraph
import com.futurion.apps.mathmingle.presentation.theme.customGreen
import com.futurion.apps.mathmingle.presentation.utils.NetworkStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var connectivityObserver: NetworkConnectivityObserver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        setContent {

            val status = connectivityObserver.networkStatus.collectAsStateWithLifecycle()
            var showMessageBar = rememberSaveable { mutableStateOf(false) }
            var message = rememberSaveable { mutableStateOf("") }
            var backgroundColor = remember { mutableStateOf(androidx.compose.ui.graphics.Color.Red) }

//            LaunchedEffect(key1 = status.value) {
//                when(status.value) {
//                    NetworkStatus.Connected -> {
//                        Log.d("NetworkStatus", "Connected")
//                        message.value = "Connected to Internet"
//                        backgroundColor.value = customGreen
//                        delay(2000)
//                        showMessageBar.value =false
//                    }
//                    NetworkStatus.DisConnected -> {
//                        Log.d("NetworkStatus", "DisConnected")
//                        showMessageBar.value =true
//                      //  message.value = "No Internet Connection"
//                        backgroundColor.value = androidx.compose.ui.graphics.Color.Red
//                        delay(3000)
//                        showMessageBar.value =false
//                    }
//                }
//            }

            MaterialTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    bottomBar = {
                        NetworkStatusBar(
                            isConnected = showMessageBar.value,
                            message = message.value,
                            backgroundColor = backgroundColor.value
                        )
                    }
                ) { innerPadding ->

                    SetUpNavGraph(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
} // code


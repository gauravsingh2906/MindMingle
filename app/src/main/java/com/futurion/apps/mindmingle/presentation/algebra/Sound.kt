package com.futurion.apps.mindmingle.presentation.algebra

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun rememberSoundPool(): SoundPool {
    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()
        SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attributes)
            .build()
    }
    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }
    return soundPool
}
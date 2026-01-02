package com.example.tvaccesibleandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.fillMaxSize
import android.content.pm.ActivityInfo

import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private const val STREAM_URL =
    "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar orientación horizontal
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Solo setea decorfits antes de Compose
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Oculta status bar y navegación en Compose
            SideEffect {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            VideoPlayer()
        }
    }
}

@Composable
fun VideoPlayer() {

    val context = androidx.compose.ui.platform.LocalContext.current

    val player = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                androidx.media3.common.MediaItem.fromUri(STREAM_URL)
            )
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    AndroidView(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        factory = {
            androidx.media3.ui.PlayerView(it).apply {
                this.player = player
            }
        }
    )
}

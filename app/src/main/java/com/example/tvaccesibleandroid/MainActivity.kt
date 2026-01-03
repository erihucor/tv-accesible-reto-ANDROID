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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.tvaccesibleandroid.data.ChannelsProvider

private const val CHANNEL_ON_STREAM = "ecuavisa"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar orientación horizontal
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Solo setea decorfits antes de Compose
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //Set el canal a usar
        val channel = ChannelsProvider.getById(CHANNEL_ON_STREAM) ?: return
        val streamUrl = channel.url

        setContent {
            // Oculta status bar y navegación en Compose
            SideEffect {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            VideoPlayer(streamUrl)
        }
    }
}

@Composable
fun VideoPlayer(streamUrl: String) {

    val context = androidx.compose.ui.platform.LocalContext.current

    val player = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(streamUrl) {
        player.setMediaItem(MediaItem.fromUri(streamUrl))
        player.prepare()
        player.play()
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

package com.example.tvaccesibleandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.fillMaxSize

private const val STREAM_URL =
    "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
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

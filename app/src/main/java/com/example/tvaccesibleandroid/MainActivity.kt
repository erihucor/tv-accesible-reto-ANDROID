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
import com.example.tvaccesibleandroid.model.Channel

import android.util.Log
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


private const val CHANNEL_ON_STREAM = "ecuavisa"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar orientación horizontal
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Solo setea decorfits antes de Compose
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //Set el canal a usar
        val initialChannel = ChannelsProvider.getById(CHANNEL_ON_STREAM) ?: return

        setContent {
            val channels = remember {
                ChannelsProvider.channels
            }

            var currentIndex by remember {
                mutableStateOf(0)
            }
            val currentChannel = channels[currentIndex]

            // Oculta status bar y navegación en Compose
            SideEffect {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            VideoPlayer(
                channel = currentChannel,
                onNextChannel = {
                    currentIndex = (currentIndex + 1) % channels.size
                }
            )
        }
    }
}

@Composable
fun VideoPlayer(
    channel: Channel,
    onNextChannel: () -> Unit
){
    val context = androidx.compose.ui.platform.LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("PLAYER", "Error reproduciendo stream", error)
                }
            })
        }
    }


    val streamUrl = channel.url

    LaunchedEffect(streamUrl) {
        player.setMediaItem(MediaItem.fromUri(streamUrl))
        player.prepare()
        player.play()
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                androidx.media3.ui.PlayerView(it).apply {
                    this.player = player
                    useController = false
                }
            }
        )

        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            onClick = onNextChannel
        ) {
            Text("Cambiar canal")
        }
    }
}

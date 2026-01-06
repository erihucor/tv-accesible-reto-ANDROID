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
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.shape.CircleShape


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
                },
                onPreviousChannel = {
                    currentIndex = (currentIndex - 1 + channels.size) % channels.size
                }
            )
        }
    }
}

@Composable
fun VideoPlayer(
    channel: Channel,
    onNextChannel: () -> Unit,
    onPreviousChannel: () -> Unit
){
    val context = androidx.compose.ui.platform.LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("PLAYER", "Error reproduciendo stream", error)

                    Toast.makeText(
                        context,
                        "No se puede reproducir canal",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
        }
    }
/*
    LaunchedEffect(channel) {
        Toast.makeText(
            context,
            channel.name,
            Toast.LENGTH_SHORT
        ).show()
    }
*/


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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Indicador izquierdo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Canal anterior",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(48.dp)
                )
            }

            // Indicador derecho
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Siguiente canal",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = channel.name,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Zona izquierda – canal anterior
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onPreviousChannel()
                    }
            )

            // Zona derecha – siguiente canal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onNextChannel()
                    }
            )
        }

    }
}

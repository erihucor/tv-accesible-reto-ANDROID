package com.example.tvaccesibleandroid

import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
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
import androidx.compose.ui.Alignment
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.view.KeyEvent

class MainActivity : ComponentActivity() {

    private var wakeLock: PowerManager.WakeLock? = null

    val channels =  ChannelsProvider.channels

    var currentIndex by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Forzar orientación horizontal
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Solo setea decorfits antes de Compose
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Evita que la TV entre en modo espera o pantalla de ahorro
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "tvAccesible:keepScreenOn"
        )
        wakeLock?.acquire(10 * 60 * 1000L)

        setContent {

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

    override fun onDestroy() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        super.onDestroy()
    }

    //Handle control remote buttons
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when (keyCode) {

            KeyEvent.KEYCODE_DPAD_UP -> {
                currentIndex = (currentIndex + 1) % channels.size
                return true
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                currentIndex = (currentIndex - 1 + channels.size) % channels.size
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
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
                        "El canal no esta disponible, cambie a otro",
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
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            }
        )


        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = channel.id,
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }

    }
}

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
import androidx.compose.runtime.saveable.rememberSaveable

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
import com.example.tvaccesibleandroid.model.ChannelType
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
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

            val goToNextChannel = {
                if (channels.isNotEmpty()) {
                    currentIndex = cycleChannelIndex(currentIndex, 1, channels.size)
                }
            }
            val goToPreviousChannel = {
                if (channels.isNotEmpty()) {
                    currentIndex = cycleChannelIndex(currentIndex, -1, channels.size)
                }
            }

            VideoPlayer(
                channel = currentChannel,
                onNextChannel = goToNextChannel,
                onPreviousChannel = goToPreviousChannel
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

internal fun buildYouTubeVideoId(url: String): String? {
    val normalizedUrl = url.trim()
    val youtubeRegex = Regex("""(?:youtube\.com/(?:watch\?v=|embed/|shorts/)|youtu\.be/)([A-Za-z0-9_-]{11})""")
    val match = youtubeRegex.find(normalizedUrl)

    if (match != null) {
        return match.groupValues[1]
    }

    val watchMatch = Regex("""[?&]v=([A-Za-z0-9_-]{11})""").find(normalizedUrl)
    return watchMatch?.groupValues?.get(1)
}

internal fun cycleChannelIndex(currentIndex: Int, direction: Int, size: Int): Int {
    if (size <= 1) return 0
    return ((currentIndex + direction) % size + size) % size
}

@Composable
fun VideoPlayer(
    channel: Channel,
    onNextChannel: () -> Unit,
    onPreviousChannel: () -> Unit
){
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    var currentSourceIndex by remember(channel.id) { mutableIntStateOf(0) }
    val source = channel.sources.getOrNull(currentSourceIndex)
    val streamUrl = source?.url.orEmpty()
    val sourceType = source?.type ?: ChannelType.STREAM

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

    fun tryNextSource(): Boolean {
    return if (currentSourceIndex < channel.sources.lastIndex) {
        currentSourceIndex += 1
        true
    } else {
        false
        }
    }

    key(channel.id, currentSourceIndex) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (sourceType == ChannelType.YOUTUBE) {
                AndroidView(
                    factory = { ctx ->
                        YouTubePlayerView(ctx).apply {
                            lifecycleOwner.lifecycle.addObserver(this)
                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    val videoId = buildYouTubeVideoId(streamUrl)
                                    if (!videoId.isNullOrBlank()) {
                                        youTubePlayer.loadVideo(videoId, 0f)
                                    } else if (tryNextSource()) {
                                        Toast.makeText(
                                            ctx,
                                            "Fuente de YouTube inválida, probando siguiente fuente...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            ctx,
                                            "No se pudo identificar el video de YouTube",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onError(
                                    youTubePlayer: YouTubePlayer,
                                    error: PlayerConstants.PlayerError
                                ) {
                                    if (tryNextSource()) {
                                        Toast.makeText(
                                            ctx,
                                            "Fuente de YouTube falló, probando siguiente fuente...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            ctx,
                                            "Video no disponible en este reproductor. Abre en YouTube.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            })
                        }
                    }
                )
            } else {
                val player = remember(streamUrl) {
                    ExoPlayer.Builder(context).build().apply {
                        addListener(object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                Log.e("PLAYER", "Error reproduciendo stream", error)

                                if (tryNextSource()) {
                                    Toast.makeText(
                                        context,
                                        "Fuente falló, probando siguiente fuente...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "El canal no esta disponible, cambie a otro",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                    }
                }

                LaunchedEffect(streamUrl) {
                    player.setMediaItem(MediaItem.fromUri(streamUrl))
                    player.prepare()
                    player.play()
                }

                DisposableEffect(player) {
                    onDispose { player.release() }
                }

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
            }

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

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            Color.Black,
                            RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val info = if (channel.sources.isEmpty()) {
                    "0/0"
                } else {
                    "${currentSourceIndex + 1}/${channel.sources.size}"
                }

                Box(
                    modifier = Modifier
                        .background(
                            Color.Blue,
                            RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = info,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            val (backgroundColor, displayText) = when (sourceType) {
                ChannelType.YOUTUBE -> Pair(
                Color(0xFFE53935), // rojo suave
                    "YouTube"
                )
                ChannelType.STREAM -> Pair(
                Color(0xFF43A047), // verde suave
                    "TV"
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        backgroundColor,
                        RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = displayText,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
}

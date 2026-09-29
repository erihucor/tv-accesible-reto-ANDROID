package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType

object ChannelsProvider {

    private const val CHANNELS_URL =
        "TESThttps://raw.githubusercontent.com/erihucor/tv-accesible-reto-ANDROID/feature/online-channel-prov/channels.json"

    fun getFallbackWarningMessage(): String =
        "No se pudieron obtener los canales en línea. Se están usando los canales de respaldo."

    private val fallbackChannels = listOf(
        Channel(
            id = "00",
            name = "BLN",
            sources = listOf(
                ChannelSource(
                    url = "https://www.youtube.com/watch?v=MzJllBzHH6Q",
                    type = ChannelType.YOUTUBE
                )
            )
        ),
        Channel(
            id = "01",
            name = "Oromar",
            sources = listOf(
                ChannelSource(
                    url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
                )
            )
        ),
        Channel(
            id = "02",
            name = "Oromar Internacional",
            sources = listOf(
                ChannelSource(
                    url = "https://stream.oromar.tv/hls/camriva/index.m3u8"
                )
            )
        ),
        Channel(
            id = "03",
            name = "Ecuador TV",
            sources = listOf(
                ChannelSource(
                    url = "https://video-eu1.streamerr.co/hls/s64029a8fdf/live.m3u8"
                )
            )
        ),
        Channel(
            id = "04",
            name = "RTS",
            sources = listOf(
                ChannelSource(
                    url = "https://d2w3o8zn50cs1k.cloudfront.net/ts:abr.m3u8"
                )
            )
        ),
        Channel(
            id = "05",
            name = "Ecuavisa GUAYAQUIL",
            sources = listOf(
                ChannelSource(
                    url = "https://dai.google.com/linear/hls/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8"
                )
            )
        ),
        Channel(
            id = "06",
            name = "Ecuavisa QUITO",
            sources = listOf(
                ChannelSource(
                    url = "https://jireh-8-hls-video-us-isp.dps.live/hls-video/c54ac2799874375c81c1672abb700870537c5223/ecuavisa/ecuavisa.smil/ecuavisa/livestream0/chunks.m3u8?dpssid=b213779049306a876e85ad00b&sid=ba5t1l1xb26747018686a876e85ad009&ndvc=1"
                )
            )
        ),
        Channel(
            id = "07",
            name = "Teleamazonas QUITO",
            sources = listOf(
                ChannelSource(
                    url = "https://teleamazonas-live.cdn.vustreams.com/live/fd4ab346-b4e3-4628-abf0-b5a1bc192428/live.isml/fd4ab346-b4e3-4628-abf0-b5a1bc192428.m3u8"
                )
            )
        ),
        Channel(
            id = "08",
            name = "TVC",
            sources = listOf(
                ChannelSource(
                    url = "https://d2m7i0pvomh4vg.cloudfront.net/ts:abr.m3u8"
                )
            )
        ),
        Channel(
            id = "08",
            name = "El Chavo del 8",
            sources = listOf(
                ChannelSource(
                    url = "https://live20.bozztv.com/giatvplayout7/giatv-211465/playlist.m3u8"
                )
            )
        ),
        Channel(
            id = "09",
            name = "Corazon TV",
            sources = listOf(
                ChannelSource(
                    url = "https://sistemastr.tropicalmoonmedia.com/live/7FFCFEC3978B68D1A2ED0A38DE96AF76/12.m3u8"
                )
            )
        )
    )

    var channels: List<Channel> = fallbackChannels
        private set

    suspend fun loadChannels(): Boolean = withContext(Dispatchers.IO) {
        val remoteChannels = runCatching {
            val json = downloadChannelsJson()
            parseChannels(json)
        }.getOrNull()

        val usedFallback = remoteChannels.isNullOrEmpty()

        channels = if (usedFallback) {
            fallbackChannels
        } else {
            remoteChannels
        }

        usedFallback
    }

    fun getFallbackChannels(): List<Channel> = fallbackChannels

    fun parseChannels(json: String): List<Channel> {
        val jsonArray = JSONArray(json)
        return (0 until jsonArray.length()).map { index ->
            val item = jsonArray.getJSONObject(index)
            Channel(
                id = item.getString("id"),
                name = item.getString("name"),
                sources = listOf(
                    ChannelSource(
                        url = item.getString("url"),
                        type = ChannelType.STREAM
                    )
                )
            )
        }
    }

    private fun downloadChannelsJson(): String {
        val connection = URL(CHANNELS_URL).openConnection() as HttpURLConnection
        connection.connectTimeout = 15_000
        connection.readTimeout = 15_000
        connection.requestMethod = "GET"
        connection.doInput = true

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("No se pudo obtener el archivo de canales: ${connection.responseCode}")
        }

        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
package com.example.tvaccesibleandroid.data

import android.widget.Toast
import com.example.tvaccesibleandroid.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ChannelsProvider {

    private const val CHANNELS_URL =
        "https://raw.githubusercontent.com/erihucor/tv-accesible-reto-ANDROID/feature/online-channel-prov/channels.json"

    fun getFallbackWarningMessage(): String =
        "No se pudieron obtener los canales en línea. Se están usando los canales de respaldo."

    private val fallbackChannels = listOf(
        Channel(
            id = "01",
            name = "Oromar",
            url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
        ),
        Channel(
            id = "02",
            name = "TC",
            url = "https://live2.eu-north-1b.cf.dmcdn.net/sec2(bax9kYcu8dVwF3gnUAt9H0g8DsBXfYPaN6t0sh-3ODyqwPl1JfZcKnL9iCDD62hffqwJNR6ewQ8U_2FA105LyrFUkq-zW3gUoWGilYn-LGmfJzyK0xtN219jcmVuhpcC)/cloud/3/x7wijay/s/live-720.m3u8"
        ),
        Channel(
            id = "03",
            name = "RTS",
            url = "https://d2w3o8zn50cs1k.cloudfront.net/ts:abr.m3u8"
        ),
        Channel(
            id = "04",
            name = "Ecuavisa GUAYAQUIL",
            url = "https://dai.google.com/linear/hls/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8"
        ),
        Channel(
            id = "05",
            name = "Ecuavisa QUITO",
            url = "http://45.171.108.253:8888/ECUAVISA/index.m3u8"
        ),
        Channel(
            id = "06",
            name = "Teleamazonas QUITO",
            url = "https://teleamazonas-live.cdn.vustreams.com/live/fd4ab346-b4e3-4628-abf0-b5a1bc192428/live.isml/fd4ab346-b4e3-4628-abf0-b5a1bc192428.m3u8"
        ),
        Channel(
            id = "07",
            name = "TVC",
            url = "https://d2m7i0pvomh4vg.cloudfront.net/ts:abr.m3u8"
        ),
        Channel(
            id = "08",
            name = "El Chavo del 8",
            url = "https://live20.bozztv.com/giatvplayout7/giatv-211465/playlist.m3u8"
        ),
        Channel(
            id = "09",
            name = "Corazon TV",
            url = "https://sistemastr.tropicalmoonmedia.com/live/7FFCFEC3978B68D1A2ED0A38DE96AF76/12.m3u8"
        )
    )

    var channels: List<Channel> = fallbackChannels
        private set

    suspend fun refreshChannels(): List<Channel> = withContext(Dispatchers.IO) {
        val remoteChannels = runCatching {
            val json = downloadChannelsJson()
            parseChannels(json)
        }.getOrElse {
            fallbackChannels
        }

        channels = remoteChannels.ifEmpty { fallbackChannels }
        channels
    }

    fun getFallbackChannels(): List<Channel> = fallbackChannels

    fun parseChannels(json: String): List<Channel> {
        val jsonArray = JSONArray(json)
        return (0 until jsonArray.length()).map { index ->
            val item = jsonArray.getJSONObject(index)
            Channel(
                id = item.getString("id"),
                name = item.getString("name"),
                url = item.getString("url")
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
package com.example.tvaccesibleandroid.data

import android.content.res.AssetManager
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
        "https://raw.githubusercontent.com/erihucor/tv-accesible-reto-ANDROID/refs/heads/main/channels.json"
    private const val CHANNELS_ASSET = "channels.json"

    fun getFallbackWarningMessage(): String =
        "No se pudieron obtener los canales en línea. Se están usando los canales locales."

    var channels: List<Channel> = emptyList()
        private set

    suspend fun loadChannels(assets: AssetManager): Boolean = withContext(Dispatchers.IO) {
        val remoteChannels = runCatching {
            val json = downloadChannelsJson()
            parseChannels(json)
        }.getOrNull()

        val usedFallback = remoteChannels.isNullOrEmpty()

        channels = if (usedFallback) {
            loadLocalChannels(assets)
        } else {
            remoteChannels
        }

        usedFallback
    }

    suspend fun refreshChannels(): List<Channel> =
        withContext(Dispatchers.IO) {
            val remoteChannels = parseChannels(downloadChannelsJson())
            if (remoteChannels.isEmpty()) {
                throw IOException("El archivo remoto de canales está vacío.")
            }

            remoteChannels.also { updatedChannels ->
                channels = updatedChannels
            }
        }

    private fun loadLocalChannels(assets: AssetManager): List<Channel> =
        assets.open(CHANNELS_ASSET).bufferedReader().use { reader ->
            parseChannels(reader.readText()).also { localChannels ->
                require(localChannels.isNotEmpty()) { "El archivo local de canales está vacío." }
            }
        }

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
                        type = if (item.optString("type").equals("YOUTUBE", ignoreCase = true)) {
                            ChannelType.YOUTUBE
                        } else {
                            ChannelType.STREAM
                        }
                    )
                )
            )
        }
    }

    private fun downloadChannelsJson(): String {
        val connection = URL(CHANNELS_URL).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.useCaches = false
            connection.setRequestProperty("Cache-Control", "no-cache")

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException(
                    "No se pudo obtener el archivo de canales: ${connection.responseCode}"
                )
            }

            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
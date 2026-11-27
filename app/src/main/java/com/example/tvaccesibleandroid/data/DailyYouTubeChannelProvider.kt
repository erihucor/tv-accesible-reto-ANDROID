package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.BuildConfig
import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DailyYouTubeChannelProvider(
    private val searchTerm: String = DEFAULT_SEARCH_TERM,
    private val dailyVideoUrlProvider: suspend (String) -> String = Companion::searchVideoUrl
) : ChannelCatalogProvider {

    companion object {
        const val DEFAULT_SEARCH_TERM = "Samsung QLED 4K Demo Video : Dominic Nature"
        const val CHANNEL_ID = "video-del-dia"
        const val CHANNEL_NAME = "Video del dia"
        const val TEST_VIDEO_URL =
            "https://www.youtube.com/watch?v=x91MPoITQ3I"

        private const val YOUTUBE_SEARCH_URL =
            "https://www.googleapis.com/youtube/v3/search"

        suspend fun searchVideoUrl(searchTerm: String): String = withContext(Dispatchers.IO) {
            require(searchTerm.isNotBlank()) { "El término de búsqueda no puede estar vacío." }
            require(BuildConfig.YOUTUBE_API_KEY.isNotBlank()) {
                "Falta configurar YOUTUBE_API_KEY en local.properties."
            }

            val encodedQuery = URLEncoder.encode(searchTerm, "UTF-8")
            val encodedApiKey = URLEncoder.encode(BuildConfig.YOUTUBE_API_KEY, "UTF-8")
            val requestUrl = URL(
                "$YOUTUBE_SEARCH_URL?part=snippet&type=video&maxResults=1" +
                    "&q=$encodedQuery&key=$encodedApiKey"
            )
            val connection = requestUrl.openConnection() as HttpURLConnection
            try {
                connection.connectTimeout = 15_000
                connection.readTimeout = 15_000
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.useCaches = false

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException(
                        "La búsqueda de YouTube falló: HTTP ${connection.responseCode}."
                    )
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                parseFirstVideoUrl(response)
            } finally {
                connection.disconnect()
            }
        }

        internal fun parseFirstVideoUrl(response: String): String {
            val items = JSONObject(response).getJSONArray("items")
            if (items.length() == 0) {
                throw IOException("No se encontraron videos para la búsqueda.")
            }

            val videoId = items.getJSONObject(0)
                .getJSONObject("id")
                .getString("videoId")
            return "https://www.youtube.com/watch?v=$videoId"
        }
    }

    override suspend fun loadChannels(): List<Channel> {
        val videoUrl = TEST_VIDEO_URL//dailyVideoUrlProvider(searchTerm).trim()
        if (videoUrl.isBlank()) {
            return emptyList()
        }

        return listOf(
            Channel(
                id = CHANNEL_ID,
                name = CHANNEL_NAME,
                sources = listOf(
                    ChannelSource(
                        url = videoUrl,
                        type = ChannelType.YOUTUBE
                    )
                )
            )
        )
    }
}
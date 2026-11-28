package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType

class BlnYouTubeChannelProvider(
    private val searchVideoUrl: suspend (String) -> String
) : ChannelCatalogProvider {

    companion object {
        const val BLN_SEARCH_TERM = "EN VIVO BLN: LA DINASTÍA"
        const val CHANNEL_ID = "00"
        const val CHANNEL_NAME = "Busqueda API Youtube"
    }

    suspend fun searchVideoUrlBLN(): String =
        searchVideoUrl(BLN_SEARCH_TERM)

    override suspend fun loadChannels(): List<Channel> {
        val videoUrl = searchVideoUrlBLN().trim()
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
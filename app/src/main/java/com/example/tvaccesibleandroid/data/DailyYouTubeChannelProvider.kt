package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType

class DailyYouTubeChannelProvider(
    private val dailyVideoUrlProvider: suspend () -> String = Companion::getDailyVideoUrl
) : ChannelCatalogProvider {

    companion object {
        const val CHANNEL_ID = "video-del-dia"
        const val CHANNEL_NAME = "Video del dia"
        const val TEST_VIDEO_URL =
            "https://www.youtube.com/watch?v=x91MPoITQ3I"

        suspend fun getDailyVideoUrl(): String = TEST_VIDEO_URL
    }

    override suspend fun loadChannels(): List<Channel> {
        val videoUrl = dailyVideoUrlProvider().trim()
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
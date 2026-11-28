package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType
import org.junit.Assert.assertEquals
import org.junit.Test

private const val TEST_VIDEO_URL = "https://www.youtube.com/watch?v=x91MPoITQ3I"

class ChannelsProviderTest {

    @Test
    fun parseChannels_shouldMapJsonArrayToChannels() {
        val json = """
            [
              {"id":"01","name":"Oromar","url":"https://example.com/oromar.m3u8"},
              {"id":"02","name":"TC","url":"https://example.com/tc.m3u8","type":"youtube"}
            ]
        """.trimIndent()

        val result = ChannelsProvider.parseChannels(json)


        assertEquals(
            listOf(
                Channel("01", "Oromar",
                    listOf(ChannelSource(url = "https://example.com/oromar.m3u8"))),
                Channel("02", "TC",
                    listOf(ChannelSource(url = "https://example.com/tc.m3u8", type = ChannelType.YOUTUBE)))),
            result
        )
    }

    @Test
    fun fallbackWarningMessage_shouldExplainOfflineFallback() {
        assertEquals(
            "No se pudieron obtener los canales en línea. Se están usando los canales locales.",
            ChannelsProvider.getFallbackWarningMessage()
        )
    }

    @Test
    fun dailyProvider_shouldSearchBLNAndCreateYoutubeChannelWithStableId() {
        var receivedSearchTerm: String? = null
        val provider = BlnYouTubeChannelProvider(
            searchVideoUrl = { searchTerm ->
                receivedSearchTerm = searchTerm
                TEST_VIDEO_URL
            }
        )

        val channel = kotlinx.coroutines.runBlocking {
            provider.loadChannels().single()
        }

        assertEquals(BlnYouTubeChannelProvider.BLN_SEARCH_TERM, receivedSearchTerm)
        assertEquals(BlnYouTubeChannelProvider.CHANNEL_ID, channel.id)
        assertEquals(ChannelType.YOUTUBE, channel.primarySource?.type)
        assertEquals(TEST_VIDEO_URL, channel.url)
    }

    @Test
    fun searchVideoUrlBLN_shouldUseSpecificSearchTerm() {
        var receivedSearchTerm: String? = null
        val provider = BlnYouTubeChannelProvider(
            searchVideoUrl = { searchTerm ->
                receivedSearchTerm = searchTerm
                TEST_VIDEO_URL
            }
        )

        val result = kotlinx.coroutines.runBlocking {
            provider.searchVideoUrlBLN()
        }

        assertEquals(BlnYouTubeChannelProvider.BLN_SEARCH_TERM, receivedSearchTerm)
        assertEquals(TEST_VIDEO_URL, result)
    }

    @Test
    fun parseFirstVideoUrl_shouldReturnYoutubeUrlForFirstSearchResult() {
        val response = """
            {"items":[{"id":{"kind":"youtube#video","videoId":"abc123"}}]}
        """.trimIndent()

        assertEquals(
            "https://www.youtube.com/watch?v=abc123",
            YouTubeSearchClient.parseFirstVideoUrl(response)
        )
    }

    @Test(expected = java.io.IOException::class)
    fun parseFirstVideoUrl_shouldFailWhenSearchHasNoResults() {
        YouTubeSearchClient.parseFirstVideoUrl("""{"items":[]}""")
    }

    @Test
    fun channelRepository_shouldAppendAndReplaceDynamicChannels() {
        val baseChannel = Channel(
            "01",
            "Oromar",
            listOf(ChannelSource("https://example.com/live.m3u8"))
        )
        val dynamicChannel = Channel(
            BlnYouTubeChannelProvider.CHANNEL_ID,
            BlnYouTubeChannelProvider.CHANNEL_NAME,
            listOf(
                ChannelSource(
                    TEST_VIDEO_URL,
                    ChannelType.YOUTUBE
                )
            )
        )
        val repository = ChannelRepository(emptyList())

        val result = repository.mergeChannels(listOf(baseChannel), listOf(dynamicChannel))

        assertEquals(listOf(baseChannel, dynamicChannel), result)
        val updatedDynamicChannel = dynamicChannel.copy(name = "Video actualizado")
        assertEquals(
            updatedDynamicChannel,
            repository.mergeChannels(result, listOf(updatedDynamicChannel)).last()
        )
    }
}

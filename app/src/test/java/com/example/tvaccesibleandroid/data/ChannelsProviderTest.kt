package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType
import org.junit.Assert.assertEquals
import org.junit.Test

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
            "No se pudieron obtener los canales en línea. Se están usando los canales de respaldo.",
            ChannelsProvider.getFallbackWarningMessage()
        )
    }
}

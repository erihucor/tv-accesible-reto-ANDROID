package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelSource
import com.example.tvaccesibleandroid.model.ChannelType

object ChannelsProvider {

    val channels = listOf(
        Channel(
            id = "yt-demo",
            name = "YouTube Demo",
            sources = listOf(
                ChannelSource(
                    url = "https://www.youtube.com/watch?v=V_g_Ut0jO-0",
                    type = ChannelType.YOUTUBE
                )
            )
        ),
        Channel(
            id = "yt-live-demo",
            name = "YouTube Live Demo - Mult",
            sources = listOf(
                ChannelSource(
                    url = "https://www.youtube.com/@NASA/live",
                    type = ChannelType.YOUTUBE
                ),
                ChannelSource(
                    url = "https://www.youtube.com/watch?v=922OvWh7JRU",
                    type = ChannelType.YOUTUBE
                )
            )
        ),
        Channel(
            id = "01",
            name = "Ecuavisa QUITO",
            sources = listOf(
                ChannelSource(
                    url = "http://45.171.108.253:8888/ECUAVISA/index.m3u8"
                )
            )
        ),
        Channel(
            id = "02",
            name = "Ecuavisa GUAYAQUIL - Mult",
            sources = listOf(
                ChannelSource(
                    url = "https://dai.google.com/linear/hls/pa/event/GyPkTVDZSXGhpOvxPK7m2g/stream/6369e14d-0957-4630-9351-691650150bdb:MRN2/master.m3u8"
                ),
                ChannelSource(
                    url = "http://177.234.249.178:8888/ECUAVISA/index.m3u8"
                )
            )
        ),
        Channel(
            id = "02-test",
            name = "Test STREAM - YOUTUBE - Mult",
            sources = listOf(
                ChannelSource(
                    url = "https://dai.google.com/linear/hls/pa/event/GyPkTVDZSXGhpOvxPK7m2g/stream/6369e14d-0957-4630-9351-691650150bdb:MRN2/master.m3u8"
                ),
                ChannelSource(
                    url = "https://www.youtube.com/watch?v=cfLry1eQ-oo",
                    type = ChannelType.YOUTUBE
                )
            )
        ),
        Channel(
            id = "03",
            name = "Oromar",
            sources = listOf(
                ChannelSource(
                    url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
                )
            )
        ),
        Channel(
            id = "04",
            name = "RTS",
            sources = listOf(
                ChannelSource(
                    url = "http://157.100.248.242:8080/RTSHD/index.m3u8"
                )
            )
        ),
        Channel(
            id = "05",
            name = "Teleamazonas QUITO",
            sources = listOf(
                ChannelSource(
                    url = "http://157.100.248.242:8080/TeleamazonasHD/index.m3u8"
                )
            )
        ),
        Channel(
            id = "06",
            name = "TC",
            sources = listOf(
                ChannelSource(
                    url = "http://45.171.108.253:8888/TC/index.m3u8"
                )
            )
        ),
        Channel(
            id = "07",
            name = "El Chavo del 8",
            sources = listOf(
                ChannelSource(
                    url = "https://live20.bozztv.com/giatvplayout7/giatv-211465/playlist.m3u8"
                )
            )
        ),
        Channel(
            id = "08",
            name = "Corazon TV",
            sources = listOf(
                ChannelSource(
                    url = "https://sistemastr.tropicalmoonmedia.com/live/7FFCFEC3978B68D1A2ED0A38DE96AF76/12.m3u8"
                )
            )
        ),
        Channel(
            id = "test",
            name = "Video de Prueba",
            sources = listOf(
                ChannelSource(
                    url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
                )
            )
        )
    )

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
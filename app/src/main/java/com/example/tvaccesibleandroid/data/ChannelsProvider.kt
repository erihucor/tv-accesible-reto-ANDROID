package com.example.tvaccesibleandroid.data
import com.example.tvaccesibleandroid.model.Channel
import com.example.tvaccesibleandroid.model.ChannelType

object ChannelsProvider {

    val channels = listOf(
        Channel(
            id = "yt-live-demo",
            name = "YouTube Live Demo",
            url = "https://www.youtube.com/watch?v=IskkmNPJdUk",
            type = ChannelType.YOUTUBE
        ),
        Channel(
            id = "yt-demo",
            name = "YouTube Demo",
            url = "https://www.youtube.com/watch?v=8dA2yVsUO0c",
            type = ChannelType.YOUTUBE
        ),
        Channel(
            id = "01",
            name = "Ecuavisa QUITO",
            url = "http://45.171.108.253:8888/ECUAVISA/index.m3u8"
            //url = "https://origin.dpsgo.com/ssai/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8"
        ),
        Channel(
            id = "02",
            name = "Ecuavisa GUAYAQUIL",
            url = "https://dai.google.com/linear/hls/pa/event/GyPkTVDZSXGhpOvxPK7m2g/stream/6369e14d-0957-4630-9351-691650150bdb:MRN2/master.m3u8"
        ),
        Channel(
            id = "03",
            name = "Oromar",
            url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
        ),
        Channel(
            id = "04",
            name = "RTS",
            url = "http://157.100.248.242:8080/RTSHD/index.m3u8"
            //url = "https://d2qsan2ut81n2k.cloudfront.net/live/72a3661e-1019-45f8-af10-af59f6ef6222/ts:abr.m3u8"
        ),
        Channel(
            id = "05",
            name = "Teleamazonas QUITO",
            url = "http://157.100.248.242:8080/TeleamazonasHD/index.m3u8"
            //url = "https://teleamazonas-live.cdn.vustreams.com/live/fd4ab346-b4e3-4628-abf0-b5a1bc192428/live.isml/fd4ab346-b4e3-4628-abf0-b5a1bc192428.m3u8"
        ),
        Channel(
            id = "06",
            name = "TC",
            url = "http://45.171.108.253:8888/TC/index.m3u8"
        ),
        Channel(
            id = "07",
            name = "El Chavo del 8",
            url = "https://live20.bozztv.com/giatvplayout7/giatv-211465/playlist.m3u8"
        ),
        Channel(
            id = "08",
            name = "Corazon TV",
            url = "https://sistemastr.tropicalmoonmedia.com/live/7FFCFEC3978B68D1A2ED0A38DE96AF76/12.m3u8"
        ),
        Channel(
            id = "test",
            name = "Video de Prueba",
            url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        )
    )

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
package com.example.tvaccesibleandroid.data
import com.example.tvaccesibleandroid.model.Channel

object ChannelsProvider {

    val channels = listOf(
        Channel(
            id = "03",
            name = "Oromar",
            url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
        ),
        Channel(
            id = "06",
            name = "TC",
            url = "http://45.171.108.253:8888/TC/index.m3u8"
        ),
        Channel(
            id = "04",
            name = "RTS",
            url = "http://157.100.248.242:8080/RTSHD/index.m3u8"
        ),
        Channel(
            id = "02",
            name = "Ecuavisa GUAYAQUIL",
            url = "https://dai.google.com/linear/hls/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8"
        ),
        Channel(
            id = "05",
            name = "Teleamazonas QUITO",
            url = "http://157.100.248.242:8080/TeleamazonasHD/index.m3u8"
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
            id = "01",
            name = "Ecuavisa QUITO",
            url = "http://45.171.108.253:8888/ECUAVISA/index.m3u8"
        )
    )

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
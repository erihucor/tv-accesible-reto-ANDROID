package com.example.tvaccesibleandroid.data
import com.example.tvaccesibleandroid.model.Channel

object ChannelsProvider {

    val channels = listOf(
        Channel(
            id = "01",
            name = "Oromar",
            url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
        ),
        Channel(
            id = "02",
            name = "TC",
            url = "https://live2.eu-north-1a.cf.dmcdn.net/sec2(IXMzOyIDjTp0cn0LEALPhSyzfPXypZMy_cCtsWfydbqvD6iLGk4lJKEHHv4Ym-4J8xLUZZ86j647XR9oVPa3Zng_RJJZlqUnR7QmRrfQu7UIfQLV2TQBZOBP-dMpjTAa)/cloud/3/x7wijay/s/live-720.m3u8"
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
            url = "https://teleamazonas-live.cdn.vustreams.com/live/fd4ab346-b4e3-4628-abf0-b5a1bc192428/live.isml/playlist.m3u8"
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

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
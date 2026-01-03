package com.example.tvaccesibleandroid.data
import com.example.tvaccesibleandroid.model.Channel

object ChannelsProvider {

    val channels = listOf(
        Channel(
            id = "ecuavisa",
            name = "Ecuavisa",
            url = "https://origin.dpsgo.com/ssai/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8"
        ),
        Channel(
            id = "102",
            name = "Oromar",
            url = "https://stream.oromar.tv/hls/oromartv_hi/index.m3u8"
        ),
        Channel(
            id = "103",
            name = "RTS",
            url = "https://d2qsan2ut81n2k.cloudfront.net/live/72a3661e-1019-45f8-af10-af59f6ef6222/ts:abr.m3u8"
        ),
        Channel(
            id = "104",
            name = "Teleamazonas Quito",
            url = "https://teleamazonas-live.cdn.vustreams.com/live/fd4ab346-b4e3-4628-abf0-b5a1bc192428/live.isml/fd4ab346-b4e3-4628-abf0-b5a1bc192428.m3u8"
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
package com.example.tvaccesibleandroid.data
import com.example.tvaccesibleandroid.model.Channel

object ChannelsProvider {

    val channels = listOf(
        Channel(
            id = "ecuavisa",
            nombre = "Ecuavisa",
            url = "https://origin.dpsgo.com/ssai/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8"
        ),
        Channel(
            id = "test",
            nombre = "Video de Prueba",
            url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        )
    )

    fun getById(id: String): Channel? =
        channels.find { it.id == id }
}
package com.example.tvaccesibleandroid.model

enum class ChannelType {
    STREAM,
    YOUTUBE
}

data class Channel(
    val id: String,
    val name: String,
    val url: String,
    val type: ChannelType = ChannelType.STREAM
)

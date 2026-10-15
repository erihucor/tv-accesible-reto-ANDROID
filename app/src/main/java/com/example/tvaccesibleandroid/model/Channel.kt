package com.example.tvaccesibleandroid.model

enum class ChannelType {
    STREAM,
    YOUTUBE
}

data class ChannelSource(
    val url: String,
    val type: ChannelType = ChannelType.STREAM
)

data class Channel(
    val id: String,
    val name: String,
    val sources: List<ChannelSource>
) {
    val primarySource: ChannelSource? = sources.firstOrNull()
    val url: String? = primarySource?.url
    val type: ChannelType = primarySource?.type ?: ChannelType.STREAM
}

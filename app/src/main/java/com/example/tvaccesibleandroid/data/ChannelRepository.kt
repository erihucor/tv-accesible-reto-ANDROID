package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel

class ChannelRepository(
    private val providers: List<ChannelCatalogProvider>
) {
    suspend fun merge(baseChannels: List<Channel>): List<Channel> {
        val dynamicChannels = providers
            .flatMap { provider ->
                try {
                    provider.loadChannels()
                } catch (_: Exception) {
                    emptyList()
                }
            }

        return mergeChannels(baseChannels, dynamicChannels)
    }

    fun mergeChannels(
        baseChannels: List<Channel>,
        dynamicChannels: List<Channel>
    ): List<Channel> {
        val merged = baseChannels.toMutableList()

        dynamicChannels.forEach { dynamicChannel ->
            val existingIndex = merged.indexOfFirst { channel ->
                channel.id == dynamicChannel.id
            }
            if (existingIndex >= 0) {
                merged[existingIndex] = dynamicChannel
            } else {
                merged.add(dynamicChannel)
            }
        }

        return merged
    }
}
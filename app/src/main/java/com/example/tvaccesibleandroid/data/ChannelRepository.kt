package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel
import kotlinx.coroutines.CancellationException

class ChannelRepository(
    private val providers: List<ChannelCatalogProvider>
) {
    suspend fun merge(
        baseChannels: List<Channel>,
        onProviderError: (ChannelCatalogProvider, Exception) -> Unit
    ): List<Channel> {
        val dynamicChannels = providers
            .flatMap { provider ->
                try {
                    provider.loadChannels()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    onProviderError(provider, error)
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
package com.example.tvaccesibleandroid.data

import com.example.tvaccesibleandroid.model.Channel

interface ChannelCatalogProvider {
    suspend fun loadChannels(): List<Channel>
}
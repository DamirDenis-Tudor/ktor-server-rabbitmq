package com.mesh.kabbitMq.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class KabbitMQService(private val config: KabbitMQConfig) {

    private val connectionFactory = ConnectionFactory().apply { setUri(config.uri) }

    private val connectionCache = mutableMapOf<String, Connection>()

    private val channelCache = mutableMapOf<String, Channel>()

    fun getChannel(id: String = "DEFAULT", connectionId: String = "DEFAULT") =
        channelCache.getOrPut(id) { getConnection(connectionId).createChannel() }

    fun getConnection(id: String = "DEFAULT") =
        connectionCache.getOrPut(id) { connectionFactory.newConnection() }

    fun closeConnection(connectionId: String) {
        connectionCache[connectionId]?.close()
        connectionCache.remove(connectionId)
    }

    fun closeChannel(channelId: String) {
        channelCache[channelId]?.close()
        channelCache.remove(channelId)
    }
}
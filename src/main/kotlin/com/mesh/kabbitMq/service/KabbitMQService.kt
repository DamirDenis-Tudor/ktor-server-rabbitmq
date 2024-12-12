package com.mesh.kabbitMq.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class KabbitMQService(private val config: KabbitMQConfig) {

    private val connectionFactory = ConnectionFactory().apply { setUri(config.uri) }

    private val connection: Connection by lazy {
        connectionFactory.newConnection(config.connectionName)
    }

    private val channelCache = mutableMapOf<String, Channel>()

    fun getChannel(id: String = KabbitMQService::class.simpleName!!) =
        channelCache.getOrPut(id) { connection.createChannel() }

    fun close() = connection.close()
}
package com.mesh.kabbitMq.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

/**
 * Service class that manages RabbitMQ connections and channels.
 * This class handles creating, caching, and managing RabbitMQ connections and channels.
 *
 * @param config the configuration to be used for creating connections.
 *
 * @author Damir Denis-Tudor
 * @version 0.1.0
 */
class KabbitMQService(private val config: KabbitMQConfig) {
    private val connectionFactory = ConnectionFactory().apply { setUri(config.uri) }

    private val connectionCache = mutableMapOf<String, Connection>()
    private val channelCache = mutableMapOf<String, Channel>()

    /**
     * Retrieves a RabbitMQ channel by its ID. If the channel is not cached or is closed, a new one is created.
     *
     * @param id the ID of the channel to retrieve, defaults to "DEFAULT".
     * @param connectionId the ID of the connection to use, defaults to "DEFAULT".
     * @return the requested RabbitMQ channel.
     */
    fun getChannel(id: String = "DEFAULT", connectionId: String = "DEFAULT"): Channel {
        val channel = channelCache.getOrPut(id) { getConnection(connectionId).createChannel() }

        if (!channel.isOpen) {
            channelCache.remove(id)
            channelCache.getOrPut(id) { getConnection(connectionId).createChannel() }
        }

        return channel
    }

    /**
     * Retrieves a RabbitMQ connection by its ID. If the connection is not cached or is closed, a new one is created.
     *
     * @param id the ID of the connection to retrieve, defaults to "DEFAULT".
     * @return the requested RabbitMQ connection.
     */
    fun getConnection(id: String = "DEFAULT"): Connection {
        val connection = connectionCache.getOrPut(id) { connectionFactory.newConnection() }

        if (!connection.isOpen) {
            connectionCache.getOrPut(id) { connectionFactory.newConnection() }
        }

        return connection
    }

    /**
     * Closes a RabbitMQ connection by its ID and removes it from the connection cache.
     *
     * @param connectionId the ID of the connection to close and remove.
     */
    fun closeConnection(connectionId: String) {
        connectionCache[connectionId]?.close()
        connectionCache.remove(connectionId)
    }

    /**
     * Closes a RabbitMQ channel by its ID and removes it from the channel cache.
     *
     * @param channelId the ID of the channel to close and remove.
     */
    fun closeChannel(channelId: String) {
        channelCache[channelId]?.close()
        channelCache.remove(channelId)
    }
}

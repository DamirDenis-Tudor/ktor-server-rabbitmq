package com.mesh.kabbitMq.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.lang.Thread.sleep

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
    private val connectionFactory = ConnectionFactory().apply {
        setUri(config.uri)
    }

    private val connectionCache = mutableMapOf<String, Connection>()
    private val channelCache = mutableMapOf<String, Channel>()

    /**
     * Retries a block of code for a specified number of attempts with a delay between retries.
     *
     * @param block The block of code to execute. If it succeeds, the result will be returned.
     * @return The result of the block if it succeeds.
     * @throws  IllegalStateException if the block fails after the maximum number of retry attempts.
     */
    private fun <T> retry(block: () -> T): T {
        repeat(config.connectionAttempts) { index ->
            runCatching { block() }
                .onSuccess {
                    return@retry it
                }.onFailure {
                    println("Attempt ${index + 1} failed: ${it.message}.")
                    sleep(config.attemptDelay * 1000L)
                }
        }

        error("Failed after ${config.connectionAttempts} retries")
    }

    /**
     * Retrieves a RabbitMQ connection by its ID. If the connection is not cached or is closed, a new one is created.
     *
     * @param id the ID of the connection to retrieve, defaults to "DEFAULT".
     * @return the requested RabbitMQ connection.
     */
    fun getConnection(id: String = "DEFAULT"): Connection = retry {
        val connection = connectionCache.getOrPut(id) {
            connectionFactory.newConnection()
        }

        if (!connection.isOpen)
            error("Connection $id is not open.")

        return@retry connection
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
     * Closes a RabbitMQ channel by its ID and removes it from the channel cache.
     *
     * @param channelId the ID of the channel to close and remove.
     */
    fun closeChannel(channelId: String) {
        channelCache[channelId]?.close()
        channelCache.remove(channelId)
    }
}

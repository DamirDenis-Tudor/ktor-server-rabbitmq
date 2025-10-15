package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Connection
import io.ktor.util.collections.*
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class ConnectionManager {

    protected val logger = KtorSimpleLogger(this::class.simpleName ?: "ConnectionManager")

    abstract val dispatcher: CoroutineDispatcher
    abstract val coroutineScope: CoroutineScope
    abstract val configuration: ConnectionConfig

    protected val channelCache = ConcurrentMap<String, Channel>()
    protected val connectionCache = ConcurrentMap<String, Connection>()

    protected val connectionMutex = Mutex()
    protected val channelMutex = Mutex()

    /**
     * Retries a block of code a specified number of times with delays between attempts.
     *
     * @param block The block of code to execute. If it succeeds, the result is returned.
     * @return The result of the block if it succeeds.
     * @throws IllegalStateException if the block fails after the maximum number of retries.
     */
    abstract suspend fun <T> retry(block: suspend () -> T): T

    /**
     * Retrieves the ID of a connection from the cache.
     *
     * @param connection The RabbitMQ connection to identify.
     * @return The associated ID or the default connection name if not found.
     */
    fun getConnectionId(connection: Connection): String =
        connectionCache.entries.find { it.value == connection }?.key ?: configuration.defaultConnectionName

    /**
     * Retrieves or creates a RabbitMQ connection by its ID.
     *
     * If the connection is not found in the cache or is closed, a new connection is created.
     *
     * @param id the ID of the connection to retrieve. Defaults to the default connection name.
     * @return the RabbitMQ connection.
     */
    abstract suspend fun getConnection(id: String = configuration.defaultConnectionName): Connection

    /**
     * Closes and removes a RabbitMQ connection by its ID.
     *
     * @param connectionId the ID of the connection to close.
     */
    suspend fun closeConnection(connectionId: String) = connectionMutex.withLock {
        connectionCache[connectionId]?.close()
        connectionCache.remove(connectionId)

        logger.debug("Connection with id: <$connectionId>, closed")
    }

    /**
     * Generates a unique key for identifying a channel within the channel cache.
     *
     * @param connectionId the ID of the connection.
     * @param channelId the ID of the channel.
     * @return a unique string key for the channel.
     */
    protected fun getChannelKey(connectionId: String, channelId: Int): String =
        "$connectionId-channel-$channelId"

    /**
     * Retrieves or creates a RabbitMQ channel by its ID.
     *
     * If the channel is not found in the cache or is closed, a new one is created.
     *
     * @param channelId the ID of the channel to retrieve. Defaults to 1.
     * @param connectionId the ID of the connection to use. Defaults to the default connection name.
     * @return the RabbitMQ channel.
     */
    suspend fun getChannel(
        channelId: Int = 1,
        connectionId: String = configuration.defaultConnectionName,
    ): Channel = channelMutex.withLock {
        retry {
            val id = getChannelKey(connectionId, channelId)

            if (channelCache.containsKey(id)) logger.debug("Channel with id: <$id> will be taken from cache.")

            val channel = channelCache.getOrPut(id) {
                logger.debug("Creating new channel with id <$channelId> for connection with id <$connectionId>.")
                getConnection(connectionId).createChannel()
                    ?: error("Could not allocate this channel id <$channelId>. ")
            }

            if (!channel.isOpen) {
                channelCache.remove(id)
                error("Channel <$channelId> is not open. ${channel.closeReason}")
            }

            return@retry channel
        }
    }

    /**
     * Closes and removes a RabbitMQ channel by its ID.
     *
     * @param channelId the ID of the channel to close.
     * @param connectionId the ID of the associated connection.
     */
    suspend fun closeChannel(
        channelId: Int = 1,
        connectionId: String = configuration.defaultConnectionName,
    ) = channelMutex.withLock {
        val id = getChannelKey(connectionId, channelId)

        channelCache[id]?.close()
        channelCache.remove(id)

        logger.debug("Channel with id: <$channelId> for connection with id <$connectionId>, closed")
    }

    /**
     * Closes all active RabbitMQ connections.
     *
     * This method iterates through all connections in the connection cache and closes each one.
     */
    suspend fun close() = connectionMutex.withLock {
        connectionCache.values.forEach { connection -> connection.close() }
    }

}

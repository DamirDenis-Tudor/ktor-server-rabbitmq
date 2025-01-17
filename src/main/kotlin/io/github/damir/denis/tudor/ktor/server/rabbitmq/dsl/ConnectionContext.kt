package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Represents a context for a RabbitMQ connection, allowing for scoped operations
 * on channels and managing resources associated with the connection.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
class ConnectionContext(val connectionManager: ConnectionManager, val connection: Connection)

/**
 * Provides a DSL extension to create or retrieve a specific channel within the connection context,
 * execute a given block of operations, and optionally close the channel after execution.
 *
 * @param id The unique ID of the channel to be retrieved or created.
 * @param autoClose Whether the channel should be automatically closed after the block is executed.
 * @param block The block of operations to execute within the channel context.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend inline fun ConnectionContext.channel(
    id: Int,
    autoClose: Boolean = false,
    crossinline block: suspend ChannelContext.() -> Unit
) = runCatching {
    with(connectionManager) {
        withContext(connectionManager.dispatcher) {
            getChannel(id, getConnectionId(connection)).also {
                coroutineScope.launch(dispatcher) {
                    it.also { ChannelContext(connectionManager, it).apply { block() } }
                }.let { job ->
                    if (autoClose) {
                        job.join()
                        closeChannel(id)
                    }
                }
            }
        }
    }
}

/**
 * Provides a DSL extension to create a new channel within the connection context,
 * execute a given block of operations, and optionally close the channel after execution.
 *
 * @param autoClose Whether the channel should be automatically closed after the block is executed.
 * @param block The block of operations to execute within the channel context.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend inline fun ConnectionContext.channel(
    autoClose: Boolean = false,
    crossinline block: suspend ChannelContext.() -> Unit
) = runCatching {
    with(connectionManager) {
        withContext(connectionManager.dispatcher) {
            val connectionId = getConnectionId(connection)
            val channelId = Random.nextInt(1000, 5000)
            connectionManager.getChannel(channelId, connectionId).also {
                coroutineScope.launch(dispatcher) {
                    it.also { ChannelContext(connectionManager, it).apply { block() } }
                }.let { job ->
                    if (autoClose) {
                        job.join()
                        closeChannel(channelId)
                    }
                }
            }
        }
    }
}

/**
 * Provides a DSL extension to create a new channel within the connection context,
 * execute a given suspending block directly on the channel, and optionally close the channel after execution.
 *
 * @param autoClose Whether the channel should be automatically closed after the block is executed.
 * @param block The suspending block of operations to execute directly on the channel.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend inline fun ConnectionContext.libChannel(
    autoClose: Boolean = false,
    crossinline block: suspend Channel.() -> Unit
) = runCatching {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(1000, 5000)
        connectionManager.getChannel(channelId, connectionId).also {
            getChannel(channelId, connectionId).also {
                coroutineScope.launch(dispatcher) {
                    it.apply { block() }
                }.let { job ->
                    if (autoClose) {
                        job.join()
                        closeChannel(channelId)
                    }
                }
            }
        }
    }
}
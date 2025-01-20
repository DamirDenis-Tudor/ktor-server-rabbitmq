package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Represents a context for a RabbitMQ connection, allowing for scoped operations
 * on channels and managing resources associated with the connection.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
class ConnectionContext(
    override val connectionManager: ConnectionManager,
    val connection: Connection,
    defaultChannel: Channel,
) : ChannelContext(
    connectionManager = connectionManager,
    channel = defaultChannel
)

/**
 * Executes a block of code within the `default channel context of current connection by launching a coroutine`.
 *
 * This function allows operations to be performed directly on the default channel of current connection provided by the `ConnectionManager`.
 *
 * Essentially, there is one difference between the following code snippets (`in the second example a coroutine is launched`):
 *
 * ```kotlin
 * rabbitmq {
 *      connection(id = "consume") {
 *          basicConsume {
 *              autoAck = true
 *              queue = "demo-queue"
 *              deliverCallback<String> { tag, message ->
 *                  logger.info("Received message: $message")
 *              }
 *          }
 *      }
 * }
 *
 * rabbitmq {
 *      connection(id = "consume") {
 *          channel{
 *              basicConsume {
 *                  autoAck = true
 *                  queue = "demo-queue"
 *                  deliverCallback<String> { tag, message ->
 *                      logger.info("Received message: $message")
 *                  }
 *              }
 *          }
 *      }
 * }
 * ```
 *
 * @param block A suspendable block of code to execute within the `ChannelContext`.
 *
 * @author Damir Denis-Tudor
 * @since 1.3.3
 */
@RabbitDslMarker
fun ConnectionContext.channel(
    block: suspend ChannelContext.() -> Unit
) = runCatching {
    with(connectionManager) {
        getChannel(connectionId = getConnectionId(connection)).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                ChannelContext(connectionManager, it).apply { block() }
            }
        }
    }
}

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
        getChannel(id, getConnectionId(connection)).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.also { ChannelContext(connectionManager, it).apply { block() } }
            }.let { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(id, getConnectionId(connection))
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
    id: Int,
    autoClose: Boolean = false,
    crossinline block: suspend Channel.() -> Unit
) = runCatching {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = id
        getChannel(channelId, connectionId).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.apply { block() }
            }.let { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(channelId, getConnectionId(connection))
                }
            }
        }
    }
}
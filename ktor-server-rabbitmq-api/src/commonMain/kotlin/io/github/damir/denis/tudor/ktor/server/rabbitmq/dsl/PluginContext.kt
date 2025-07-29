package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The PluginContext is used to manage actions within a RabbitMQ context.
 * It extends the `ChannelContext` because `ConnectionsManager` handles the default connection and channel.
 * Therefore, all operations can be performed within the default channel.
 *
 * Additionally, this context includes extensions that allow the creation of channels and connections
 * in various scopes. For example, you can create direct connections and channels using the Java library,
 * or you can choose to interact with the context provided by this library (`ChannelContext`, `ConnectionContext`).
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
class PluginContext(
    override val connectionManager: ConnectionManager,
    channel: Channel,
) : ChannelContext(
    connectionManager = connectionManager,
    channel = channel,
)

/**
 * Creates a `PluginContext` using the default connection and channel from the `ConnectionManager`.
 */
suspend fun PluginContext(
    connectionManager: ConnectionManager,
) = PluginContext(
    connectionManager = connectionManager,
    channel = connectionManager.getChannel()
)

/**
 * Executes a block of code within the `default channel context of the default connection by launching a coroutine`.
 *
 * This function allows operations to be performed directly on the default channel provided by the `ConnectionManager`.
 *
 * Essentially, there is one difference between the following code snippets (`in the second example a coroutine is launched`):
 *
 * ```kotlin
 * rabbitmq {
 *     basicPublish {
 *         routingKey = "..."
 *         exchange = "..."
 *         message { "..." }
 *     }
 * }
 *
 * rabbitmq {
 *     channel {
 *         basicPublish {
 *             routingKey = "..."
 *             exchange = "..."
 *             message { "..." }
 *         }
 *     }
 * }
 * ```
 *
 * @param block A suspendable block of code to execute within the `ChannelContext`.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun PluginContext.channel(
    block: suspend ChannelContext.() -> Unit,
) = with(connectionManager) {
    getChannel().also {
        coroutineScope.launch(Dispatchers.rabbitMQ) {
            ChannelContext(
                connectionManager = connectionManager,
                channel = it
            ).apply { block() }
        }
    }
}

/**
 * Executes a block of code within a specific channel context.
 *
 * This function allows operations to be performed on a channel identified by its `id`.
 * Optionally, the channel can be automatically closed after execution.
 *
 * This extension ensures that when `autoClose = true`, the coroutine started for a specific job must be waited on to complete using `join`.
 *
 * @param id The ID of the channel to operate on.
 * @param autoClose Whether to close the channel automatically after execution.
 * @param block A suspendable block of code to execute within the `ChannelContext`.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun PluginContext.channel(
    id: Int,
    autoClose: Boolean = false,
    block: suspend ChannelContext.() -> Unit,
) = with(connectionManager) {
    getChannel(id).also {
        coroutineScope.launch(Dispatchers.rabbitMQ) {
            ChannelContext(
                connectionManager = connectionManager,
                channel = it
            ).apply { block() }
        }.let { job ->
            if (autoClose) {
                job.join()
                closeChannel(id)
            }
        }
    }
}

/**
 * Executes a block of code directly on a RabbitMQ `Channel`.
 *
 * This function provides direct access to a channel identified by its `id` for advanced operations.
 * Optionally, the channel can be automatically closed after execution.
 *
 * This extension ensures that when `autoClose = true`, the coroutine started for a specific job must be waited on to complete using `join`.
 *
 * @param id The ID of the channel to operate on.
 * @param autoClose Whether to close the channel automatically after execution.
 * @param block A suspendable block of code to execute on the `Channel`.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun PluginContext.libChannel(
    id: Int,
    autoClose: Boolean = false,
    block: suspend Channel.() -> Unit,
) = with(connectionManager) {
    getChannel(id).also {
        coroutineScope.launch(Dispatchers.rabbitMQ) {
            it.apply { block() }
        }.let { job ->
            if (autoClose) {
                job.join()
                closeChannel(id)
            }
        }
    }
}

/**
 * Executes a block of code within a specific connection context. A default channel will be allocated
 * in order to enhance the usability
 *
 * This function allows operations to be performed on a connection identified by its `id`.
 * Optionally, the connection can be automatically closed after execution.
 *
 * This extension ensures that when `autoClose = true`, the coroutine started for a specific job must be waited on to complete using `join`.
 *
 * @param id The ID of the connection to operate on.
 * @param autoClose Whether to close the connection automatically after execution.
 * @param block A suspendable block of code to execute within the `ConnectionContext`.
 *
 * @author Damir Denis-Tudor
 * @since 1.3.3
 */
@RabbitDslMarker
suspend fun PluginContext.connection(
    id: String,
    autoClose: Boolean = false,
    block: suspend ConnectionContext.() -> Unit,
) = with(connectionManager) {
    getConnection(id).also {
        coroutineScope.launch(Dispatchers.rabbitMQ) {
            ConnectionContext(
                connectionManager = connectionManager,
                connection = it,
                defaultChannel = getChannel(connectionId = id)
            ).apply { block() }
        }.let { job ->
            if (autoClose) {
                job.join()
                closeConnection(id)
            }
        }
    }
}

/**
 * Executes a block of code directly on a RabbitMQ `Connection`.
 *
 * This function provides direct access to a connection identified by its `id` for advanced operations.
 * Optionally, the connection can be automatically closed after execution.
 *
 * This extension ensures that when `autoClose = true`, the coroutine started for a specific job must be waited on to complete using `join`.
 *
 * @param id The ID of the connection to operate on.
 * @param autoClose Whether to close the connection automatically after execution.
 * @param block A suspendable block of code to execute on the `Connection`.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.2
 */
@RabbitDslMarker
suspend fun PluginContext.libConnection(
    id: String,
    autoClose: Boolean = false,
    block: suspend Connection.() -> Unit,
) = with(connectionManager) {
    getConnection(id).also {
        coroutineScope.launch(Dispatchers.rabbitMQ) {
            it.apply { block() }
        }.let { job ->
            if (autoClose) {
                job.join()
                closeConnection(id)
            }
        }
    }
}

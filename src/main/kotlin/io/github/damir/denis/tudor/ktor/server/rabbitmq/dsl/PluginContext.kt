package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class PluginContext(
    override val connectionManager: ConnectionManager
) : ChannelContext(
    connectionManager,
    connectionManager.getChannel()
)

internal fun PluginContext.getChannelContext(channel: Channel): ChannelContext =
    ChannelContext(connectionManager, channel)

@RabbitDslMarker
fun PluginContext.channel(
    block: suspend ChannelContext.() -> Unit
): Channel {
    with(connectionManager) {
        return getChannel()
            .also {
                coroutineScope.launch(dispatcher) {
                    getChannelContext(it).apply { block() }
                }
            }
    }
}

@RabbitDslMarker
fun PluginContext.channel(
    id: Int,
    autoClose: Boolean = false,
    block: suspend ChannelContext.() -> Unit
): Channel {
    with(connectionManager) {
        return getChannel(id)
            .also {
                coroutineScope.launch(dispatcher) {
                    getChannelContext(it).apply { block() }
                    if (autoClose) closeChannel(id)
                }
            }
    }
}

@RabbitDslMarker
fun PluginContext.libChannel(
    id: Int,
    autoClose: Boolean = false,
    block: suspend Channel.() -> Unit
): Channel {
    with(connectionManager) {
        return getChannel(id)
            .also {
                coroutineScope.launch(dispatcher) {
                    it.apply { block() }
                    if (autoClose) closeChannel(id)
                }
            }
    }
}

@RabbitDslMarker
fun PluginContext.connection(
    id: String,
    autoClose: Boolean = false,
    block: suspend ConnectionContext.() -> Unit
): Connection {
    with(connectionManager) {
        return getConnection(id)
            .also {
                coroutineScope.launch(dispatcher) {
                    ConnectionContext(connectionManager, it).apply { block() }
                    if (autoClose) closeConnection(id)
                }
            }
    }
}

@RabbitDslMarker
fun PluginContext.libConnection(
    id: String,
    autoClose: Boolean = false,
    block: suspend Connection.() -> Unit
): Connection {
    with(connectionManager) {
        return getConnection(id)
            .also {
                coroutineScope.launch(dispatcher) {
                    it.apply { block() }
                    if (autoClose) closeConnection(id)
                }
            }
    }
}

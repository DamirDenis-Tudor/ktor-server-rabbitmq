package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Connection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher

abstract class ConnectionManager {

    abstract val dispatcher: ExecutorCoroutineDispatcher
    abstract val coroutineScope: CoroutineScope
    abstract val configuration: ConnectionConfig

    abstract fun getConnectionId(connection: Connection): String
    abstract suspend fun getConnection(id: String = configuration.defaultConnectionName): Connection
    abstract suspend fun closeConnection(connectionId: String)

    abstract suspend fun getChannel(
        channelId: Int = 1,
        connectionId: String = configuration.defaultConnectionName,
    ): Channel

    abstract suspend fun closeChannel(channelId: Int = 1, connectionId: String = configuration.defaultConnectionName)

    abstract suspend fun close()

}

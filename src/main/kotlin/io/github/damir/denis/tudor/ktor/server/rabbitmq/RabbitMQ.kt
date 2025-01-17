package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionConfig
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.properties.Delegates

val ConnectionManagerKey = AttributeKey<ConnectionManager>("ConnectionManager")

val RabbitMQ = createApplicationPlugin(
    name = "RabbitMQ",
    configurationPath = "ktor.rabbitmq",
    createConfiguration = ::ConnectionConfig
) {
    pluginConfig.verify()

    with(ConnectionManager(application, pluginConfig)) {
        RabbitMQDispatcherHolder.dispatcher = dispatcher
        application.attributes.put(ConnectionManagerKey, this)
    }
}

private object RabbitMQDispatcherHolder {
    lateinit var dispatcher: CoroutineDispatcher
}

val Dispatchers.rabbitMQ: CoroutineDispatcher
    get() = RabbitMQDispatcherHolder.dispatcher
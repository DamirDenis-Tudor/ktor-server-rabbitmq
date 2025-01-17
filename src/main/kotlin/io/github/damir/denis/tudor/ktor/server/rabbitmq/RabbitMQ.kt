package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionConfig
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.ktor.server.application.*
import io.ktor.util.*

val ConnectionManagerKey = AttributeKey<ConnectionManager>("ConnectionManager")

val RabbitMQ = createApplicationPlugin(
    name = "RabbitMQ",
    configurationPath = "ktor.rabbitmq",
    createConfiguration = ::ConnectionConfig
) {
    pluginConfig.verify()

    with(ConnectionManager(application, pluginConfig)) {
        application.attributes.put(ConnectionManagerKey, this)
    }
}
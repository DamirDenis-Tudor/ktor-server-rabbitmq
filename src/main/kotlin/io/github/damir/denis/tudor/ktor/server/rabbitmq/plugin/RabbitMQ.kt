package io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin

import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionConfig
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.PluginContext
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.ktor.server.application.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val ConnectionManagerKey = AttributeKey<ConnectionManager>("ConnectionManager")

val RabbitMQ = createApplicationPlugin(
    name = "KabbitMQ",
    configurationPath = "ktor.rabbitmq",
    createConfiguration = ::ConnectionConfig
) {
    pluginConfig.verify()

    with(ConnectionManager(pluginConfig)) {
        ConnectionConfig.service = this
        application.attributes.put(ConnectionManagerKey, this)
    }
}

@RabbitDslMarker
fun Application.rabbitmq(block: PluginContext.() -> Unit) {
    with(attributes[ConnectionManagerKey]) {
        PluginContext(this).apply(block)
    }
}

@RabbitDslMarker
fun Routing.rabbitmq(block: PluginContext.() -> Unit) {
    with(application.attributes[ConnectionManagerKey]) {
        PluginContext(this).apply(block)
    }
}

@RabbitDslMarker
fun Route.rabbitmq(block: PluginContext.() -> Unit) {
    with(application.attributes[ConnectionManagerKey]) {
        PluginContext(this).apply(block)
    }
}
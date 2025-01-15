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
import kotlinx.coroutines.launch

val ConnectionManagerKey = AttributeKey<ConnectionManager>("ConnectionManager")

val RabbitMQ = createApplicationPlugin(
    name = "KabbitMQ",
    configurationPath = "ktor.rabbitmq",
    createConfiguration = ::ConnectionConfig
) {
    pluginConfig.verify()

    with(ConnectionManager(application, pluginConfig)) {
        ConnectionConfig.service = this
        application.attributes.put(ConnectionManagerKey, this)
    }
}

@RabbitDslMarker
fun Application.rabbitmq(block:suspend PluginContext.() -> Unit) {
    with(attributes[ConnectionManagerKey]) {
        coroutineScope.launch(dispatcher) {
            PluginContext(this@with).apply { this.block() }
        }
    }
}

@RabbitDslMarker
fun Routing.rabbitmq(block: suspend PluginContext.() -> Unit) {
    with(application.attributes[ConnectionManagerKey]) {
        coroutineScope.launch(dispatcher) {
            PluginContext(this@with).apply { this.block() }
        }
    }
}

@RabbitDslMarker
fun Route.rabbitmq(block: suspend PluginContext.() -> Unit) {
    with(application.attributes[ConnectionManagerKey]) {
        coroutineScope.launch(dispatcher) {
            PluginContext(this@with).apply { this.block() }
        }
    }
}
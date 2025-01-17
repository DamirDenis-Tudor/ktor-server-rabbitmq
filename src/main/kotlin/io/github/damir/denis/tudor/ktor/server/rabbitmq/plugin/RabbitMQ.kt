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

/**
 * Configures RabbitMQ within the `Application` scope.
 *
 * This function provides a way to set up RabbitMQ operations at the `Application` level
 * using the `PluginContext`. It retrieves the `ConnectionManager` instance from the
 * application attributes and executes the provided block within the `PluginContext`.
 *
 * @param block A suspendable block where RabbitMQ operations can be configured.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
fun Application.rabbitmq(block: suspend PluginContext.() -> Unit) {
    with(attributes[ConnectionManagerKey]) {
        coroutineScope.launch(dispatcher) {
            PluginContext(this@with).apply { this.block() }
        }
    }
}

/**
 * Configures RabbitMQ within the `Routing` scope.
 *
 * This function enables RabbitMQ configuration and operations within the `Routing` scope.
 * It retrieves the `ConnectionManager` from the application's attributes and uses it
 * within a `PluginContext` to execute the given block.
 *
 * @param block A suspendable block where RabbitMQ routing operations can be configured.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
fun Routing.rabbitmq(block: suspend PluginContext.() -> Unit) {
    with(application.attributes[ConnectionManagerKey]) {
        coroutineScope.launch(dispatcher) {
            PluginContext(this@with).apply { this.block() }
        }
    }
}

/**
 * Configures RabbitMQ within the `Route` scope.
 *
 * This function allows RabbitMQ setup and operations within a specific route. It retrieves the
 * `ConnectionManager` from the application's attributes and creates a `PluginContext`
 * to execute the provided block.
 *
 * @param block A suspendable block where RabbitMQ route-specific operations can be configured.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
fun Route.rabbitmq(block: suspend PluginContext.() -> Unit) {
    with(application.attributes[ConnectionManagerKey]) {
        coroutineScope.launch(dispatcher) {
            PluginContext(this@with).apply { this.block() }
        }
    }
}

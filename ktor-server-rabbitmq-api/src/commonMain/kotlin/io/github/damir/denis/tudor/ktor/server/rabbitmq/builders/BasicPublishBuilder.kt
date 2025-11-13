package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Properties
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json

@RabbitDslMarker
class BasicPublishBuilder(
    private val channel: Channel,
) {
    var exchange: String by Delegator()
    var routingKey: String by Delegator()
    var message: ByteArray by Delegator()
    var mandatory: Boolean by Delegator()
    var immediate: Boolean by Delegator()
    var properties: Properties by Delegator()


    init {
        routingKey = ""
        properties = Properties()
    }

    @RabbitDslMarker
    inline fun <reified T> message(block: () -> T) {
        message = Json.encodeToString(block()).toByteArray(Charsets.UTF_8)
    }

    @RabbitDslMarker
    inline fun <reified T> message(block: T) {
        message = Json.encodeToString(block).toByteArray(Charsets.UTF_8)
    }

    suspend fun build() = delegatorScope(on = this@BasicPublishBuilder) {
        return@delegatorScope when {
            verify(::exchange, ::routingKey, ::message, ::mandatory, ::immediate, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    mandatory,
                    immediate,
                    properties,
                    message
                )
            }

            verify(::exchange, ::routingKey, ::message, ::immediate, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    immediate = immediate,
                    properties = properties,
                    message = message
                )
            }

            verify(::exchange, ::routingKey, ::message, ::mandatory, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    mandatory,
                    properties = properties,
                    message = message
                )
            }

            verify(::exchange, ::routingKey, ::message, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    properties = properties,
                    message = message
                )
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}


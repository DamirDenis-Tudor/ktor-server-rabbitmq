package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import kotlinx.serialization.encodeToString
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
    var properties: BasicProperties by Delegator()


    init {
        routingKey = ""
        properties = BasicProperties()
    }

    @RabbitDslMarker
    inline fun <reified T> message(block: () -> T) {
        message = Json.encodeToString(block()).toByteArray(Charsets.UTF_8)
    }

    @RabbitDslMarker
    inline fun <reified T> message(block: T) {
        message = Json.encodeToString(block).toByteArray(Charsets.UTF_8)
    }

    fun build() = delegatorScope(on = this@BasicPublishBuilder) {
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
                    immediate,
                    properties,
                    message
                )
            }

            verify(::exchange, ::routingKey, ::message, ::mandatory, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    mandatory,
                    properties,
                    message
                )
            }

            verify(::exchange, ::routingKey, ::message, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    properties,
                    message
                )
            }

            else -> error(stateTrace())
        }
    }
}


package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@KabbitMQDslMarker
class KabbitMQBasicPublishBuilder(
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

    @KabbitMQDslMarker
    inline fun <reified T> message(block: () -> T) {
        message = Json.encodeToString(block()).toByteArray(Charsets.UTF_8)
    }

    @KabbitMQDslMarker
    inline fun <reified T> message(block: T) {
        message = Json.encodeToString(block).toByteArray(Charsets.UTF_8)
    }

    fun build() = withThisRef(this@KabbitMQBasicPublishBuilder) {
        return@withThisRef when {
            initialized(::exchange, ::routingKey, ::message, ::mandatory, ::immediate, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    mandatory,
                    immediate,
                    properties,
                    message
                )
            }

            initialized(::exchange, ::routingKey, ::message, ::immediate, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    immediate,
                    properties,
                    message
                )
            }

            initialized(::exchange, ::routingKey, ::message, ::mandatory, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    mandatory,
                    properties,
                    message
                )
            }

            initialized(::exchange, ::routingKey, ::message, ::properties) -> {
                channel.basicPublish(
                    exchange,
                    routingKey,
                    properties,
                    message
                )
            }

            else -> error(reportStateTrace())
        }
    }
}


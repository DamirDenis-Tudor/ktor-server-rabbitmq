package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.State
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*


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

            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQBasicPublishBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }
}
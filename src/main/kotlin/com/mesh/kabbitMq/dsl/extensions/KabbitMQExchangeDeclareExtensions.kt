package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQExchangeDeclareBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.exchangeDeclare
 */
@KabbitMQDslMarker
inline fun Application.exchangeDeclare(block: KabbitMQExchangeDeclareBuilder.() -> Unit) {
    KabbitMQExchangeDeclareBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()
}

/**
 * @see Channel.exchangeDeclare
 */
@KabbitMQDslMarker
inline fun Channel.exchangeDeclare(block: KabbitMQExchangeDeclareBuilder.() -> Unit) {
    KabbitMQExchangeDeclareBuilder(this).apply(block).build()
}
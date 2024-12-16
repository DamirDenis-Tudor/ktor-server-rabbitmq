package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQExchangeDeclareBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
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
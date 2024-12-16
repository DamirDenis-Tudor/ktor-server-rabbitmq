package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQExchangeDeleteBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.exchangeDelete
 */
@KabbitMQDslMarker
inline fun Application.exchangeDelete(block: KabbitMQExchangeDeleteBuilder.() -> Unit) =
    KabbitMQExchangeDeleteBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.exchangeDelete
 */
@KabbitMQDslMarker
inline fun Channel.exchangeDelete(block: KabbitMQExchangeDeleteBuilder.() -> Unit) =
    KabbitMQExchangeDeleteBuilder(this).apply(block).build()

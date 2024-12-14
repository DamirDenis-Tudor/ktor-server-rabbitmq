package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQExchangeDeleteBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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

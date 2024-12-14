package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQConsumerCountBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.consumerCount
 */
@KabbitMQDslMarker
inline fun Application.consumerCount(block: KabbitMQConsumerCountBuilder.() -> Unit) =
    KabbitMQConsumerCountBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.consumerCount
 */
@KabbitMQDslMarker
inline fun Channel.consumerCount(block: KabbitMQConsumerCountBuilder.() -> Unit) =
    KabbitMQConsumerCountBuilder(this).apply(block).build()
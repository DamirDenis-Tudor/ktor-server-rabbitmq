package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQBasicNackBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicNack
 */
@KabbitMQDslMarker
inline fun Application.basicNack(block: KabbitMQBasicNackBuilder.() -> Unit) =
    KabbitMQBasicNackBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicNack
 */
@KabbitMQDslMarker
inline fun Channel.basicNack(block: KabbitMQBasicNackBuilder.() -> Unit) =
    KabbitMQBasicNackBuilder(this).apply(block).build()
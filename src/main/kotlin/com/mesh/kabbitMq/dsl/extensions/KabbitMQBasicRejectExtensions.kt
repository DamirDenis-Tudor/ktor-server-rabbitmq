package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQBasicRejectBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicReject
 */
@KabbitMQDslMarker
inline fun Application.basicReject(block: KabbitMQBasicRejectBuilder.() -> Unit) =
    KabbitMQBasicRejectBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicReject
 */
@KabbitMQDslMarker
inline fun Channel.basicReject(block: KabbitMQBasicRejectBuilder.() -> Unit) =
    KabbitMQBasicRejectBuilder(this).apply(block).build()

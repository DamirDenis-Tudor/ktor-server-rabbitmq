package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQBasicGetBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicGet
 */
@KabbitMQDslMarker
inline fun Channel.basicGet(block: KabbitMQBasicGetBuilder.() -> Unit) =
    KabbitMQBasicGetBuilder(this).apply(block).build()

/**
 * @see Channel.basicGet
 */
@KabbitMQDslMarker
inline fun Application.basicGet(block: KabbitMQBasicGetBuilder.() -> Unit) =
    KabbitMQBasicGetBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQBasicQosBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicQos
 */
@KabbitMQDslMarker
inline fun Application.basicQos(block: KabbitMQBasicQosBuilder.() -> Unit) =
    KabbitMQBasicQosBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicQos
 */
@KabbitMQDslMarker
inline fun Channel.basicQos(block: KabbitMQBasicQosBuilder.() -> Unit) =
    KabbitMQBasicQosBuilder(this).apply(block).build()
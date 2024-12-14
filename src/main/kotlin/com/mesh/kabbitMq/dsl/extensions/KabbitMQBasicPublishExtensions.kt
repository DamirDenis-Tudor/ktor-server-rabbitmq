package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQBasicPropertiesBuilder
import com.mesh.kabbitMq.builders.KabbitMQBasicPublishBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicPublish
 */
@KabbitMQDslMarker
inline fun Application.basicPublish(block: KabbitMQBasicPublishBuilder.() -> Unit) =
    KabbitMQBasicPublishBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicPublish
 */
@KabbitMQDslMarker
inline fun Channel.basicPublish(block: KabbitMQBasicPublishBuilder.() -> Unit) =
    KabbitMQBasicPublishBuilder(this,).apply(block).build()

/**
 * @see AMQP.BasicProperties
 */
@KabbitMQDslMarker
inline fun basicProperties(block: KabbitMQBasicPropertiesBuilder.() -> Unit) =
    KabbitMQBasicPropertiesBuilder().apply(block).build()

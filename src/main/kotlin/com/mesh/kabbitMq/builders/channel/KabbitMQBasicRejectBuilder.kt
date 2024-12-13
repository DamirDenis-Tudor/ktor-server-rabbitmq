package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

@KabbitMQDslMarker
class KabbitMQBasicRejectBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegates.notNull()
    var requeue: Boolean = false

    fun build() {
        channel.basicReject(deliveryTag, requeue)
    }
}
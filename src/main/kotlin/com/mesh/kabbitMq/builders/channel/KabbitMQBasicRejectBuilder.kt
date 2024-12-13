package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQBasicRejectBuilder(private val channel: Channel) {
    var deliveryTag: Long by StateDelegator()
    var requeue: Boolean = false

    fun build() {
        channel.basicReject(deliveryTag, requeue)
    }
}
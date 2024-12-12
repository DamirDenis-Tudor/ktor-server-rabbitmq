package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

class KabbitMQBasicRejectBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegates.notNull()
    var requeue: Boolean = false

    fun build() {
        channel.basicReject(deliveryTag, requeue)
    }
}
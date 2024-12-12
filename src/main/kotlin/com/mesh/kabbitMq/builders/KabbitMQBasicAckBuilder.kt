package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

class KabbitMQBasicAckBuilder(private val channel: Channel) {
    var deliveryTag by Delegates.notNull<Long>()
    var multiple: Boolean = false

    fun build() {
        channel.basicAck(deliveryTag, multiple)
    }
}
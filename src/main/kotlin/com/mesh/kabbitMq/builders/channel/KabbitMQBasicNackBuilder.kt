package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQBasicNackBuilder(private val channel: Channel) {
    var deliveryTag: Long by StateDelegator()
    var multiple: Boolean = false
    var requeue: Boolean = false

    fun build() {
        channel.basicNack(deliveryTag, multiple, requeue)
    }
}
package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

class KabbitMQBasicNackBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegates.notNull()
    var multiple: Boolean = false
    var requeue: Boolean = false

    fun build() {
        channel.basicNack(deliveryTag, multiple, requeue)
    }
}
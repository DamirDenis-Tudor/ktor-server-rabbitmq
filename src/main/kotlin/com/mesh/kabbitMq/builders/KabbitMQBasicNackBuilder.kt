package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

@KabbitMQDslMarker
class KabbitMQBasicNackBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegates.notNull()
    var multiple: Boolean = false
    var requeue: Boolean = false

    fun build() {
        channel.basicNack(deliveryTag, multiple, requeue)
    }
}
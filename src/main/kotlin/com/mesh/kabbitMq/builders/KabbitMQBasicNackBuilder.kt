package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQBasicNackBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()
    var requeue: Boolean by Delegator()

    init {
        multiple = false
        requeue = false
    }

    fun build() = channel.basicNack(deliveryTag, multiple, requeue)

}
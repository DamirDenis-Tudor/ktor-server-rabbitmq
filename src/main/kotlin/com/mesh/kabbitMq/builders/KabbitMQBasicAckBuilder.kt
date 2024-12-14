package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.State
import com.mesh.kabbitMq.delegator.Delegator
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQBasicAckBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()

    init {
        multiple = false
    }

    fun build() {
        channel.basicAck(deliveryTag, multiple)
    }
}

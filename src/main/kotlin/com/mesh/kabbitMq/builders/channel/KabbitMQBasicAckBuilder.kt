package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQBasicAckBuilder(private val channel: Channel) {
    var deliveryTag: Long by StateDelegator()
    var multiple: Boolean by StateDelegator(State.Initialized(false))

    fun build() {
        channel.basicAck(deliveryTag, multiple)
    }
}

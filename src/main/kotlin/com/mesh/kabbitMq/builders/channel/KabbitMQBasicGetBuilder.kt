package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse

@KabbitMQDslMarker
class KabbitMQBasicGetBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()
    var autoAck: Boolean by StateDelegator()

    fun build(): GetResponse = channel.basicGet(queue, autoAck)
}
package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse

@KabbitMQDslMarker
class KabbitMQBasicGetBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var autoAck: Boolean by Delegator()

    fun build(): GetResponse = channel.basicGet(queue, autoAck)
}
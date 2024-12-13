package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.AMQP.Queue.DeclareOk
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeclareBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()
    var durable: Boolean by StateDelegator(State.Initialized(true))
    var exclusive: Boolean by StateDelegator(State.Initialized(false))
    var autoDelete: Boolean by StateDelegator(State.Initialized(false))
    var arguments: Map<String, Any> by StateDelegator(State.Initialized(emptyMap()))

    fun build(): DeclareOk = with(StateDelegator) {
        when {
            initialized(::queue, ::durable, ::exclusive, ::autoDelete, ::arguments) -> {
                channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments)
            }
            else -> {
                channel.queueDeclare()
            }
        }
    }
}

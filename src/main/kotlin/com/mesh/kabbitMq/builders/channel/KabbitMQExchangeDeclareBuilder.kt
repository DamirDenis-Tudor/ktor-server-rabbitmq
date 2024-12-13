package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeclareBuilder(private val channel: Channel) {

    var exchange: String by StateDelegator()
    var type: BuiltinExchangeType by StateDelegator()
    var durable: Boolean by StateDelegator(State.Initialized(false))
    var autoDelete: Boolean by StateDelegator(State.Initialized(false))
    var internal: Boolean by StateDelegator(State.Initialized(false))
    var arguments: Map<String, Any> by StateDelegator(State.Initialized(emptyMap()))

    fun build() {
        channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments)
    }
}

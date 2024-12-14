package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.State
import com.mesh.kabbitMq.delegator.Delegator
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeclareBuilder(private val channel: Channel) {

    var exchange: String by Delegator()
    var type: BuiltinExchangeType by Delegator()
    var durable: Boolean by Delegator()
    var autoDelete: Boolean by Delegator()
    var internal: Boolean by Delegator()
    var arguments: Map<String, Any> by Delegator()

    init {
        durable = false
        autoDelete = false
        internal = false
        arguments = emptyMap()
    }

    fun build(): AMQP.Exchange.DeclareOk =
        channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments)
}

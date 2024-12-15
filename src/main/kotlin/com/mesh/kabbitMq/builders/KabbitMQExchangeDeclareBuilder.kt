package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*

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

    fun build(): AMQP.Exchange.DeclareOk = withThisRef(this@KabbitMQExchangeDeclareBuilder) {
        return@withThisRef when {
            initialized(::exchange, ::type, ::durable, ::autoDelete, ::internal, ::arguments) -> {
                channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments)
            }

            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQExchangeDeclareBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }
}

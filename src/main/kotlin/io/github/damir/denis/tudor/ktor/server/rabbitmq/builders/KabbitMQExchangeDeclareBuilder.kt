package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
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

    fun build(): AMQP.Exchange.DeclareOk = withThisRef(this@KabbitMQExchangeDeclareBuilder) {
        return@withThisRef when {
            initialized(::exchange, ::type, ::durable, ::autoDelete, ::internal, ::arguments) -> {
                channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments)
            }

            else -> error(reportStateTrace())
        }
    }
}

package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeclareBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var durable: Boolean by Delegator()
    var exclusive: Boolean by Delegator()
    var autoDelete: Boolean by Delegator()
    var arguments: Map<String, Any> by Delegator()

    init {
        durable = true
        exclusive = false
        autoDelete = false
        arguments = emptyMap()
    }

    fun build(): AMQP.Queue.DeclareOk = withThisRef(this@KabbitMQQueueDeclareBuilder) {
        return@withThisRef when {
            initialized(::queue, ::durable, ::exclusive, ::autoDelete, ::arguments) -> {
                channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments)
            }

            else -> error(reportStateTrace())
        }
    }
}

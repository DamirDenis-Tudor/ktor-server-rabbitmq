package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueUnbindBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var exchange: String by Delegator()
    var routingKey: String by Delegator()
    var arguments: Map<String, Any> by Delegator()

    fun build(): AMQP.Queue.UnbindOk = withThisRef(this@KabbitMQQueueUnbindBuilder) {
        return@withThisRef when {
            initialized(::queue, ::exchange, ::routingKey, ::arguments) -> {
                channel.queueUnbind(queue, exchange, routingKey, arguments)
            }

            initialized(::queue, ::exchange, ::routingKey) -> {
                channel.queueUnbind(queue, exchange, routingKey)
            }

            else -> error(reportStateTrace())
        }
    }
}
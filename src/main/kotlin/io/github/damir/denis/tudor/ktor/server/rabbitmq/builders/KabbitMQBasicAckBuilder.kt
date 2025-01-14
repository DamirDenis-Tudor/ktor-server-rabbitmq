package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker


@KabbitMQDslMarker
class KabbitMQBasicAckBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()

    init {
        multiple = false
    }

    fun build() = delegatorScope(on = this@KabbitMQBasicAckBuilder) {
        return@delegatorScope when {
            verify(::deliveryTag, ::multiple) -> {
                channel.basicAck(deliveryTag, multiple)
            }

            else -> error(stateTrace())
        }
    }
}

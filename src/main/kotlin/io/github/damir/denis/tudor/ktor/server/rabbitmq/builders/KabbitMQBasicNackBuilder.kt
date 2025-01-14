package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker

@KabbitMQDslMarker
class KabbitMQBasicNackBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()
    var requeue: Boolean by Delegator()

    init {
        multiple = false
        requeue = false
    }

    fun build() = delegatorScope(on = this@KabbitMQBasicNackBuilder) {
        return@delegatorScope when {
            verify(::deliveryTag, ::multiple, ::requeue) -> {
                channel.basicNack(deliveryTag, multiple, requeue)
            }

            else -> error(stateTrace())
        }
    }
}
package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker

@KabbitMQDslMarker
class KabbitMQMessageCountBuilder(private val channel: Channel) {
    var queue: String by Delegator()

    fun build(): Long = delegatorScope(on = this@KabbitMQMessageCountBuilder) {
        return@delegatorScope when {
            verify(::queue) -> {
                channel.messageCount(queue)
            }
            else -> error(stateTrace())
        }
    }

}
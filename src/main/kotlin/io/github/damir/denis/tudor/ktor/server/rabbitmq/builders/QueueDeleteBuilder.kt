package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker

@RabbitDslMarker
class QueueDeleteBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var ifUnused: Boolean by Delegator()
    var ifEmpty: Boolean by Delegator()

    fun build(): AMQP.Queue.DeleteOk = delegatorScope(on = this@QueueDeleteBuilder) {
        return@delegatorScope when {
            verify(::queue, ::ifUnused, ::ifEmpty) -> {
                channel.queueDelete(queue, ifUnused, ifEmpty)
            }

            verify(::queue) -> {
                channel.queueDelete(queue)
            }

            else -> error(stateTrace())
        }
    }
}

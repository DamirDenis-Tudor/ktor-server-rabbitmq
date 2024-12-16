package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeleteBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var ifUnused: Boolean by Delegator()
    var ifEmpty: Boolean by Delegator()

    fun build(): AMQP.Queue.DeleteOk = withThisRef(this@KabbitMQQueueDeleteBuilder) {
        return@withThisRef when {
            initialized(::queue, ::ifUnused, ::ifEmpty) -> {
                channel.queueDelete(queue, ifUnused, ifEmpty)
            }

            initialized(::queue) -> {
                channel.queueDelete(queue)
            }

            else -> error(reportStateTrace())
        }
    }
}

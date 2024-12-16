package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQMessageCountBuilder(private val channel: Channel) {
    var queue: String by Delegator()

    fun build(): Long = withThisRef(this@KabbitMQMessageCountBuilder) {
        return@withThisRef when {
            initialized(::queue) -> {
                channel.messageCount(queue)
            }
            else -> error(reportStateTrace())
        }
    }

}
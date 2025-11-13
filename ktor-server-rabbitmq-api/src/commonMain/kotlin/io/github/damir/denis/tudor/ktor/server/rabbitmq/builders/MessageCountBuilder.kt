package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel

@RabbitDslMarker
class MessageCountBuilder(private val channel: Channel) {
    var queue: String by Delegator()

    suspend fun build(): Long = delegatorScope(on = this@MessageCountBuilder) {
        return@delegatorScope when {
            verify(::queue) -> {
                channel.messageCount(queue)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }

}

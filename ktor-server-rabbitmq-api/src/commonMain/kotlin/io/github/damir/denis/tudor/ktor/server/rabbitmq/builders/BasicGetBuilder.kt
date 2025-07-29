package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.GetResponse

@RabbitDslMarker
class BasicGetBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var autoAck: Boolean by Delegator()

    suspend fun build(): GetResponse = delegatorScope(on = this@BasicGetBuilder) {
        return@delegatorScope when {
            verify(::queue, ::autoAck) -> {
                channel.basicGet(queue, autoAck)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}

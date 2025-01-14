package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker

@RabbitDslMarker
class BasicGetBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var autoAck: Boolean by Delegator()

    fun build(): GetResponse = delegatorScope(on = this@BasicGetBuilder) {
        return@delegatorScope when {
            verify(::queue, ::autoAck) -> {
                channel.basicGet(queue, autoAck)
            }

            else -> error(stateTrace())
        }
    }
}
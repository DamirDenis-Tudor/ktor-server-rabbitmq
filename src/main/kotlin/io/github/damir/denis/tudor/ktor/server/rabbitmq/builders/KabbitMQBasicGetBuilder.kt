package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse

@KabbitMQDslMarker
class KabbitMQBasicGetBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var autoAck: Boolean by Delegator()

    fun build(): GetResponse = withThisRef(this@KabbitMQBasicGetBuilder) {
        return@withThisRef when {
            initialized(::queue, ::autoAck) -> {
                channel.basicGet(queue, autoAck)
            }

            else -> error(reportStateTrace())
        }
    }
}
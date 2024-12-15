package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*

@KabbitMQDslMarker
class KabbitMQMessageCountBuilder(private val channel: Channel) {
    var queue: String by Delegator()

    fun build(): Long = withThisRef(this@KabbitMQMessageCountBuilder) {
        return@withThisRef when {
            initialized(::queue) -> {
                channel.messageCount(queue)
            }
            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQMessageCountBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }

}
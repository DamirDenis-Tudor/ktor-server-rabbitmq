package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*

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

            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQQueueDeleteBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }
}

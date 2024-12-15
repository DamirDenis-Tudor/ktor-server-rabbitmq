package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*


@KabbitMQDslMarker
class KabbitMQBasicRejectBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var requeue: Boolean by Delegator()

    init {
        requeue = false
    }

    fun build() = withThisRef(this@KabbitMQBasicRejectBuilder) {
        return@withThisRef when {
            initialized(::deliveryTag, ::requeue) -> {
                channel.basicReject(deliveryTag, requeue)
            }

            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQBasicRejectBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }
}
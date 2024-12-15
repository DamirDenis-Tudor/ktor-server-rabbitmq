package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQBasicAckBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()

    init {
        multiple = false
    }

    fun build() = withThisRef(this@KabbitMQBasicAckBuilder) {
        return@withThisRef when {
            initialized(::deliveryTag, ::multiple) -> {
                channel.basicAck(deliveryTag, multiple)
            }

            else -> reportStateTrace()
        }
    }
}

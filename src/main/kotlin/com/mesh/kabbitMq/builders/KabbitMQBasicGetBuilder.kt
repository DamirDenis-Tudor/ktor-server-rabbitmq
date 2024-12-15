package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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
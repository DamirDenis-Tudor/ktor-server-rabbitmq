package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.mesh.kabbitMq.util.StateDelegator.Companion.withThisRef
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueBindBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()
    var exchange: String by StateDelegator()
    var routingKey: String by StateDelegator()
    var arguments: Map<String, Any> by StateDelegator()

    init {
        routingKey = ""
    }

    fun build() {
        withThisRef(this@KabbitMQQueueBindBuilder){
            when  {
                initialized(::arguments) ->{
                    channel.queueBind(queue, exchange, routingKey, arguments)
                }
                else -> {
                    channel.queueBind(queue, exchange, routingKey)
                }
            }
        }
    }
}
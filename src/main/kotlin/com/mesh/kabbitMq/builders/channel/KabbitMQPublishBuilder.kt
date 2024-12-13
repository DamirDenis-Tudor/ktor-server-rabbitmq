package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQPublishBuilder(private val channel: Channel) {
    var exchange: String by StateDelegator()
    var routingKey: String by StateDelegator()
    var message: String by StateDelegator()
    var mandatory: Boolean by StateDelegator()
    var immediate: Boolean by StateDelegator()
    var basicProperties by StateDelegator(State.Initialized(AMQP.BasicProperties()))

    fun build() {
        with(StateDelegator) {
            when {
                initialized(::mandatory, ::immediate) -> {
                    channel.basicPublish(exchange, routingKey, mandatory, immediate, basicProperties, message.toByteArray())
                }
                initialized(::immediate) -> {
                    channel.basicPublish(exchange, routingKey, immediate, basicProperties, message.toByteArray())
                }
                initialized(::mandatory) -> {
                    channel.basicPublish(exchange, routingKey, mandatory, basicProperties, message.toByteArray())
                }
                else -> {
                    channel.basicPublish(exchange, routingKey, basicProperties, message.toByteArray())
                }
            }
        }
    }
}
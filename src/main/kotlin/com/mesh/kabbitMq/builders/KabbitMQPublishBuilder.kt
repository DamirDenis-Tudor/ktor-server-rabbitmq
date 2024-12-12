package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQPublishBuilder(private val channel: Channel) {
    lateinit var exchange: String
    lateinit var routingKey: String
    lateinit var message: String
    var mandatory: Boolean? = null
    var immediate: Boolean? = null
    var basicProperties: AMQP.BasicProperties? = null

    fun build() {
        when {
            mandatory != null && immediate != null -> channel.basicPublish(exchange, routingKey, mandatory!!, immediate!!, basicProperties, message.toByteArray())
            mandatory != null -> channel.basicPublish(exchange, routingKey, mandatory!!, basicProperties, message.toByteArray())
            immediate != null -> channel.basicPublish(exchange, routingKey, immediate!!, basicProperties, message.toByteArray())
            else -> channel.basicPublish(exchange, routingKey, basicProperties, message.toByteArray())
        }
    }
}
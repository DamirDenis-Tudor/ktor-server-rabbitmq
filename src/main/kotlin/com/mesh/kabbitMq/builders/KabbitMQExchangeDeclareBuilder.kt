package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeclareBuilder(private val channel: Channel) {
    lateinit var exchange: String
    lateinit var type: BuiltinExchangeType
    var durable: Boolean = false
    var autoDelete: Boolean = false
    var internal: Boolean = false
    var arguments: Map<String, Any> = emptyMap()

    fun build() {
        when{
            !::exchange.isInitialized  -> error("Exchange is not initialized")
            !::type.isInitialized  -> error("Type is not initialized")
            else -> channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments)
        }
    }
}
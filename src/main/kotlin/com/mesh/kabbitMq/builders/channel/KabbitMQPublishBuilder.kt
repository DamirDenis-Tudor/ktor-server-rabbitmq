package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.mesh.kabbitMq.util.StateDelegator.Companion.withThisRef
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import java.util.*

@KabbitMQDslMarker
class KabbitMQPublishBuilder(private val channel: Channel) {
    var exchange: String by StateDelegator()
    var routingKey: String by StateDelegator()
    var message: String by StateDelegator()
    var mandatory: Boolean by StateDelegator()
    var immediate: Boolean by StateDelegator()
    var properties by StateDelegator(State.Initialized(AMQP.BasicProperties()))

    fun build() {
        withThisRef(this@KabbitMQPublishBuilder){
            when {
                initialized(::mandatory, ::immediate) -> {
                    channel.basicPublish(exchange, routingKey, mandatory, immediate, properties, message.toByteArray())
                }
                initialized(::immediate) -> {
                    channel.basicPublish(exchange, routingKey, immediate, properties, message.toByteArray())
                }
                initialized(::mandatory) -> {
                    channel.basicPublish(exchange, routingKey, mandatory, properties, message.toByteArray())
                }
                else -> {
                    channel.basicPublish(exchange, routingKey, properties, message.toByteArray())
                }
            }
        }
    }

    @KabbitMQDslMarker
    class KabbitMQBasicPropertiesBuilder {

        private val builder = AMQP.BasicProperties.Builder()

        var contentType: String by StateDelegator()
        var contentEncoding: String by StateDelegator()
        var headers: Map<String, Any> by StateDelegator()
        var deliveryMode: Int by StateDelegator()
        var priority: Int by StateDelegator()
        var correlationId: String by StateDelegator()
        var replyTo: String by StateDelegator()
        var expiration: String by StateDelegator()
        var messageId: String by StateDelegator()
        var timestamp: Date by StateDelegator()
        var type: String by StateDelegator()
        var userId: String by StateDelegator()
        var appId: String by StateDelegator()
        var clusterId: String by StateDelegator()

        fun build(): AMQP.BasicProperties {
            withThisRef(this@KabbitMQBasicPropertiesBuilder) {
                when {
                    initialized(::contentType) -> builder.contentType(contentType)
                    initialized(::contentEncoding) -> builder.contentEncoding(contentEncoding)
                    initialized(::headers) -> builder.headers(headers)
                    initialized(::deliveryMode) -> builder.deliveryMode(deliveryMode)
                    initialized(::priority) -> builder.priority(priority)
                    initialized(::correlationId) -> builder.correlationId(correlationId)
                    initialized(::replyTo) -> builder.replyTo(replyTo)
                    initialized(::expiration) -> builder.expiration(expiration)
                    initialized(::messageId) -> builder.messageId(messageId)
                    initialized(::timestamp) -> builder.timestamp(timestamp)
                    initialized(::type) -> builder.type(type)
                    initialized(::userId) -> builder.userId(userId)
                    initialized(::appId) -> builder.appId(appId)
                    initialized(::clusterId) -> builder.clusterId(clusterId)
                    else -> {}
                }
            }

            return builder.build()
        }
    }
}
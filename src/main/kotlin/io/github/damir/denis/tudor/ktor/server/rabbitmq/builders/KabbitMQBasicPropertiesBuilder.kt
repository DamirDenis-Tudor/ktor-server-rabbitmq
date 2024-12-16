package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import java.util.*

@KabbitMQDslMarker
class KabbitMQBasicPropertiesBuilder {

    private val builder = AMQP.BasicProperties.Builder()

    var contentType: String by Delegator()
    var contentEncoding: String by Delegator()
    var headers: Map<String, Any> by Delegator()
    var deliveryMode: Int by Delegator()
    var priority: Int by Delegator()
    var correlationId: String by Delegator()
    var replyTo: String by Delegator()
    var expiration: String by Delegator()
    var messageId: String by Delegator()
    var timestamp: Date by Delegator()
    var type: String by Delegator()
    var userId: String by Delegator()
    var appId: String by Delegator()
    var clusterId: String by Delegator()

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
package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.AMQP
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import java.util.*

@RabbitDslMarker
class BasicPropertiesBuilder {

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
        delegatorScope(on = this@BasicPropertiesBuilder) {
            when {
                verify(::contentType) -> builder.contentType(contentType)
                verify(::contentEncoding) -> builder.contentEncoding(contentEncoding)
                verify(::headers) -> builder.headers(headers)
                verify(::deliveryMode) -> builder.deliveryMode(deliveryMode)
                verify(::priority) -> builder.priority(priority)
                verify(::correlationId) -> builder.correlationId(correlationId)
                verify(::replyTo) -> builder.replyTo(replyTo)
                verify(::expiration) -> builder.expiration(expiration)
                verify(::messageId) -> builder.messageId(messageId)
                verify(::timestamp) -> builder.timestamp(timestamp)
                verify(::type) -> builder.type(type)
                verify(::userId) -> builder.userId(userId)
                verify(::appId) -> builder.appId(appId)
                verify(::clusterId) -> builder.clusterId(clusterId)
                else -> {}
            }
        }

        return builder.build()
    }
}
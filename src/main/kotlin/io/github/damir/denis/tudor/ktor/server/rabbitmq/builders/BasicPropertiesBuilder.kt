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
            if (verify(::contentType)) builder.contentType(contentType)
            if (verify(::contentEncoding)) builder.contentEncoding(contentEncoding)
            if (verify(::headers)) builder.headers(headers)
            if (verify(::deliveryMode)) builder.deliveryMode(deliveryMode)
            if (verify(::priority)) builder.priority(priority)
            if (verify(::correlationId)) builder.correlationId(correlationId)
            if (verify(::replyTo)) builder.replyTo(replyTo)
            if (verify(::expiration)) builder.expiration(expiration)
            if (verify(::messageId)) builder.messageId(messageId)
            if (verify(::timestamp)) builder.timestamp(timestamp)
            if (verify(::type)) builder.type(type)
            if (verify(::userId)) builder.userId(userId)
            if (verify(::appId)) builder.appId(appId)
            if (verify(::clusterId)) builder.clusterId(clusterId)
        }

        return builder.build()
    }
}
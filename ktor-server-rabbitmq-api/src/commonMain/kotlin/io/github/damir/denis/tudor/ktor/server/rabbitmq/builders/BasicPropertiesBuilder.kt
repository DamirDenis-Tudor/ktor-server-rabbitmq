package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Properties

@RabbitDslMarker
class BasicPropertiesBuilder {

    var contentType: String by Delegator()
    var contentEncoding: String by Delegator()
    var headers: Map<String, Any> by Delegator()
    var deliveryMode: Int by Delegator()
    var priority: Int by Delegator()
    var correlationId: String by Delegator()
    var replyTo: String by Delegator()
    var expiration: String by Delegator()
    var messageId: String by Delegator()
    var timestamp: Long by Delegator()
    var type: String by Delegator()
    var userId: String by Delegator()
    var appId: String by Delegator()
    var clusterId: String by Delegator()

    suspend fun build(): Properties {
        return delegatorScope(on = this@BasicPropertiesBuilder) {
            Properties(
                if (verify(::contentType)) contentType else null,
                if (verify(::contentEncoding)) contentEncoding else null,
                if (verify(::headers)) headers else null,
                if (verify(::deliveryMode)) deliveryMode else null,
                if (verify(::priority)) priority else null,
                if (verify(::correlationId)) correlationId else null,
                if (verify(::replyTo)) replyTo else null,
                if (verify(::expiration)) expiration else null,
                if (verify(::messageId)) messageId else null,
                if (verify(::timestamp)) timestamp else null,
                if (verify(::type)) type else null,
                if (verify(::userId)) userId else null,
                if (verify(::appId)) appId else null,
                if (verify(::clusterId)) clusterId else null,
            )
        }
    }
}

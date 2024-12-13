package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

@KabbitMQDslMarker
class KabbitMQBasicAckBuilder(
    private val channel: Channel
) {
    var deliveryTag by Delegates.notNull<Long>()
    var multiple: Boolean = false

    fun build() {
        channel.basicAck(deliveryTag, multiple)
    }
}

package com.mesh.kabbitMq.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery

class KabbitMqScope(
    private val channel: Channel,
    private val message: Delivery,
) {

    fun ack(multiple: Boolean = false) {
        channel.basicAck(message.envelope.deliveryTag, multiple)
    }

    fun nack(multiple: Boolean = false, requeue: Boolean = false) {
        channel.basicNack(message.envelope.deliveryTag, multiple, requeue)
    }

    fun reject(requeue: Boolean = false) {
        channel.basicReject(message.envelope.deliveryTag, requeue)
    }
}
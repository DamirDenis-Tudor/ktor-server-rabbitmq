package com.mesh.kabbitMq.service

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

interface IKabbitMQService {

    /**
     * Publishes a message to the specified exchange with the given routing key.
     *
     * @param exchange the exchange to publish to.
     * @param routingKey the routing key to use for routing the message.
     * @param props the message properties, can be null.
     * @param body the message body to be serialized and sent.
     */
    fun publish(exchange: String, routingKey: String = "", props: AMQP.BasicProperties? = null, body: String)

    /**
     * Consumes messages from the specified queue.
     *
     * @param queue the queue to consume messages from.
     * @param autoAck whether messages should be automatically acknowledged.
     * @param basicQos the basic quality of service, can be null.
     * @param rabbitDeliverCallback the callback to invoke when a message is received.
     */
    fun consume(
        queue: String,
        autoAck: Boolean = true,
        basicQos: Int? = null,
        rabbitDeliverCallback: KabbitMqScope.(body: String) -> Unit
    )

    fun withChannel(block: Channel.() -> Unit)

}

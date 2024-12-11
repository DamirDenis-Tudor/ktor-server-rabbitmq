package com.mesh.kabbitMq.service

import com.mesh.kabbitMq.config.KabbitMQConfig
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class KabbitMQService(private val rabbitMqConfiguration: KabbitMQConfig) : IKabbitMQService {

    private val connectionFactory = ConnectionFactory().apply { setUri(rabbitMqConfiguration.uri) }
    private val connection: Connection = connectionFactory.newConnection(rabbitMqConfiguration.connectionName)
    private val channel: Channel = connection.createChannel()

    init { rabbitMqConfiguration.applyInitialization(channel) }

    override fun publish(exchange: String, routingKey: String, props: AMQP.BasicProperties?, body: String) =
        withChannel { channel.basicPublish(exchange, routingKey, props, body.toByteArray()) }

    override fun consume(
        queue: String,
        autoAck: Boolean,
        basicQos: Int?,
        rabbitDeliverCallback: KabbitMqScope.(body: String) -> Unit
    ) {
        withChannel {
            basicQos?.let { this.basicQos(it) }
            basicConsume(queue, autoAck, { consumerTag, message ->
                runCatching {
                    rabbitDeliverCallback.invoke(KabbitMqScope(channel, message), message.body.decodeToString())
                }.getOrElse { throwable ->
                    System.err.println(
                        "DeliverCallback error: (messageId =" +
                                " ${message.properties.messageId}, consumerTag = $consumerTag)"
                    )
                    throw throwable
                }
            }, { consumerTag ->
                System.err.println("Consume cancelled: (consumerTag = $consumerTag)")
            })
        }
    }

    override fun withChannel(block: Channel.() -> Unit) = block.invoke(channel)
}
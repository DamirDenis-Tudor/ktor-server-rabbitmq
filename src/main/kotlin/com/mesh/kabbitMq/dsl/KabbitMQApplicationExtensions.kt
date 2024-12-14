package com.mesh.kabbitMq.dsl

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.channel.*
import com.mesh.kabbitMq.service.KabbitMQConfig
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.ktor.server.application.*
import io.ktor.websocket.*

@KabbitMQDslMarker
inline fun Application.connection(id: String,autoClose: Boolean = true, block: Connection.() -> Unit) =
    attributes[KabbitMQServiceKey].getConnection(id).apply(block).apply{
        if (autoClose) attributes[KabbitMQServiceKey].closeConnection(id)
    }

@KabbitMQDslMarker
inline fun Application.channel(id: String, autoClose: Boolean = true, block: Channel.() -> Unit): Channel {
    return attributes[KabbitMQServiceKey].getChannel(id).apply(block).apply{
        if (autoClose) attributes[KabbitMQServiceKey].closeChannel(id)
    }
}

@KabbitMQDslMarker
inline fun Application.basicAck(block: KabbitMQBasicAckBuilder.() -> Unit) =
    KabbitMQBasicAckBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.basicQos(block: KabbitMQBasicQosBuilder.() -> Unit) =
    KabbitMQBasicQosBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.exchangeDelete(block: KabbitMQExchangeDeleteBuilder.() -> Unit) =
    KabbitMQExchangeDeleteBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.queueDeclare(block: KabbitMQQueueDeclareBuilder.() -> Unit) =
    KabbitMQQueueDeclareBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.basicConsume(block: KabbitMQBasicConsumeBuilder.() -> Unit) =
    KabbitMQBasicConsumeBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.basicReject(block: KabbitMQBasicRejectBuilder.() -> Unit) =
    KabbitMQBasicRejectBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.messageCount(block: KabbitMQMessageCountBuilder.() -> Unit) =
    KabbitMQMessageCountBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.queueDelete(block: KabbitMQQueueDeleteBuilder.() -> Unit) =
    KabbitMQQueueDeleteBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.basicGet(block: KabbitMQBasicGetBuilder.() -> Unit) =
    KabbitMQBasicGetBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.consumerCount(block: KabbitMQConsumerCountBuilder.() -> Unit) =
    KabbitMQConsumerCountBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.basicPublish(block: KabbitMQPublishBuilder.() -> Unit) =
    KabbitMQPublishBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun KabbitMQPublishBuilder.basicProperties(block: KabbitMQPublishBuilder.KabbitMQBasicPropertiesBuilder.() -> Unit) =
    KabbitMQPublishBuilder.KabbitMQBasicPropertiesBuilder().apply(block).build()

@KabbitMQDslMarker
inline fun Application.queueUnbind(block: KabbitMQQueueUnbindBuilder.() -> Unit) =
    KabbitMQQueueUnbindBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.basicNack(block: KabbitMQBasicNackBuilder.() -> Unit) =
    KabbitMQBasicNackBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Application.exchangeDeclare(block: KabbitMQExchangeDeclareBuilder.() -> Unit) {
    KabbitMQExchangeDeclareBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()
}

@KabbitMQDslMarker
inline fun Application.queueBind(block: KabbitMQQueueBindBuilder.() -> Unit) =
    KabbitMQQueueBindBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()
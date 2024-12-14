package com.mesh.kabbitMq.dsl

import com.mesh.kabbitMq.builders.channel.*
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

@KabbitMQDslMarker
inline fun Connection.channel(block: Channel.() -> Unit): Channel {
    return this.createChannel().also(block)
}

@KabbitMQDslMarker
inline fun Connection.channel(id: String, block: Channel.() -> Unit): Channel {
    return this.createChannel().also(block)
}

@KabbitMQDslMarker
inline fun Channel.basicConsume(block: KabbitMQBasicConsumeBuilder.() -> Unit) =
    KabbitMQBasicConsumeBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.basicAck(block: KabbitMQBasicAckBuilder.() -> Unit) =
    KabbitMQBasicAckBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.basicQos(block: KabbitMQBasicQosBuilder.() -> Unit) =
    KabbitMQBasicQosBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.exchangeDelete(block: KabbitMQExchangeDeleteBuilder.() -> Unit) =
    KabbitMQExchangeDeleteBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.queueDeclare(block: KabbitMQQueueDeclareBuilder.() -> Unit) =
    KabbitMQQueueDeclareBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.basicReject(block: KabbitMQBasicRejectBuilder.() -> Unit) =
    KabbitMQBasicRejectBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.messageCount(block: KabbitMQMessageCountBuilder.() -> Unit) =
    KabbitMQMessageCountBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.queueDelete(block: KabbitMQQueueDeleteBuilder.() -> Unit) =
    KabbitMQQueueDeleteBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.basicGet(block: KabbitMQBasicGetBuilder.() -> Unit) =
    KabbitMQBasicGetBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.consumerCount(block: KabbitMQConsumerCountBuilder.() -> Unit) =
    KabbitMQConsumerCountBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun <reified T : Any> Channel.basicPublish(block: KabbitMQPublishBuilder.() -> Unit) =
    KabbitMQPublishBuilder(this,).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.queueUnbind(block: KabbitMQQueueUnbindBuilder.() -> Unit) =
    KabbitMQQueueUnbindBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.basicNack(block: KabbitMQBasicNackBuilder.() -> Unit) =
    KabbitMQBasicNackBuilder(this).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.exchangeDeclare(block: KabbitMQExchangeDeclareBuilder.() -> Unit) {
    KabbitMQExchangeDeclareBuilder(this).apply(block).build()
}

@KabbitMQDslMarker
inline fun Channel.queueBind(block: KabbitMQQueueBindBuilder.() -> Unit) =
    KabbitMQQueueBindBuilder(this).apply(block).build()
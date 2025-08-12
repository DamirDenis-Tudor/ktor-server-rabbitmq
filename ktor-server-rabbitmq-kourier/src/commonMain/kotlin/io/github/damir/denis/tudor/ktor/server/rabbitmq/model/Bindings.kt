package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

import dev.kourier.amqp.*

fun Envelope(original: AMQPMessage) = Envelope(
    deliveryTag = original.deliveryTag.toLong(),
    isRedeliver = original.redelivered,
    exchange = original.exchange,
    routingKey = original.routingKey,
)

fun Properties(original: dev.kourier.amqp.Properties) = Properties(
    contentType = original.contentType,
    contentEncoding = original.contentEncoding,
    headers = original.headers?.toMap(),
    deliveryMode = original.deliveryMode?.toInt(),
    priority = original.priority?.toInt(),
    correlationId = original.correlationId,
    replyTo = original.replyTo,
    expiration = original.expiration,
    messageId = original.messageId,
    timestamp = original.timestamp,
    type = original.type,
    userId = original.userId,
    appId = original.appId,
    //clusterId = original.clusterId, // Not supported in Kourier
)

fun Properties.toKourierProperties(): dev.kourier.amqp.Properties = properties {
    this@properties.contentType = this@toKourierProperties.contentType
    this@properties.contentEncoding = this@toKourierProperties.contentEncoding
    this@properties.headers = this@toKourierProperties.headers?.toTable()
    this@properties.deliveryMode = this@toKourierProperties.deliveryMode?.toUByte()
    this@properties.priority = this@toKourierProperties.priority?.toUByte()
    this@properties.correlationId = this@toKourierProperties.correlationId
    this@properties.replyTo = this@toKourierProperties.replyTo
    this@properties.expiration = this@toKourierProperties.expiration
    this@properties.messageId = this@toKourierProperties.messageId
    this@properties.timestamp = this@toKourierProperties.timestamp
    this@properties.type = this@toKourierProperties.type
    this@properties.userId = this@toKourierProperties.userId
    this@properties.appId = this@toKourierProperties.appId
    //this@properties.clusterId = this@toKourierProperties.clusterId // Not supported in Kourier
}

fun Delivery(original: AMQPResponse.Channel.Message.Delivery) = Delivery(
    envelope = Envelope(original.message),
    properties = Properties(original.message.properties),
    body = original.message.body,
)

fun GetResponse(original: AMQPResponse.Channel.Message.Get) = original.message?.let { message ->
    GetResponse(
        envelope = Envelope(message),
        props = Properties(message.properties),
        body = message.body,
        messageCount = original.messageCount.toInt(),
    )
} ?: error("GetResponse does not contain a message")

fun ExchangeDeclareOk(original: AMQPResponse.Channel.Exchange.Declared) = ExchangeDeclareOk

fun ExchangeDeleteOk(original: AMQPResponse.Channel.Exchange.Deleted) = ExchangeDeleteOk

fun QueueBindOk(original: AMQPResponse.Channel.Queue.Bound) = QueueBindOk

fun QueueUnbindOk(original: AMQPResponse.Channel.Queue.Unbound) = QueueUnbindOk

fun QueueDeclareOk(original: AMQPResponse.Channel.Queue.Declared) = QueueDeclareOk(
    queue = original.queueName,
    messageCount = original.messageCount.toInt(),
    consumerCount = original.consumerCount.toInt(),
)

fun QueueDeleteOk(original: AMQPResponse.Channel.Queue.Deleted) = QueueDeleteOk(
    messageCount = original.messageCount.toInt(),
)

fun ShutdownSignalException(
    original: AMQPException.ChannelClosed,
) = ShutdownSignalException(
    hardError = false,//original.isHardError,
    initiatedByApplication = original.isInitiatedByApplication,
    reason = original.message,
    ref = null//original.reference
)

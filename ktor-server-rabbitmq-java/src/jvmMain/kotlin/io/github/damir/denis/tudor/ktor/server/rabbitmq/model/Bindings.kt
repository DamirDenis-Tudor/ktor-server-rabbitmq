package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

import com.rabbitmq.client.AMQP

fun Envelope(original: com.rabbitmq.client.Envelope) = Envelope(
    deliveryTag = original.deliveryTag,
    isRedeliver = original.isRedeliver,
    exchange = original.exchange,
    routingKey = original.routingKey,
)

fun Properties(original: AMQP.BasicProperties) = Properties(
    contentType = original.contentType,
    contentEncoding = original.contentEncoding,
    headers = original.headers,
    deliveryMode = original.deliveryMode,
    priority = original.priority,
    correlationId = original.correlationId,
    replyTo = original.replyTo,
    expiration = original.expiration,
    messageId = original.messageId,
    timestamp = original.timestamp?.time,
    type = original.type,
    userId = original.userId,
    appId = original.appId,
    clusterId = original.clusterId,
)

fun Properties.toJavaProperties(): AMQP.BasicProperties = AMQP.BasicProperties.Builder().apply {
    contentType?.let { contentType(it) }
    contentEncoding?.let { contentEncoding(it) }
    headers?.let { headers(it) }
    deliveryMode?.let { deliveryMode(it) }
    priority?.let { priority(it) }
    correlationId?.let { correlationId(it) }
    replyTo?.let { replyTo(it) }
    expiration?.let { expiration(it) }
    messageId?.let { messageId(it) }
    timestamp?.let { timestamp(java.util.Date(it)) }
    type?.let { type(it) }
    userId?.let { userId(it) }
    appId?.let { appId(it) }
    clusterId?.let { clusterId(it) }
}.build()

fun Delivery(original: com.rabbitmq.client.Delivery) = Delivery(
    envelope = Envelope(original.envelope),
    properties = Properties(original.properties),
    body = original.body,
)

fun GetResponse(original: com.rabbitmq.client.GetResponse) = GetResponse(
    envelope = Envelope(original.envelope),
    props = Properties(original.props),
    body = original.body,
    messageCount = original.messageCount,
)

fun ExchangeDeclareOk(original: AMQP.Exchange.DeclareOk) = ExchangeDeclareOk

fun ExchangeDeleteOk(original: AMQP.Exchange.DeleteOk) = ExchangeDeleteOk

fun QueueBindOk(original: AMQP.Queue.BindOk) = QueueBindOk

fun QueueUnbindOk(original: AMQP.Queue.UnbindOk) = QueueUnbindOk

fun QueueDeclareOk(original: AMQP.Queue.DeclareOk) = QueueDeclareOk(
    queue = original.queue,
    messageCount = original.messageCount,
    consumerCount = original.consumerCount,
)

fun QueueDeleteOk(original: AMQP.Queue.DeleteOk) = QueueDeleteOk(
    messageCount = original.messageCount,
)

fun ShutdownSignalException(
    original: com.rabbitmq.client.ShutdownSignalException,
) = ShutdownSignalException(
    hardError = original.isHardError,
    initiatedByApplication = original.isInitiatedByApplication,
    reason = original.reason,
    ref = original.reference
)

package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

class JavaChannel(
    val channel: com.rabbitmq.client.Channel,
) : Channel {

    override val isOpen: Boolean
        get() = channel.isOpen

    override val closeReason: String?
        get() = channel.closeReason.message

    override suspend fun basicAck(deliveryTag: Long, multiple: Boolean) =
        channel.basicAck(deliveryTag, multiple)

    override suspend fun basicNack(deliveryTag: Long, multiple: Boolean, requeue: Boolean) =
        channel.basicNack(deliveryTag, multiple, requeue)

    override suspend fun basicReject(deliveryTag: Long, requeue: Boolean) =
        channel.basicReject(deliveryTag, requeue)

    override suspend fun basicGet(queue: String, autoAck: Boolean): GetResponse =
        channel.basicGet(queue, autoAck).let(::GetResponse)

    override suspend fun basicPublish(
        exchange: String,
        routingKey: String,
        mandatory: Boolean,
        immediate: Boolean,
        properties: Properties,
        message: ByteArray,
    ) = channel.basicPublish(
        exchange,
        routingKey,
        mandatory,
        immediate,
        properties.toJavaProperties(),
        message
    )

    override suspend fun basicConsume(
        queue: String,
        autoAck: Boolean,
        consumerTag: String,
        noLocal: Boolean,
        exclusive: Boolean,
        arguments: Map<String, Any>,
        deliverCallback: DeliverCallback,
        cancelCallback: CancelCallback,
        shutdownSignalCallback: ConsumerShutdownSignalCallback,
    ): String = channel.basicConsume(
        queue,
        autoAck,
        consumerTag,
        noLocal,
        exclusive,
        arguments,
        { tag, delivery ->
            deliverCallback.handle(tag, Delivery(delivery))
        },
        { tag ->
            cancelCallback.handle(tag)
        },
        { consumerTag, sig ->
            shutdownSignalCallback.handleShutdownSignal(consumerTag, ShutdownSignalException(sig))
        }
    )

    override suspend fun basicQos(prefetchSize: Int, prefetchCount: Int, global: Boolean) =
        channel.basicQos(prefetchSize, prefetchCount, global)

    override suspend fun exchangeDeclare(
        exchange: String,
        type: String,
        durable: Boolean,
        autoDelete: Boolean,
        internal: Boolean,
        arguments: Map<String, Any>,
    ): ExchangeDeclareOk = channel.exchangeDeclare(
        exchange,
        type,
        durable,
        autoDelete,
        internal,
        arguments
    ).let(::ExchangeDeclareOk)

    override suspend fun exchangeDelete(exchange: String, ifUnused: Boolean): ExchangeDeleteOk =
        channel.exchangeDelete(exchange, ifUnused).let(::ExchangeDeleteOk)

    override suspend fun messageCount(queue: String): Long =
        channel.messageCount(queue)

    override suspend fun consumerCount(queue: String): Long =
        channel.consumerCount(queue)

    override suspend fun queueBind(queue: String, exchange: String, routingKey: String, arguments: Map<String, Any>) =
        channel.queueBind(queue, exchange, routingKey, arguments).let(::QueueBindOk)

    override suspend fun queueUnbind(
        queue: String,
        exchange: String,
        routingKey: String,
        arguments: Map<String, Any>,
    ): QueueUnbindOk = channel.queueUnbind(
        queue,
        exchange,
        routingKey,
        arguments
    ).let(::QueueUnbindOk)

    override suspend fun queueDeclare(
        queue: String,
        durable: Boolean,
        exclusive: Boolean,
        autoDelete: Boolean,
        arguments: Map<String, Any>,
    ): QueueDeclareOk = channel.queueDeclare(
        queue,
        durable,
        exclusive,
        autoDelete,
        arguments
    ).let(::QueueDeclareOk)

    override suspend fun queueDelete(queue: String, ifUnused: Boolean, ifEmpty: Boolean): QueueDeleteOk =
        channel.queueDelete(queue, ifUnused, ifEmpty).let(::QueueDeleteOk)

    override suspend fun close() =
        channel.close()

}

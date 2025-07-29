package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

class JavaChannel(
    val channel: com.rabbitmq.client.Channel,
) : Channel {

    override val isOpen: Boolean
        get() = channel.isOpen

    override val closeReason: String?
        get() = channel.closeReason.message

    override fun basicAck(deliveryTag: Long, multiple: Boolean) =
        channel.basicAck(deliveryTag, multiple)

    override fun basicNack(deliveryTag: Long, multiple: Boolean, requeue: Boolean) =
        channel.basicNack(deliveryTag, multiple, requeue)

    override fun basicReject(deliveryTag: Long, requeue: Boolean) =
        channel.basicReject(deliveryTag, requeue)

    override fun basicGet(queue: String, autoAck: Boolean): GetResponse =
        channel.basicGet(queue, autoAck).let(::GetResponse)

    override fun basicPublish(
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

    override fun basicConsume(
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

    override fun basicQos(prefetchSize: Int, prefetchCount: Int, global: Boolean) =
        channel.basicQos(prefetchSize, prefetchCount, global)

    override fun exchangeDeclare(
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

    override fun exchangeDelete(exchange: String, ifUnused: Boolean): ExchangeDeleteOk =
        channel.exchangeDelete(exchange, ifUnused).let(::ExchangeDeleteOk)

    override fun messageCount(queue: String): Long =
        channel.messageCount(queue)

    override fun consumerCount(queue: String): Long =
        channel.consumerCount(queue)

    override fun queueBind(queue: String, exchange: String, routingKey: String, arguments: Map<String, Any>) =
        channel.queueBind(queue, exchange, routingKey, arguments).let(::QueueBindOk)

    override fun queueUnbind(
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

    override fun queueDeclare(
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

    override fun queueDelete(queue: String, ifUnused: Boolean, ifEmpty: Boolean): QueueDeleteOk =
        channel.queueDelete(queue, ifUnused, ifEmpty).let(::QueueDeleteOk)

    override fun close() =
        channel.close()

}

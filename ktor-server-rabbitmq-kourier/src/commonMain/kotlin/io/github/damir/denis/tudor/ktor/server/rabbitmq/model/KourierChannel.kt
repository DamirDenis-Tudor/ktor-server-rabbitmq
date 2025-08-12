package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

import dev.kourier.amqp.channel.AMQPChannel
import dev.kourier.amqp.connection.ConnectionState
import dev.kourier.amqp.toTable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

class KourierChannel(
    val channel: AMQPChannel,
) : Channel {

    override val isOpen: Boolean
        get() = channel.state == ConnectionState.CLOSED

    @OptIn(ExperimentalCoroutinesApi::class)
    override val closeReason: String?
        get() = runCatching { channel.channelClosed.getCompleted() }.getOrNull()?.message

    override suspend fun basicAck(deliveryTag: Long, multiple: Boolean) =
        channel.basicAck(deliveryTag.toULong(), multiple)

    override suspend fun basicNack(deliveryTag: Long, multiple: Boolean, requeue: Boolean) =
        channel.basicNack(deliveryTag.toULong(), multiple, requeue)

    override suspend fun basicReject(deliveryTag: Long, requeue: Boolean) =
        channel.basicReject(deliveryTag.toULong(), requeue)

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
        message,
        exchange,
        routingKey,
        mandatory,
        immediate,
        properties.toKourierProperties(),
    ).let {}

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
    ): String {
        val deferredConsumerTag = CompletableDeferred<String>()
        val consumerTag = channel.basicConsume(
            queue,
            consumerTag,
            autoAck,
            //noLocal,
            exclusive,
            arguments.toTable(),
            onDelivery = {
                deliverCallback.handle(deferredConsumerTag.await(), Delivery(it))
            },
            onCanceled = {
                cancelCallback.handle(deferredConsumerTag.await())
            },
            /*
            { consumerTag, sig ->
                shutdownSignalCallback.handleShutdownSignal(consumerTag, ShutdownSignalException(sig))
            }
             */
        ).consumerTag
        deferredConsumerTag.complete(consumerTag)
        return consumerTag
    }

    override suspend fun basicQos(prefetchSize: Int, prefetchCount: Int, global: Boolean) =
        channel.basicQos(prefetchCount.toUShort(), global).let {}

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
        arguments.toTable()
    ).let(::ExchangeDeclareOk)

    override suspend fun exchangeDelete(exchange: String, ifUnused: Boolean): ExchangeDeleteOk =
        channel.exchangeDelete(exchange, ifUnused).let(::ExchangeDeleteOk)

    override suspend fun messageCount(queue: String): Long =
        channel.messageCount(queue).toLong()

    override suspend fun consumerCount(queue: String): Long =
        channel.consumerCount(queue).toLong()

    override suspend fun queueBind(queue: String, exchange: String, routingKey: String, arguments: Map<String, Any>) =
        channel.queueBind(queue, exchange, routingKey, arguments.toTable()).let(::QueueBindOk)

    override suspend fun queueUnbind(
        queue: String,
        exchange: String,
        routingKey: String,
        arguments: Map<String, Any>,
    ): QueueUnbindOk = channel.queueUnbind(
        queue,
        exchange,
        routingKey,
        arguments.toTable()
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
        arguments.toTable()
    ).let(::QueueDeclareOk)

    override suspend fun queueDelete(queue: String, ifUnused: Boolean, ifEmpty: Boolean): QueueDeleteOk =
        channel.queueDelete(queue, ifUnused, ifEmpty).let(::QueueDeleteOk)

    override suspend fun close() =
        channel.close().let {}

}

package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

interface Channel {

    val isOpen: Boolean
    val closeReason: String?

    fun basicAck(deliveryTag: Long, multiple: Boolean)

    fun basicNack(deliveryTag: Long, multiple: Boolean, requeue: Boolean)

    fun basicReject(deliveryTag: Long, requeue: Boolean)

    fun basicGet(queue: String, autoAck: Boolean): GetResponse

    fun basicPublish(
        exchange: String,
        routingKey: String,
        mandatory: Boolean = false,
        immediate: Boolean = false,
        properties: Properties = Properties(),
        message: ByteArray,
    )

    fun basicConsume(
        queue: String,
        autoAck: Boolean,
        consumerTag: String = "",
        noLocal: Boolean = false,
        exclusive: Boolean = false,
        arguments: Map<String, Any> = emptyMap(),
        deliverCallback: DeliverCallback,
        cancelCallback: CancelCallback = CancelCallback { },
        shutdownSignalCallback: ConsumerShutdownSignalCallback = ConsumerShutdownSignalCallback { _, _ -> },
    ): String

    fun basicQos(prefetchSize: Int = 0, prefetchCount: Int, global: Boolean = false)

    fun exchangeDeclare(
        exchange: String,
        type: String,
        durable: Boolean,
        autoDelete: Boolean,
        internal: Boolean,
        arguments: Map<String, Any>,
    ): ExchangeDeclareOk

    fun exchangeDelete(exchange: String, ifUnused: Boolean): ExchangeDeleteOk

    fun messageCount(queue: String): Long

    fun consumerCount(queue: String): Long

    fun queueBind(
        queue: String,
        exchange: String,
        routingKey: String,
        arguments: Map<String, Any> = emptyMap(),
    ): QueueBindOk

    fun queueUnbind(
        queue: String,
        exchange: String,
        routingKey: String,
        arguments: Map<String, Any> = emptyMap(),
    ): QueueUnbindOk

    fun queueDeclare(
        queue: String,
        durable: Boolean,
        exclusive: Boolean,
        autoDelete: Boolean,
        arguments: Map<String, Any> = emptyMap(),
    ): QueueDeclareOk

    fun queueDelete(queue: String, ifUnused: Boolean = false, ifEmpty: Boolean = false): QueueDeleteOk

    fun close()

}

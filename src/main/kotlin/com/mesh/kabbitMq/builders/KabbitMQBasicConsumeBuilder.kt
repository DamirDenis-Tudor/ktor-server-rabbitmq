package com.mesh.kabbitMq.builders

import com.rabbitmq.client.*

class KabbitMQBasicConsumeBuilder(private val channel: Channel) {

    lateinit var queue: String
    var autoAck: Boolean = true
    lateinit var consumerTag: String
    var noLocal: Boolean = false
    var exclusive: Boolean = false
    var arguments: Map<String, Any> = emptyMap()
    lateinit var callback: Consumer
    lateinit var deliverCallback: DeliverCallback
    lateinit var cancelCallback: CancelCallback
    lateinit var shutdownSignalCallback: ConsumerShutdownSignalCallback

    fun build(): String {
        if (!::queue.isInitialized) error("Queue must be provided")

        return when {
            ::callback.isInitialized -> {
                channel.basicConsume(queue, autoAck, callback)
            }

            ::deliverCallback.isInitialized && ::cancelCallback.isInitialized -> {
                channel.basicConsume(queue, autoAck, deliverCallback, cancelCallback)
            }

            ::deliverCallback.isInitialized && ::shutdownSignalCallback.isInitialized -> {
                channel.basicConsume(queue, autoAck, deliverCallback, shutdownSignalCallback)
            }

            ::deliverCallback.isInitialized && ::cancelCallback.isInitialized && ::shutdownSignalCallback.isInitialized -> {
                channel.basicConsume(queue, autoAck, deliverCallback, cancelCallback, shutdownSignalCallback)
            }

            ::deliverCallback.isInitialized && ::cancelCallback.isInitialized && arguments.isNotEmpty() -> {
                channel.basicConsume(queue, autoAck, arguments, deliverCallback, cancelCallback)
            }

            ::deliverCallback.isInitialized && ::shutdownSignalCallback.isInitialized && arguments.isNotEmpty() -> {
                channel.basicConsume(queue, autoAck, arguments, deliverCallback, shutdownSignalCallback)
            }

            ::deliverCallback.isInitialized && ::cancelCallback.isInitialized && ::shutdownSignalCallback.isInitialized && arguments.isNotEmpty() -> {
                channel.basicConsume(queue, autoAck, arguments, deliverCallback, cancelCallback, shutdownSignalCallback)
            }

            ::consumerTag.isInitialized -> {
                channel.basicConsume(queue, autoAck, consumerTag, deliverCallback, cancelCallback)
            }

            ::consumerTag.isInitialized && ::shutdownSignalCallback.isInitialized -> {
                channel.basicConsume(queue, autoAck, consumerTag, deliverCallback, shutdownSignalCallback)
            }

            ::consumerTag.isInitialized && ::deliverCallback.isInitialized && ::cancelCallback.isInitialized && ::shutdownSignalCallback.isInitialized && arguments.isNotEmpty() -> {
                channel.basicConsume(queue, autoAck, consumerTag, noLocal, exclusive, arguments, deliverCallback, cancelCallback, shutdownSignalCallback)
            }

            else -> error("Unsupported combination of parameters for basicConsume.")
        }
    }
}

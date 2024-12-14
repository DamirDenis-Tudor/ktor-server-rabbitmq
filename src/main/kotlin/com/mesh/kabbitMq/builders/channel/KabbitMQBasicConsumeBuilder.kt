package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.mesh.kabbitMq.util.StateDelegator.Companion.withThisRef
import com.rabbitmq.client.*
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.ApiStatus.Internal

@KabbitMQDslMarker
class KabbitMQBasicConsumeBuilder(
    private val channel: Channel,
) {

    var noLocal: Boolean = false
    var exclusive: Boolean = false
    var arguments: Map<String, Any> = emptyMap()

    var autoAck: Boolean by StateDelegator()
    var queue: String by StateDelegator()
    var consumerTag: String by StateDelegator()

    var deliverCallback: DeliverCallback by StateDelegator()
    private var cancelCallback: CancelCallback by StateDelegator()
    private var shutdownSignalCallback: ConsumerShutdownSignalCallback by StateDelegator()


    init {
        cancelCallback { tag ->
            println("Consumer with tag: $tag cancelled")
        }
    }

    @KabbitMQDslMarker
    inline fun <reified T> deliverCallback(crossinline callback: (tag: Long, message: T) -> Unit) {
        deliverCallback = DeliverCallback { _, delivery ->
            callback(
                delivery.envelope.deliveryTag,
                Json.decodeFromString<T>(
                    delivery.body.toString(Charsets.UTF_8)
                )
            )
        }
    }

    @KabbitMQDslMarker
    fun cancelCallback(callback: (tag: String) -> Unit) {
        cancelCallback = CancelCallback { consumerTag ->
            callback(consumerTag)
        }
    }

    @KabbitMQDslMarker
    fun shutdownSignalCallback(callback: (tag: String, sig: ShutdownSignalException) -> Unit) {
        shutdownSignalCallback = ConsumerShutdownSignalCallback { consumerTag, sig ->
            callback(consumerTag, sig)
        }
    }

    fun build() {
        return withThisRef(this@KabbitMQBasicConsumeBuilder) {
            when {
                initialized(::consumerTag, ::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        consumerTag,
                        deliverCallback,
                        cancelCallback
                    )
                }

                initialized(::consumerTag, ::deliverCallback, ::cancelCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        consumerTag,
                        noLocal,
                        exclusive,
                        arguments,
                        deliverCallback,
                        cancelCallback,
                        shutdownSignalCallback
                    )
                }

                initialized(::deliverCallback, ::cancelCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        arguments,
                        deliverCallback,
                        cancelCallback,
                        shutdownSignalCallback
                    )
                }

                initialized(::consumerTag, ::deliverCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        consumerTag,
                        deliverCallback,
                        shutdownSignalCallback
                    )
                }

                initialized(::deliverCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        arguments,
                        deliverCallback,
                        shutdownSignalCallback
                    )
                }

                initialized(::deliverCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        deliverCallback,
                        shutdownSignalCallback
                    )
                }

                initialized(::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        deliverCallback,
                        cancelCallback
                    )
                }

                initialized(::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        deliverCallback,
                        cancelCallback
                    )
                }

                initialized(::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(
                        queue,
                        autoAck,
                        arguments,
                        deliverCallback,
                        cancelCallback
                    )
                }


                else -> {
                    stateTrace().let { println(it) }
                    error("Unsupported combination of parameters for basicConsume.")
                }
            }
        }
    }
}
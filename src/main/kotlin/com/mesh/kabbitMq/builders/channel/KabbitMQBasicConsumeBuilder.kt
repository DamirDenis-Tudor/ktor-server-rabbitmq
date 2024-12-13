package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.mesh.kabbitMq.util.StateDelegator.Companion.withThisRef
import com.rabbitmq.client.*
import kotlin.reflect.full.memberProperties

@KabbitMQDslMarker
class KabbitMQBasicConsumeBuilder(private val channel: Channel) {

    var autoAck: Boolean = true
    var noLocal: Boolean = false
    var exclusive: Boolean = false
    var arguments: Map<String, Any> = emptyMap()

    var queue: String by StateDelegator()
    var consumerTag: String by StateDelegator()
    var callback: Consumer by StateDelegator()
    var deliverCallback: DeliverCallback by StateDelegator()
    var cancelCallback: CancelCallback by StateDelegator()
    var shutdownSignalCallback: ConsumerShutdownSignalCallback by StateDelegator()

    fun build() {
        return withThisRef(this@KabbitMQBasicConsumeBuilder){
            when {
                initialized(::queue, ::callback) -> {
                    channel.basicConsume(queue, autoAck, callback)
                }

                initialized(::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(queue, autoAck, deliverCallback, cancelCallback)
                }

                initialized(::deliverCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(queue, autoAck, deliverCallback, shutdownSignalCallback)
                }

                initialized(::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(queue, autoAck, deliverCallback, cancelCallback, shutdownSignalCallback)
                }

                initialized(::deliverCallback, ::cancelCallback) -> {
                    channel.basicConsume(queue, autoAck, arguments, deliverCallback, cancelCallback)
                }

                initialized(::deliverCallback, ::shutdownSignalCallback)  -> {
                    channel.basicConsume(queue, autoAck, arguments, deliverCallback, shutdownSignalCallback)
                }

                initialized(::deliverCallback, ::cancelCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(queue, autoAck, arguments, deliverCallback, cancelCallback, shutdownSignalCallback)
                }

                initialized(::consumerTag) -> {
                    channel.basicConsume(queue, autoAck, consumerTag, deliverCallback, cancelCallback)
                }

                initialized(::consumerTag, ::shutdownSignalCallback) -> {
                    channel.basicConsume(queue, autoAck, consumerTag, deliverCallback, shutdownSignalCallback)
                }

                initialized(::consumerTag, ::deliverCallback, ::cancelCallback, ::shutdownSignalCallback) -> {
                    channel.basicConsume(queue, autoAck, consumerTag, noLocal, exclusive, arguments, deliverCallback, cancelCallback, shutdownSignalCallback)
                }

                else -> {
                    stateTrace(this@KabbitMQBasicConsumeBuilder).let { println(it) }
                    error("Unsupported combination of parameters for basicConsume.")
                }
            }
        }
    }
}

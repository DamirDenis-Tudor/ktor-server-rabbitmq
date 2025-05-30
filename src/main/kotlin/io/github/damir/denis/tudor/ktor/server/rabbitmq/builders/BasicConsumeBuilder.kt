package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders


import com.rabbitmq.client.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Message
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@OptIn(InternalAPI::class)
@RabbitDslMarker
class BasicConsumeBuilder(
    val connectionManager: ConnectionManager,
    private val channel: Channel,
) {
    val defaultLogger = KtorSimpleLogger(this.javaClass.name)

    var noLocal: Boolean by Delegator()
    var exclusive: Boolean by Delegator()
    var arguments: Map<String, Any> by Delegator()

    var autoAck: Boolean by Delegator()
    var queue: String by Delegator()
    var consumerTag: String by Delegator()

    private var deliverCallback: DeliverCallback by Delegator()
    private var cancelCallback: CancelCallback by Delegator()
    private var shutdownSignalCallback: ConsumerShutdownSignalCallback by Delegator()

    var dispatcher: CoroutineDispatcher = Dispatchers.rabbitMQ
    var coroutinePollSize: Int = 1

    @InternalAPI
    var receiverChannel = kotlinx.coroutines.channels.Channel<Pair<String, Delivery>>(
        connectionManager.configuration.consumerChannelCoroutineSize
    )

    @InternalAPI
    var failureCallbackDefined = false

    @InternalAPI
    var receiverFailChannel = kotlinx.coroutines.channels.Channel<Pair<String, Delivery>>(
        connectionManager.configuration.consumerChannelCoroutineSize
    )

    init {
        noLocal = false
        exclusive = false
        arguments = emptyMap()
        deliverCallback = DeliverCallback { consumerTag, delivery ->
            receiverChannel.trySendBlocking(consumerTag to delivery)
        }
        cancelCallback = CancelCallback { }
        shutdownSignalCallback = ConsumerShutdownSignalCallback { _, error -> }
    }

    @RabbitDslMarker
    @Deprecated(
        message = "Use deliverCallback with Message<T> parameter for full access to properties and envelope.",
        level = DeprecationLevel.WARNING
    )
    inline fun <reified T> deliverCallback(crossinline callback: suspend (tag: Long, message: T) -> Unit) {
        repeat(coroutinePollSize) {
            connectionManager.coroutineScope.launch(dispatcher) {
                receiverChannel.consumeAsFlow().collect { (consumerTag, delivery) ->
                    runCatching {
                        when (T::class) {
                            String::class -> String(delivery.body) as T
                            ByteArray::class -> delivery.body as T

                            else -> Json.decodeFromString<T>(String(delivery.body))
                        }
                    }.onFailure { error ->
                        defaultLogger.error(error)
                        if (failureCallbackDefined){
                            receiverFailChannel.trySendBlocking(consumerTag to delivery)
                        }
                    }.getOrNull()?.let { message ->
                        callback(delivery.envelope.deliveryTag, message)
                    }
                }
            }
        }
    }

    @RabbitDslMarker
    inline fun <reified T> deliverCallback(crossinline callback: suspend (message: Message<T>) -> Unit) {
        repeat(coroutinePollSize) {
            connectionManager.coroutineScope.launch(dispatcher) {
                receiverChannel.consumeAsFlow().collect { (consumerTag, delivery) ->
                    runCatching {
                         when (T::class) {
                            String::class -> String(delivery.body) as T
                            ByteArray::class -> delivery.body as T

                            else -> Json.decodeFromString<T>(String(delivery.body))
                        }
                    }.onFailure { error ->
                        defaultLogger.error(error)
                        if (failureCallbackDefined){
                            receiverFailChannel.trySendBlocking(consumerTag to delivery)
                        }
                    }.getOrNull()?.let { message ->
                        callback(
                            Message(
                                body = message,
                                consumerTag = consumerTag,
                                envelope = delivery.envelope,
                                properties = delivery.properties
                            )
                        )
                    }
                }
            }
        }
    }

    @RabbitDslMarker
    @Deprecated(
        message = "Use deliverFailureCallback with Message<ByteArray> parameter for full access to properties and envelope.",
        level = DeprecationLevel.WARNING
    )
    fun deliverFailureCallback(callback: suspend (tag: Long, message: ByteArray) -> Unit) {
        failureCallbackDefined = true
        connectionManager.coroutineScope.launch(dispatcher) {
            receiverFailChannel.consumeAsFlow().collect { (_, delivery) ->
                callback(delivery.envelope.deliveryTag, delivery.body)
            }
        }
    }

    @RabbitDslMarker
    fun deliverFailureCallback(callback: suspend (message: Message<ByteArray>) -> Unit) {
        failureCallbackDefined = true
        connectionManager.coroutineScope.launch(dispatcher) {
            receiverFailChannel.consumeAsFlow().collect { (consumerTag, delivery) ->
                callback(
                    Message(
                        body = delivery.body,
                        consumerTag = consumerTag,
                        envelope = delivery.envelope,
                        properties = delivery.properties
                    )
                )
            }
        }
    }

    @RabbitDslMarker
    fun cancelCallback(callback: (tag: String) -> Unit) {
        cancelCallback = CancelCallback { consumerTag ->
            callback(consumerTag)
        }
    }

    @RabbitDslMarker
    fun shutdownSignalCallback(callback: (tag: String, sig: ShutdownSignalException) -> Unit) {
        shutdownSignalCallback = ConsumerShutdownSignalCallback { consumerTag, sig ->
            callback(consumerTag, sig)
        }
    }

    fun build(): String = delegatorScope(on = this@BasicConsumeBuilder) {
        return@delegatorScope when {
            verify(
                ::queue,
                ::autoAck,
                ::consumerTag,
                ::noLocal,
                ::exclusive,
                ::arguments,
                ::deliverCallback,
                ::cancelCallback,
                ::shutdownSignalCallback
            ) -> {
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

            verify(::queue, ::autoAck, ::arguments, ::deliverCallback, ::cancelCallback, ::shutdownSignalCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    arguments,
                    deliverCallback,
                    cancelCallback,
                    shutdownSignalCallback
                )
            }

            verify(::queue, ::autoAck, ::consumerTag, ::deliverCallback, ::cancelCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    consumerTag,
                    deliverCallback,
                    cancelCallback
                )
            }


            verify(::queue, ::autoAck, ::consumerTag, ::deliverCallback, ::shutdownSignalCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    consumerTag,
                    deliverCallback,
                    shutdownSignalCallback
                )
            }

            verify(::queue, ::autoAck, ::arguments, ::deliverCallback, ::shutdownSignalCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    arguments,
                    deliverCallback,
                    shutdownSignalCallback
                )
            }

            verify(::queue, ::autoAck, ::arguments, ::deliverCallback, ::cancelCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    arguments,
                    deliverCallback,
                    cancelCallback
                )
            }

            verify(::queue, ::autoAck, ::deliverCallback, ::shutdownSignalCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    deliverCallback,
                    shutdownSignalCallback
                )
            }

            verify(::queue, ::autoAck, ::deliverCallback, ::cancelCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    deliverCallback,
                    cancelCallback
                )
            }

            verify(::queue, ::autoAck, ::deliverCallback, ::cancelCallback) -> {
                channel.basicConsume(
                    queue,
                    autoAck,
                    deliverCallback,
                    cancelCallback
                )
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}

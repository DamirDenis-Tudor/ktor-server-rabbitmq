package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel

/**
 * Represents a context for a RabbitMQ channel, providing a scope for performing
 * operations such as message publishing, queue management, and acknowledgments.
 *
 * @property connectionManager The connection manager handling connections and channels.
 * @property channel The RabbitMQ channel associated with this context.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
open class ChannelContext(
    open val connectionManager: ConnectionManager,
    val channel: Channel,
)

/**
 * Acknowledges a message using a builder pattern for customizable parameters.
 *
 * @param block A configuration block for setting up the acknowledgment parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicAck(block: suspend BasicAckBuilder.() -> Unit) =
    BasicAckBuilder(channel).apply { this.block() }.build()

/**
 * Consumes messages from a queue using a builder for configurable options.
 *
 * Below are some examples of what *not* to do:
 *
 * 1. **Using `BasicReject` with `autoAck = true`**
 *    - This will throw an exception because `autoAck = true` means the message is automatically acknowledged.
 *      As a result, the delivery tag for the current message will no longer exist, causing the rejection to fail.
 *
 * ```kotlin
 * rabbitmq {
 *     channel(id = 10) {
 *         basicConsume {
 *             autoAck = true
 *             queue = "..."
 *             deliverCallback<String> { tag, message ->
 *                 // ...
 *                 basicReject {
 *                     deliveryTag = tag
 *                     requeue = false
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 2. **Consuming on a channel with `autoClose = false`**
 *    - If `autoClose = false`, the delivery callback is launched in a coroutine. However, the parent coroutine will
 *      finish its job and close the channel before the delivery callback can execute.
 *
 * ```kotlin
 * rabbitmq {
 *     channel(id = 10, autoClose = true) {
 *         basicConsume {
 *             autoAck = true
 *             queue = "..."
 *             deliverCallback<String> { tag, message ->
 *                 // This won't execute
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param block A configuration block for setting up the consumption parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicConsume(block: suspend BasicConsumeBuilder.() -> Unit) =
    BasicConsumeBuilder(connectionManager, channel).apply { this.block() }.build()

/**
 * Retrieves a single message from a queue using a builder for customization.
 *
 * @param block A configuration block for setting up the retrieval parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicGet(block: suspend BasicGetBuilder.() -> Unit) =
    BasicGetBuilder(channel).apply { this.block() }.build()

/**
 * Negatively acknowledges a message using a builder for customizable parameters.
 *
 * @param block A configuration block for setting up the negative acknowledgment parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicNack(block: suspend BasicNackBuilder.() -> Unit) =
    BasicNackBuilder(channel).apply { this.block() }.build()

/**
 * Publishes a message to an exchange using a builder for setting up the message properties.
 *
 * @param block A configuration block for setting up the message publishing parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicPublish(block: suspend BasicPublishBuilder.() -> Unit) =
    BasicPublishBuilder(channel).apply { this.block() }.build()

/**
 * Configures and builds the basic properties for a message using a builder pattern.
 *
 * This function allows customization of AMQP basic properties (e.g., headers, content type)
 * by applying the provided [block] to a [io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicPropertiesBuilder].
 *
 * @param block A suspendable configuration block used to customize the [io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicPropertiesBuilder].
 * @return The built AMQP basic properties instance.
 *
 * @see io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicPropertiesBuilder
 * @see com.rabbitmq.client.AMQP.BasicProperties
 *
 * @author Damir Denis-Tudor
 * @since 1.3.6
 */
@RabbitDslMarker
suspend fun basicProperties(block: suspend BasicPropertiesBuilder.() -> Unit) =
    BasicPropertiesBuilder().apply { this.block() }.build()

/**
 * Configures the Quality of Service (QoS) settings for the channel using a builder.
 *
 * @param block A configuration block for setting up the QoS parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicQos(block: suspend BasicQosBuilder.() -> Unit) =
    BasicQosBuilder(channel).apply { this.block() }.build()

/**
 * Rejects a message using a builder for customizable parameters.
 *
 * @param block A configuration block for setting up the rejection parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.basicReject(block: suspend BasicRejectBuilder.() -> Unit) =
    BasicRejectBuilder(channel).apply { this.block() }.build()

/**
 * Retrieves the consumer count for a queue using a builder for setup.
 *
 * @param block A configuration block for setting up the consumer count parameters.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.consumerCount(block: suspend ConsumerCountBuilder.() -> Unit) =
    ConsumerCountBuilder(channel).apply { this.block() }.build()

/**
 * Declares an exchange using a builder for customizable properties.
 *
 * @param block A configuration block for setting up the exchange declaration.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.exchangeDeclare(block: suspend ExchangeDeclareBuilder.() -> Unit) =
    ExchangeDeclareBuilder(channel).apply { this.block() }.build()

/**
 * Deletes an exchange using a builder for setup.
 *
 * @param block A configuration block for setting up the exchange deletion.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.exchangeDelete(block: suspend ExchangeDeleteBuilder.() -> Unit) =
    ExchangeDeleteBuilder(channel).apply { this.block() }.build()

/**
 * Retrieves the message count for a queue using a builder for setup.
 *
 * @param block A configuration block for setting up the message count retrieval.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.messageCount(block: suspend MessageCountBuilder.() -> Unit) =
    MessageCountBuilder(channel).apply { this.block() }.build()

/**
 * Binds a queue to an exchange using a builder for customizable parameters.
 *
 * @param block A configuration block for setting up the queue binding.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.queueBind(block: suspend QueueBindBuilder.() -> Unit) =
    QueueBindBuilder(channel).apply { this.block() }.build()


/**
 * Declares a queue using a builder for customizable properties.
 *
 * @param block A configuration block for setting up the queue declaration.
 */
@RabbitDslMarker
suspend fun ChannelContext.queueDeclare(block: suspend QueueDeclareBuilder.() -> Unit) =
    QueueDeclareBuilder(channel).apply { this.block() }.build()


/**
 * Deletes a queue using a builder for setup.
 *
 * @param block A configuration block for setting up the queue deletion.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.queueDelete(block: suspend QueueDeleteBuilder.() -> Unit) =
    QueueDeleteBuilder(channel).apply { this.block() }.build()

/**
 * Unbinds a queue from an exchange using a builder for customizable parameters.
 *
 * @param block A configuration block for setting up the queue unbinding.
 *
 * @author Damir Denis-Tudor
 * @since 1.2.3
 */
@RabbitDslMarker
suspend fun ChannelContext.queueUnbind(block: suspend QueueUnbindBuilder.() -> Unit) =
    QueueUnbindBuilder(channel).apply { this.block() }.build()

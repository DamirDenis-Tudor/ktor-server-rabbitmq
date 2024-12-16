package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicConsumeBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

@KabbitMQDslMarker
inline fun Application.basicConsume(block: KabbitMQBasicConsumeBuilder.() -> Unit) =
    KabbitMQBasicConsumeBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

@KabbitMQDslMarker
inline fun Channel.basicConsume(block: KabbitMQBasicConsumeBuilder.() -> Unit) =
    KabbitMQBasicConsumeBuilder(this).apply(block).build()
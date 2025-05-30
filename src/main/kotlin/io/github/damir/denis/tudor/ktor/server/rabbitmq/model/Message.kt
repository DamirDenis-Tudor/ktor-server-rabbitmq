package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Envelope

data class Message<T>(
    val body: T,
    val envelope: Envelope,
    val consumerTag: String,
    val properties: AMQP.BasicProperties,
)
package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

data class Message<T>(
    val body: T,
    val envelope: Envelope,
    val consumerTag: String,
    val properties: Properties,
)

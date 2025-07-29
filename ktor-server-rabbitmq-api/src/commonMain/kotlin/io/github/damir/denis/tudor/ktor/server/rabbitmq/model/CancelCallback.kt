package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

fun interface CancelCallback {

    fun handle(consumerTag: String)

}

package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

fun interface DeliverCallback {

    fun handle(consumerTag: String, message: Delivery)

}

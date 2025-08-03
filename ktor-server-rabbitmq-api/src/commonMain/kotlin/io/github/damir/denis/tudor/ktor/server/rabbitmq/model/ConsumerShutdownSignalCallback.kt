package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

fun interface ConsumerShutdownSignalCallback {

    fun handleShutdownSignal(consumerTag: String, sig: ShutdownSignalException)

}

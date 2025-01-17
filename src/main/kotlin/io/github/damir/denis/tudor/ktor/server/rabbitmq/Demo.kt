package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.rabbitmq
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun Application.module() {
    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        dispatcherThreadPollSize = 3
        connectionAttempts = 1
    }

    rabbitmq {
        channel(id = 12, autoClose = true) {
            queueBind {
                queue = "demo-queue"
                exchange = "demo-exchange"
                routingKey = "demo-routing-key"
                queueDeclare {
                    queue = "demo-queue"
                    durable = true
                }
                exchangeDeclare {
                    exchange = "demo-exchange"
                    type = "direct"
                }
            }.onSuccess {
                log.info("finsih")
            }
        }
    }

    rabbitmq {
        channel(id = 11, autoClose = true) {
            repeat(10_000_000) { index ->
                basicPublish {
                    exchange = "demo-exchange"
                    routingKey = "demo-routing-key"
                    message { "Hello World! $index" }
                }.onSuccess { message ->
                    log.debug("$index")
                }
            }
        }
    }

    rabbitmq {
        channel(id = 10, autoClose = true) {
            basicConsume {
                autoAck = true
                queue = "demo-queue"
                deliverCallback<String> { tag, message ->
                    log.debug("$tag: $message")
                }
            }
        }

        basicConsume {
            autoAck = true
            queue = "demo-queue"
            dispatcher = Dispatchers.IO
            deliverCallback<String> { tag, message ->
                log.debug("$tag: $message")
            }
        }
    }
}

internal fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
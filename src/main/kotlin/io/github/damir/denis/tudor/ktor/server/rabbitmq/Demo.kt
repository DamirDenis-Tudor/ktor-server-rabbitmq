package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.rabbitmq
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable

internal fun Application.module1() {
    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        dispatcherThreadPollSize = 3
    }

    rabbitmq {
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
        }.fold(
            onSuccess = {
                log.info("Queue binding success")
            },
            onFailure = { error ->
                log.error("Queue binding success", error)
            }
        )
    }

    rabbitmq {
        channel(id = 100, autoClose = true) {
            repeat(1_000_000) { index ->
                basicPublish {
                    exchange = "demo-exchange"
                    routingKey = "demo-routing-key"
                    message { "Hello World! $index" }
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

@Serializable
data class Message(
    var content: String
)

fun Application.module() {
    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        dispatcherThreadPollSize = 3
    }

    rabbitmq {
        queueBind {
            queue = "dlq"
            exchange = "dlx"
            routingKey = "dlq-dlx"
            queueDeclare {
                queue = "dlq"
                durable = true
            }
            exchangeDeclare {
                exchange = "dlx"
                type = "direct"
            }
        }

        queueBind {
            queue = "test-queue"
            exchange = "test-exchange"
            queueDeclare {
                queue = "test-queue"
                arguments = mapOf(
                    "x-dead-letter-exchange" to "dlx",
                    "x-dead-letter-routing-key" to "dlq-dlx"
                )
            }
            exchangeDeclare {
                exchange = "test-exchange"
                type = "fanout"
            }
        }
    }

    rabbitmq {
        repeat(100) {
            basicPublish {
                exchange = "test-exchange"
                message {
                    Message(content = "Hello world!")
                }
            }
        }
    }

    rabbitmq{
        basicConsume {
            queue = "test-queue"
            autoAck = false
            deliverCallback<Message> { tag, message ->
                basicReject {
                    deliveryTag = tag
                    requeue = false
                }
            }
        }

        basicConsume {
            queue = "dlq"
            autoAck = true
            deliverCallback<Message> { tag, message ->
                println("Received message in dead letter queue: $message")
            }
        }
    }
}

internal fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
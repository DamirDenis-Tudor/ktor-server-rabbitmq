package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.rabbitmq
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

internal fun Application.module() {
    val logger = KtorSimpleLogger("demo")

    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        dispatcherThreadPollSize = 2
    }

    Thread.sleep(2_000)

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
        }
    }

    rabbitmq {
        repeat(1_000_000) {
            basicPublish {
                exchange = "demo-exchange"
                routingKey = "demo-routing-key"
                message { "Hello World!" }
                logger.info("Publish")
            }
        }
    }

    rabbitmq {
        basicConsume {
            autoAck = true
            queue = "demo-queue"
            deliverCallback<String> { tag, message ->
                logger.info("Received message: $message")
            }
        }
    }
}

internal fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
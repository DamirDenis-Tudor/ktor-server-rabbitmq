package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.basicPublish
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.exchangeDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueBind
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.rabbitmq
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

internal fun Application.module() {
    install(RabbitMQ){
        uri = "amqp://guest:guest@localhost:5672"
    }

    routing {
        rabbitmq {

            get {
                channel(id = 0) {

                }

            }
        }
        rabbitmq {
            basicPublish {
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

                exchange = "demo-exchange"
                routingKey = "demo-routing-key"
                message { "Hello World!" }
            }
        }
    }

}

internal fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
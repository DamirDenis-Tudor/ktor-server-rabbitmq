# ![KabbitMQ](https://github.com/user-attachments/assets/bc22917b-d6bd-4f34-8775-707e575677a0)abbitMQ 

![Deployment Status](https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq/actions/workflows/deployment.yml/badge.svg) | ![Pull Request Checks](https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq/actions/workflows/pull-request-checks.yml/badge.svg)

### Overview 
- This plugin provides access to major core functionalities of the `com.rabbitmq:amqp-client` library.

### Features

- Integrated with coroutines and has a separate dispatcher.
- Seamlessly integrates with the Kotlin DSL, making it readable, maintainable, and easy to use.
- Includes a built-in connection/channel management system.
- Provides a built-in mechanism for validating property combinations.
- Gives the possibility to interact directly with the java library.

### Gradle (Kotlin DSL) - dependencies

```kotlin
dependencies {
    implementation("io.github.damirdenis-tudor:ktor-server-rabbitmq:1.3.0")
}
```

### Table of Contents
1. [Installation](#installation)
2. [Queue Binding Example](#queue-binding-example)
3. [Producer Example](#producer-example)
4. [Consumer Example](#consumer-example)
5. [Library Calls Example](#library-calls-example)
6. [Dead Letter Queue Example](#dead-letter-queue-example)
7. [Logging](#logging)



### Installation
```kotlin
install(KabbitMQ) {
    uri = "amqp://<user>:<password>@<address>:<port>"
    defaultConnectionName = "<default_connection>"
    connectionAttempts = 20
    attemptDelay = 10
    dispatcherThreadPollSize = 2

    tlsEnabled = true
    tlsKeystorePath = "<path>"
    tlsKeystorePassword = "<password>"
    tlsTruststorePath = "<path>"
    tlsTruststorePassword = "<password>"
}
```

### Queue Binding Example
```kotlin
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
```

### Producer Example
```kotlin
rabbitmq {
    repeat(10) {
        basicPublish {
            exchange = "demo-exchange"
            routingKey = "demo-routing-key"
            message { "Hello World!" }
        }
    }
}
```

### Consumer Example
```kotlin
rabbitmq {
    basicConsume {
        autoAck = true
        queue = "demo-queue"
        deliverCallback<String> { tag, message ->
            logger.info("Received message: $message")
        }
    }
}
```

### Library Calls Example
```kotlin
rabbitmq {
    libConnection("lib_connection") {
        with(createChannel()) {
            basicPublish("demo-queue", "demo-routing-key", null, "Hello!".toByteArray())

            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(
                    consumerTag: String?,
                    envelope: Envelope?,
                    properties: AMQP.BasicProperties?,
                    body: ByteArray?
                ) {

                }
            }

            basicConsume("demo-queue", true, consumer)
        }
    }
}
```


### Dead Letter Queue Example

```kotlin 
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
```

### Logging

- In order to set a logging level to this library add this line in `logback.xml` file:
```xml
<logger name="io.github.damir.denis.tudor.ktor.server.rabbitmq" level="<level>"/>
```

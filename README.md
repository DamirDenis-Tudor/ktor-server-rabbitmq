<div align="center">

# ![KabbitMQ](https://github.com/user-attachments/assets/bc22917b-d6bd-4f34-8775-707e575677a0)[abbitMQ](https://central.sonatype.com/artifact/io.github.damirdenis-tudor/ktor-server-rabbitmq) ![ktor](https://avatars.githubusercontent.com/u/28214161?s=48&v=4) [Official Ktor plugin](https://start.ktor.io/settings)

</div>

![Deployment Status](https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq/actions/workflows/deployment.yml/badge.svg) ![Pull Request Checks](https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq/actions/workflows/pull-request-checks.yml/badge.svg)

### Overview
- This plugin provides access to major core functionalities of the `com.rabbitmq:amqp-client` library.

### Features

- Integrated with coroutines and has a separate dispatcher.
- Includes a built-in connection/channel management system.
- Gives the possibility to interact directly with the java library.
- Seamlessly integrates with the Kotlin DSL, making it readable, maintainable, and easy to use.

---

### Table of Contents

1. [Installation](#installation)
2. [Queue Binding Example](#queue-binding-example)
3. [Producer Example](#producer-example)
4. [Consumer Example](#consumer-example)
5. [Advanced Consumer Example](#consumer-example-with-coroutinepollsize)
6. [Library Calls Example](#library-calls-example)
7. [Custom Coroutine Scope Example](#custom-coroutine-scope-example)
8. [Serialization Fallback Example](#serialization-fallback-example)
9. [Dead Letter Queue Example](#dead-letter-queue-example)
10. [Logging](#logging)

## Usage

### Installation
```kotlin
install(RabbitMQ) {
    uri = "amqp://<user>:<password>@<address>:<port>"
    defaultConnectionName = "<default_connection>"
    connectionAttempts = 20
    attemptDelay = 10
    dispatcherThreadPollSize = 4
    tlsEnabled = false
}
```

### Queue binding example
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

### Producer example
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
        deliverCallback<String> { message ->
            logger.info("Received message: $message")
        }
    }
}
```

### Consumer Example with coroutinePollSize
```kotlin
rabbitmq {
    connection(id = "consume") {
        basicConsume {
            autoAck = true
            queue = "demo-queue"
            dispacher = Dispacher.IO
            coroutinePollSize = 1_000
            deliverCallback<String> { message ->
                logger.info("Received message: $message")
                delay(30)
            }
        }
    }
}
```

### Consumer Example with coroutinePollSize
```kotlin
rabbitmq {
    connection(id = "consume") {
        basicConsume {
            autoAck = true
            queue = "demo-queue"
            dispacher = Dispacher.IO
            coroutinePollSize = 1_000
            deliverCallback<String> { message ->
                logger.info("Received message: $message")
                delay(30)
            }
        }
    }
}
```

### Library Calls Example
```kotlin
rabbitmq {
    libChannel(id = 2) {
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
```

```kotlin
rabbitmq {
    libConnection(id = "lib-connection") {
        val channel = createChannel()

        channel.basicPublish("demo-queue", "demo-routing-key", null, "Hello!".toByteArray())

        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?
            ) {

            }
        }

        channel.basicConsume("demo-queue", true, consumer)
    }
}
```
### Custom Coroutine Scope Example
```kotlin
val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("ExceptionHandler got $throwable")
}

val rabbitMQScope = CoroutineScope(SupervisorJob() + exceptionHandler)

// ...

install(RabbitMQ) {
    connectionAttempts = 3
    attemptDelay = 10
    uri = rabbitMQContainer.amqpUrl
    scope = rabbitMQScope
}

// ...

rabbitmq {
    connection(id = "consume") {
        basicConsume {
            autoAck = true
            queue = "demo-queue"
            dispacher = Dispacher.IO
            coroutinePollSize = 1_000
            deliverCallback<String> { message ->
                throw Exception("business logic exception")
            }
        }
    }
}

```

### Serialization Fallback Example

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
        repeat(10) {
            basicPublish {
                exchange = "test-exchange"
                message {
                    Message(content = "Hello world!")
                }
            }
        }
        repeat(10) {
            basicPublish {
                exchange = "test-exchange"
                message { "Hello world!" }
            }
        }
    }

    rabbitmq {
        basicConsume {
            queue = "test-queue"
            autoAck = false
            deliverCallback<Message> { message ->
                println("Received as Message: ${message.body}")
            }
            deliverFailureCallback { message ->
                println("Could not serialize, received as ByteArray: ${message.body}")
            }
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

    rabbitmq {
        basicConsume {
            queue = "test-queue"
            autoAck = false
            deliverCallback<Message> { message ->
                basicReject {
                    deliveryTag = message.envelope.deliveryTag
                    requeue = false
                }
            }
        }

        basicConsume {
            queue = "dlq"
            autoAck = true
            deliverCallback<Message> { message ->
                println("Received message in dead letter queue: ${message.body}")
            }
        }
    }
}
```

### Logging

- In order to set a logging level to this library add this line in `logback.xml` file:

```xml
<logger name="io.github.damir.denis.tudor.ktor.server.rabbitmq" level="DEBUG"/>
```

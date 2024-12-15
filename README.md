# ![KabbitMQ](https://github.com/user-attachments/assets/1fcc4641-1c1f-44c7-a929-6de86210303c)abbitMQ

## Overview

- `kabbitmq` is a Ktor plugin for RabbitMQ that provides access to all the core functionalities of the `com.rabbitmq:amqp-client` library. It integrates seamlessly with Ktor's DSL, offering readable, maintainable, and easy-to-use functionalities.


### Gradle (Kotlin DSL) - dependencies

```kotlin
dependencies {
    implementation("io.github.damirdenis-tudor:kabbitmq:<version>")
}
```

### Installation

```kotlin
install(KabbitMQ) {
    uri = "amqp://guest:guest@localhost:5678"
    connectionName = "guest"
}
```

### Features
- DSL wrapper for most functionalities with built-in parameter validation.
- Robust channel and connection management mechanisms.
- Integrated message serialization and deserialization.
- Option to interact directly with `com.rabbitmq:amqp-client`.

### Samples

#### Channel management sample
```kotlin
/* default connection with default channel */
consumerCount { queue = "test-queue" }

/* default connection with new channel */
channel(
    id = "intensive",
    autoClose = false /* can be reused by id */ 
){
    consumerCount { queue = "test-queue" }
}
```

#### Connection management sample
```kotlin
/* new connection with new channels */
connection(
    id = "intensive",
    autoClose = false /* can be reused by id */
) { /* channels will be terminated after task completion */
    channel {
        messageCount { queue = "test-queue" }
    }
    channel {
        basicConsume {
            queue = "test-queue"
            autoAck = true
            deliverCallback<Message> { tag, message ->
                println("Message: $message with $tag")
            }
        }
    }
}
```

#### Strongly type code style sample


```kotlin
/*
 * `com.rabbitmq:amqp-client` offers overloaded functions, therefore to ensure 
 * compatibility with the DSL style, a robust parameter validation mechanism is necessary. 
 * At the heart of this mechanism is a custom state delegator.
 */


basicConsume {
    queue = "test-queue"
    /* autoAck = true */ // let's say that autoAck is omited
    deliverCallback<Message> { tag, message ->
        println("Message: $message with $tag")
    }
}
```

```log
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <arguments>, initialized: <true>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <autoAck>, initialized: <false>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <cancelCallback>, initialized: <true>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <channel>, initialized: <false>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <consumerTag>, initialized: <false>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <deliverCallback>, initialized: <true>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <exclusive>, initialized: <true>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <noLocal>, initialized: <true>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <queue>, initialized: <true>
2024-12-14 18:29:26.015 [DefaultDispatcher-worker-1] WARN  KabbitMQBasicConsumeBuilder - <shutdownSignalCallback>, initialized: <false>

java.lang.IllegalStateException: Unsupported combination of parameters for basicConsume.
```

#### Direct interaction with libray sample


```kotlin
channel("direct-calls"){
    basicPublish("test", "test-routing-key", null, "fdsf".toByteArray())
    
    val consumer = object : DefaultConsumer(channel) {
        override fun handleDelivery(
            consumerTag: String?,
            envelope: Envelope?,
            properties: AMQP.BasicProperties?,
            body: ByteArray?
        ) {
            println("Received message: ${body?.let { String(it) }}")
        }
    }
    basicConsume(queueName, true, consumer)
}
```


## Dead Letter Queue Example

```kotlin 
@Serializable
data class Message(
    var content: String
)

fun Application.queueBinding() {
    install(KabbitMQ) {
        uri = "amqp://guest:guest@localhost:5678"
        connectionName = "guest"
    }

    // declare dead letter queue
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
            type = BuiltinExchangeType.DIRECT
        }
    }

    // declare queue configured with dead letter queue
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
            type = BuiltinExchangeType.FANOUT
        }
    }

    repeat(10) {
        basicPublish {
            exchange = "test-exchange"
            message {
                Message(content = "Hello world!")
            }
        }
    }

    basicConsume {
        queue = "test-queue"
        autoAck = false
        deliverCallback<Message> { tag, message ->
            /* process message */
            // ...
            
            /* simulate something went wrong */
            basicReject {
                deliveryTag = tag
                requeue = false
            }
        }
    }

    basicConsume {
        queue = "dlq"
        autoAck = true
        deliverCallback<Message> { _, message ->
            /* process message */
            println("Message in DLQ: $message")
        }
    }
}
```
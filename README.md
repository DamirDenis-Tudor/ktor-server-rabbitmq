# ![KabbitMQ](https://github.com/user-attachments/assets/bc22917b-d6bd-4f34-8775-707e575677a0)abbitMQ 
![Deployment Status](https://img.shields.io/badge/deployment-success-green?style=flat)

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

#### Connection and channel management sample
```kotlin
/* default connection, default channel */
messageCount { queue = "test-queue" }

channel(id = 2, autoClose = false ){
    /* calls */
}

/* new connection */
connection(id = "connection_0", autoClose = false){
    /* new channel */
    channel(id = 2, autoClose = false ){
        /* calls */
    }
    
    /* will be destroyed after the task is finished */
    channel{
        /* calls */
    }
}

/* reused connection */
connection(id = "connection_0"){
    /* reused channel */
    channel(id = 2, autoClose = false ){
        /* calls */
    }
    /* new channel */
    channel(id = 3, autoClose = false ){
        /* calls */
    }
}

/* default connection, new channel */
channel(id = 4, autoClose = false ){
    /* calls */
}
```

```log
DEBUG io.kabbitmq.KabbitMQService - Debug mode is enabled.
DEBUG io.kabbitmq.KabbitMQService - Created new connection with id: <default_connection>.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <1> for connection with id <default_connection>.
DEBUG i.k.KabbitMQMessageCountBuilder - Build method for method called.
DEBUG io.kabbitmq.KabbitMQService - Connection with id: <default_connection> taken from cache.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <2> for connection with id <default_connection>.
DEBUG io.kabbitmq.KabbitMQService - Created new connection with id: <connection_0>.
DEBUG io.kabbitmq.KabbitMQService - Connection with id: <connection_0> taken from cache.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <2> for connection with id <connection_0>.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <84316673> for connection with id <connection_0>.
DEBUG io.kabbitmq.KabbitMQService - Channel with id: <84316673>, closed
DEBUG io.kabbitmq.KabbitMQService - Connection with id: <connection_0> taken from cache.
DEBUG io.kabbitmq.KabbitMQService - Channel with id: <connection_0-channel-2> taken from cache.
DEBUG io.kabbitmq.KabbitMQService - Connection with id: <connection_0> taken from cache.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <3> for connection with id <connection_0>.
DEBUG io.kabbitmq.KabbitMQService - Connection with id: <connection_0>, closed
DEBUG io.kabbitmq.KabbitMQService - Connection with id: <default_connection> taken from cache.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <4> for connection with id <default_connection>.```
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
DEBUG io.kabbitmq.KabbitMQService - Debug mode is enabled.
DEBUG io.kabbitmq.KabbitMQService - Created new connection with id: <default_connection>.
DEBUG io.kabbitmq.KabbitMQService - Created new channel with id <1> for connection with id <default_connection>.
DEBUG i.k.KabbitMQBasicConsumeBuilder - Build method for method called.
DEBUG i.k.KabbitMQBasicConsumeBuilder - <arguments>, initialized: <true>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <autoAck>, initialized: <false>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <cancelCallback>, initialized: <true>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <channel>, initialized: <false>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <consumerTag>, initialized: <false>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <deliverCallback>, initialized: <true>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <exclusive>, initialized: <true>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <noLocal>, initialized: <true>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <queue>, initialized: <true>
DEBUG i.k.KabbitMQBasicConsumeBuilder - <shutdownSignalCallback>, initialized: <false>

java.lang.IllegalStateException: Unsupported combination of parameters for KabbitMQBasicConsumeBuilder.

	at io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicConsumeBuilder.build$lambda$3(KabbitMQBasicConsumeBuilder.kt:159)
	at io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator$Companion.withThisRef(Delegator.kt:86)
	at io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicConsumeBuilder.build(KabbitMQBasicConsumeBuilder.kt:65)
```

#### Serialization and deserialization sample

#### Direct interaction with libray sample
```kotlin
@Serializable
data class Message(
    var content: String
)

basicPublish {
    exchange = "test-exchange"
    message {
        Message(content = "Hello world!")
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
```

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

package integration

import connectionTest
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import rabbitmqTest
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class OperationsTests {

    @Serializable
    data class Message(
        var content: String
    )

    companion object {
        private val rabbitMQContainer: RabbitMQContainer = RabbitMQContainer(
            DockerImageName.parse("rabbitmq:management")
        )

        @BeforeAll
        @JvmStatic
        fun setUp() {
            rabbitMQContainer.start()
            println("RabbitMQ is running at ${rabbitMQContainer.amqpUrl}")
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            rabbitMQContainer.stop()
            println("RabbitMQ is stopped")
        }
    }

    @Test
    fun `queue create test`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmqTest {
                connectionTest(id = "test") {
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
                }
            }
        }
    }

    @Test
    fun `dead letter queue test`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmqTest {
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

                repeat(10) {
                    basicPublish {
                        exchange = "test-exchange"
                        message {
                            Message(content = "Hello world!")
                        }
                    }
                }

                sleep(2_000)

                assertEquals(messageCount { queue = "test-queue" }, 10)

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

                sleep(2_000)

                assertEquals(messageCount { queue = "dlq" }, 10)

                basicConsume {
                    queue = "dlq"
                    autoAck = true
                    deliverCallback<Message> { message ->
                        println("Received message: ${message.body}")
                    }
                }

                sleep(2_000)

                assertEquals(messageCount { queue = "dlq" }, 0)
            }
        }
    }

    @Test
    fun `consumer with coroutine poll (processing is not sequentially)`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo-queue"
                    exchange = "demo-exchange"
                    routingKey = "demo-routing-key"
                    queueDeclare {
                        queue = "demo-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                repeat(1_000) {
                    basicPublish {
                        exchange = "demo-exchange"
                        routingKey = "demo-routing-key"
                        message { "Hello World!" }
                    }
                }
            }

            rabbitmqTest {
                val counter = AtomicInteger(0)
                connectionTest(id = "consume") {
                    basicConsume {
                        autoAck = true
                        queue = "demo-queue"
                        dispatcher = Dispatchers.IO
                        coroutinePollSize = 100
                        deliverCallback<String> { message ->
                            delay(30)
                            withContext(Dispatchers.IO.limitedParallelism(1)) {
                                counter.incrementAndGet()
                            }
                        }
                    }
                }

                sleep(2_000)
                log.info(counter.toString())
                assert(counter.get() == 1_000)
            }
        }
    }

    @Test
    fun `consumer with default 1 coroutine (processing is sequentially)`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo1-queue"
                    exchange = "demo1-exchange"
                    routingKey = "demo1-routing-key"
                    queueDeclare {
                        queue = "demo1-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo1-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                repeat(100) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { "Hello World!" }
                    }
                }
            }

            rabbitmqTest {
                val counter = AtomicInteger(0)
                connectionTest(id = "consume1") {
                    basicConsume {
                        autoAck = true
                        queue = "demo1-queue"
                        dispatcher = Dispatchers.IO
                        deliverCallback<String> { message ->
                            delay(3)
                            counter.incrementAndGet()
                        }
                    }
                }

                sleep(2_000)
                log.info(counter.toString())
                assert(counter.get() == 100)
            }
        }
    }

    @Test
    fun `non serialization related exception `() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo2-queue"
                    exchange = "demo2-exchange"
                    routingKey = "demo2-routing-key"
                    queueDeclare {
                        queue = "demo2-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo2-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                basicPublish {
                    exchange = "demo2-exchange"
                    routingKey = "demo2-routing-key"
                    message { "Hello World!" }
                }
            }

            val exceptionDeferred = CompletableDeferred<Throwable>()

            val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                exceptionDeferred.complete(throwable)
            }

            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

            rabbitmqTest {
                connectionTest(id = "consume1") {
                    basicConsume {
                        autoAck = true
                        queue = "demo2-queue"
                        dispatcher = Dispatchers.IO
                        deliverCallback<String> { message ->
                            scope.launch {
                                println("Received message: ${message.body}")
                                throw Exception("business logic exception")
                            }
                        }
                    }
                }
            }

            val thrown = runBlocking {
                withTimeoutOrNull(5000) { exceptionDeferred.await() }
            }

            println(thrown)

            assertTrue(thrown is Exception && thrown.message?.contains("business logic exception") == true)
        }
    }
}
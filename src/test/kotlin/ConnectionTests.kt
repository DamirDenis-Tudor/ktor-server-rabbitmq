import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertNotEquals


class ConnectionTests {

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
    fun `test install with default connection`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }

            runTest {
                rabbitmq {

                }.join()
            }
        }
    }

    @Test
    fun `test connection reuse`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            runTest {
                rabbitmq {
                    val connection1 = connection(id = "connection_1") {}
                    val connection1Reused = connection(id = "connection_1") {}

                    assert(connection1.isSuccess)
                    assert(connection1Reused.isSuccess)

                    assertEquals(connection1Reused, connection1)
                }.join()
            }
        }
    }

    @Test
    fun `test connection channel reuse`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            runTest {
                rabbitmq {
                    val channel = channel(id = 99) {}
                    val channelReused = channel(id = 99) {}

                    assertEquals(channel, channelReused)
                }.join()
            }
        }
    }

    @Test
    fun `test autoclose`() = testApplication {

        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }
        application {
            runTest {
                rabbitmq {
                    val channel1 = channel(id = 99, autoClose = true) {}
                    val channel2 = channel(id = 99) {}

                    assertNotEquals(channel1, channel2)
                }.join()
            }
        }
    }
}


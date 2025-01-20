import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.io.files.FileNotFoundException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class InstallTests {
    @Test
    fun `test install with default parameters`() = testApplication {
        application {
            install(RabbitMQ)
        }
    }

    @Test
    fun `test install rabbitmq with tls enabled but missing other required parameters`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    tlsEnabled = true
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with tls enabled but pathNotFound`() = testApplication {
        application{
            assertFailsWith<FileNotFoundException> {
                install(RabbitMQ) {
                    tlsEnabled = true
                    tlsKeystorePath = "/path/to"
                    tlsTruststorePath = "/path/to"
                    tlsKeystorePassword = "/path/to"
                    tlsTruststorePassword = "/path/to"
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with attemptDelay lower than 0`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    attemptDelay = -1
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with connectionAttempts and delay lower than 0`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    connectionAttempts = -1
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with dispatcherThreadPollSize and delay lower than 0`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    dispatcherThreadPollSize = -1
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with consumerChannelCoroutineSize and delay lower than 0`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    consumerChannelCoroutineSize = -1
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with uri empty`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    uri = ""
                }
            }
        }
    }

    @Test
    fun `test install rabbitmq with default connection name empty`() = testApplication {
        application{
            assertFailsWith<IllegalArgumentException> {
                install(RabbitMQ) {
                    defaultConnectionName = ""
                }
            }
        }
    }


}

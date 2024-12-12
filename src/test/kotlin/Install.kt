import com.mesh.kabbitMq.KabbitMQ
import com.mesh.kabbitMq.dsl.*
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import io.ktor.server.application.*
import io.ktor.server.testing.*
import java.util.concurrent.CountDownLatch
import kotlin.test.Test

private fun Application.installModule() {
    install(KabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        connectionName = "localhost"
    }
}

class PluginTesting {

    @Test
    fun testInstall() = testApplication {
        application {
            installModule()

            exchangeDeclare {
                exchange = "test_exchange1"
                durable = true
                autoDelete = true
            }
        }
    }
}

import com.mesh.kabbitMq.KabbitMQ
import io.ktor.server.application.*
import io.ktor.server.testing.*
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

        }
    }
}

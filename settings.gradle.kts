dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Plugins
            version("kotlin", "2.1.21")
            plugin("multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("kover", "org.jetbrains.kotlinx.kover").version("0.8.3")
            plugin("ksp", "com.google.devtools.ksp").version("2.1.21-2.0.2")
            plugin("maven", "com.vanniktech.maven.publish").version("0.30.0")
            plugin("jreleaser", "org.jreleaser").version("1.15.0")

            // Kotlinx
            library("kotlinx-coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

            // Ktor
            version("ktor", "3.1.3")
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef("ktor")
            library("ktor-server-test-host", "io.ktor", "ktor-server-test-host").versionRef("ktor")

            // AMQP clients
            library("amqp-java", "com.rabbitmq:amqp-client:5.24.0")
            library("amqp-kourier", "dev.kourier:amqp-client-robust:0.2.4")

            // Tests
            library("tests-mockk", "io.mockk:mockk:1.13.12")
            library("tests-containers", "org.testcontainers:testcontainers:1.20.4")
            library("tests-containers-rabbitmq", "org.testcontainers:rabbitmq:1.20.4")
            library("tests-junit-jupiter-api", "org.junit.jupiter:junit-jupiter-api:5.11.4")
            library("tests-junit-jupiter-engine", "org.junit.jupiter:junit-jupiter-engine:5.11.4")

            // Others
            library("logback-classic", "ch.qos.logback:logback-classic:1.5.18")
        }
    }
}

rootProject.name = "ktor-server-rabbitmq"
include(":ktor-server-rabbitmq")
include(":ktor-server-rabbitmq-api")
include(":ktor-server-rabbitmq-java")
include(":ktor-server-rabbitmq-kourier")

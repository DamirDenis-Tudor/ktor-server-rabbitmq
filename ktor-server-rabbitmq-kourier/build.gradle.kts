plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
    pom {
        name.set("Ktor RabbitMQ plugin Kourier Implementation")
        description.set(
            "A Ktor plugin for RabbitMQ that provides access to all the core functionalities of the dev.kourier:amqp-client.\n" +
                    "It integrates seamlessly with Ktor's DSL, offering readable, maintainable, and easy-to-use functionalities.\n"
        )
        url.set(project.ext.get("url")?.toString())
        licenses {
            license {
                name.set(project.ext.get("license.name")?.toString())
                url.set(project.ext.get("license.url")?.toString())
            }
        }
        developers {
            developer {
                id.set(project.ext.get("developer.1.id")?.toString())
                name.set(project.ext.get("developer.1.name")?.toString())
                email.set(project.ext.get("developer.1.email")?.toString())
                url.set(project.ext.get("developer.1.url")?.toString())
            }
            developer {
                id.set(project.ext.get("developer.2.id")?.toString())
                name.set(project.ext.get("developer.2.name")?.toString())
                email.set(project.ext.get("developer.2.email")?.toString())
                url.set(project.ext.get("developer.2.url")?.toString())
            }
        }
        scm {
            url.set(project.ext.get("scm.url")?.toString())
        }
    }
}

kotlin {
    // Tiers are in accordance with <https://kotlinlang.org/docs/native-target-support.html>
    // Tier 1
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // Tier 2
    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // Tier 3
    mingwX64()
    watchosDeviceArm64()

    // jvm & js
    jvmToolchain(17)
    jvm {
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                    showCauses = true
                    showStackTraces = true
                    showExceptions = true
                    info.events = debug.events
                }
            }
        }
    }

    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":ktor-server-rabbitmq-api"))
                api(libs.amqp.kourier)
                implementation(libs.logback.classic)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.tests.mockk)
                implementation(libs.ktor.server.test.host)
                implementation(libs.tests.containers)
                implementation(libs.tests.containers.rabbitmq)
                implementation(libs.tests.junit.jupiter.api)
                implementation(libs.tests.junit.jupiter.engine)
            }
        }
    }
}

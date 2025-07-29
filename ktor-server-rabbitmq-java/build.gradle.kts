plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
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
        val jvmMain by getting {
            dependencies {
                api(project(":ktor-server-rabbitmq-api"))
                api(libs.amqp.java)
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

/*
tasks.jar {
    exclude("logback.xml")
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"])
}
*/

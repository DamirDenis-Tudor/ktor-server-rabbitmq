plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvmToolchain(17)
    jvm()

    applyDefaultHierarchyTemplate()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":ktor-server-rabbitmq-java"))
            }
        }
        /*
        val nonJvmMain by creating {
            dependencies {
                api(project(":ktor-server-rabbitmq-kourier"))
            }
        }
        */
    }
}

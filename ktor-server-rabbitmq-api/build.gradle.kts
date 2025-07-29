plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvmToolchain(17)
    jvm()

    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.server.core)
                api(libs.ktor.server.netty)
                api(libs.kotlinx.coroutines)
                api(libs.kotlinx.serialization.json)
            }
        }
    }
}

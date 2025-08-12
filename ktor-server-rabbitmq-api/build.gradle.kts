plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
    pom {
        name.set("Ktor RabbitMQ plugin API")
        description.set(
            "Common APIs for the Ktor RabbitMQ plugin, providing shared functionality across different implementations (Java and Kourier)."
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
    jvm()

    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.server.core)
                //api(libs.ktor.server.netty) // TODO: What is this used for?
                api(libs.kotlinx.coroutines)
                api(libs.kotlinx.serialization.json)
            }
        }
    }
}

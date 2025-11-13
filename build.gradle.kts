plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.maven) apply false
    alias(libs.plugins.jreleaser)
}

allprojects {
    group = "io.github.damirdenis-tudor"
    version = project.findProperty("releaseVersion") ?: "0.0.1"
    project.ext.set("url", "https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq")
    project.ext.set("license.name", "Apache 2.0")
    project.ext.set("license.url", "https://www.apache.org/licenses/LICENSE-2.0.txt")
    project.ext.set("developer.1.id", "DamirDenis-Tudor")
    project.ext.set("developer.1.name", "Damir Denis-Tudor")
    project.ext.set("developer.1.email", "denis-tudor.damir@student.tuiasi.ro")
    project.ext.set("developer.1.url", "https://github.com/DamirDenis-Tudor")
    project.ext.set("developer.2.id", "nathanfallet")
    project.ext.set("developer.2.name", "Nathan Fallet")
    project.ext.set("developer.2.email", "contact@nathanfallet.me")
    project.ext.set("developer.2.url", "https://www.nathanfallet.me")
    project.ext.set("scm.url", "https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq.git")

    repositories {
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val githubToken = project.findProperty("githubToken")?.toString() ?: "no_blank"
jreleaser {
    release {
        github {
            token = githubToken

            changelog {

                enabled = true
                setFormatted("ALWAYS")
                preset = "conventional-commits"
                extraProperties = mapOf("categorizeScopes" to true)
                contributors {
                    enabled = false
                }
                append {
                    setTarget("build/jreleaser/release/CHANGELOG.md")
                    enabled = true
                    content = """## Maven artifacts
                         - [ktor-server-rabbitmq:$version](https://central.sonatype.com/artifact/io.github.damirdenis-tudor/ktor-server-rabbitmq/$version)
                         """
                }
            }
        }
        project {
            name = "ktor-server-rabbitmq"
            description.set("Ktor Server RabbitMQ plugin")
            copyright.set("Damir Denis-Tudor")
        }
    }
}

/**
 * Import github repo; first add [jitpack] to repos
 * @param repo username/repo; e.g. y9san9/kotlogram-wrapper
 */
fun DependencyHandlerScope.github(repo: String, tag: String = "-SNAPSHOT") = implementation(
    repo.split("/").let { (username, repo) ->
        "com.github.${username}:${repo}:${tag}"
    }
)
/**
 * Jitpack maven
 */
fun RepositoryHandler.jitpack() = maven("https://jitpack.io")

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.y9san9.mafiaboost"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jitpack()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    github("y9san9/kotlogram-wrapper", "beta-1-5")
    github("y9san9/kotlogram")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "com.y9san9.mafiaboost.MainKt"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Main-Class" to application.mainClassName))
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

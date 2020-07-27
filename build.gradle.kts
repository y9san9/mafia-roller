/**
 * @param repo username/repo; e.g. y9san9/kotlogram-wrapper
 */
fun DependencyHandlerScope.github(repo: String, tag: String = "-SNAPSHOT") = implementation(
    repo.split("/").let { (username, repo) ->
        "com.github.${username}:${repo}:${tag}"
    }
)

plugins {
    kotlin("jvm") version "1.3.72"
}

group = "com.y9san9.mafiaboost"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    github("y9san9/kotlogram-wrapper")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

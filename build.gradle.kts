plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
}

group = "ru.ought.recipe_balancer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

tasks {
    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}

val spekVersion by extra("2.0.8")
val atriumVersion by extra("0.9.0-alpha")
val mockkVersion by extra("1.9.3")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation("com.charleskorn.kaml:kaml:0.15.0")

    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:$atriumVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
plugins {
  kotlin("jvm") version "1.5.31"
  application
}

application {
  mainClass.set("MainKt")
}

group = "org.traderepublic.candlesticks"
version = "1.1.1"

repositories {
  mavenCentral()
}

object DependencyVersions {
  const val coroutines = "1.5.2"
  const val http4k = "4.13.1.0"
  const val jackson = "2.13.+"
  const val mockk = "1.12.0"
  const val exposedVersion = "0.37.3"
}

dependencies {
  implementation(kotlin("stdlib"))
  testImplementation(kotlin("test"))


  implementation(platform("org.http4k:http4k-bom:4.13.1.0"))
  implementation("org.http4k:http4k-core")
  implementation("org.http4k:http4k-server-netty")
  implementation("org.http4k:http4k-client-websocket:${DependencyVersions.http4k}")
  implementation("org.http4k:http4k-format-jackson:${DependencyVersions.http4k}")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.coroutines}")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersions.jackson}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${DependencyVersions.jackson}")
  testImplementation("io.mockk:mockk:${DependencyVersions.mockk}")

  implementation("org.jetbrains.exposed:exposed-core:${DependencyVersions.exposedVersion}")
  implementation("org.jetbrains.exposed:exposed-dao:${DependencyVersions.exposedVersion}")
  implementation("org.jetbrains.exposed:exposed-jdbc:${DependencyVersions.exposedVersion}")
  implementation("org.jetbrains.exposed:exposed-java-time:${DependencyVersions.exposedVersion}")
  implementation("com.h2database:h2:1.4.199")
}

tasks.test {
  useJUnitPlatform()
}

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val projectGroup: String by project
val projectVersion: String by project
group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

val springVersion: String by project
val reactorBomVersion: String by project
val nettyVersion: String by project
val kotlinCoroutinesVersion: String by project
val jsr305Version: String by project
val jUnit5Version: String by project
val assertJVersion: String by project
dependencies {
    implementation(platform("org.springframework:spring-framework-bom:$springVersion"))
    implementation(platform("org.junit:junit-bom:$jUnit5Version"))
    implementation(platform("io.projectreactor:reactor-bom:$reactorBomVersion"))
    implementation(platform("io.netty:netty-bom:$nettyVersion"))

    implementation("io.projectreactor:reactor-core")
    implementation("io.projectreactor.netty:reactor-netty")
    implementation("org.springframework:spring-core")
    implementation("io.netty:netty-buffer")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    compileOnly("com.google.code.findbugs:jsr305:$jsr305Version")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.time.ExperimentalTime")
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

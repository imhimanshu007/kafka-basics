plugins {
    id("java")
}


allprojects{
    group = "org.relaxcoder"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

subprojects{
    apply(plugin = "java")
    dependencies {
        implementation("org.apache.kafka.kafka-clients:3.5.0")
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
    tasks.test {
        useJUnitPlatform()
    }
}
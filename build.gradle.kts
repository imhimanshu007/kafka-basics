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
        implementation("org.apache.kafka:kafka-clients:3.5.0")
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")

        // https://mvnrepository.com/artifact/org.projectlombok/lombok
        compileOnly("org.projectlombok:lombok:1.18.28")
        annotationProcessor("org.projectlombok:lombok:1.18.28")
        testCompileOnly("org.projectlombok:lombok:1.18.28")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
    }
    tasks.test {
        useJUnitPlatform()
    }
}
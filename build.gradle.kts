import nebula.plugin.contacts.Contact
import nebula.plugin.contacts.ContactsExtension

plugins {
    `java-library`

    id("nebula.release") version "16.0.0"

    id("nebula.maven-manifest") version "18.4.0"
    id("nebula.maven-nebula-publish") version "18.4.0"
    id("nebula.maven-resolved-dependencies") version "18.4.0"

    id("nebula.contacts") version "6.0.0"
    id("nebula.info") version "11.3.3"

    id("nebula.javadoc-jar") version "18.4.0"
    id("nebula.source-jar") version "18.4.0"
}

apply(plugin = "nebula.publish-verification")

configure<nebula.plugin.release.git.base.ReleasePluginExtension> {
    defaultVersionStrategy = nebula.plugin.release.NetflixOssStrategies.SNAPSHOT(project)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Set as appropriate for your organization
group = "io.micronaut.rewrite"
description = "Rewrite recipes for upgrading to Micronaut 4."
version = "0.1.0-SNAPSHOT"

repositories {
    mavenLocal()
    // Needed to pick up snapshot versions of rewrite
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
}

//The bom version can also be set to a specific version or latest.release.
val rewriteBomVersion = "latest.integration"

configurations.all {
    resolutionStrategy {
        force("org.openrewrite.gradle.tooling:model:0.8.1")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:latest.release")
    compileOnly("com.google.code.findbugs:jsr305:latest.release")
    annotationProcessor("org.projectlombok:lombok:latest.release")
    implementation(platform("org.openrewrite.recipe:rewrite-recipe-bom:${rewriteBomVersion}"))
    implementation("org.openrewrite.gradle.tooling:model:0.8.1")

    implementation("org.openrewrite:rewrite-java")
    runtimeOnly("org.openrewrite:rewrite-java-17")
    implementation("org.openrewrite.recipe:rewrite-migrate-java")
    implementation("org.openrewrite:rewrite-maven")
    implementation("org.openrewrite:rewrite-gradle")
    implementation("org.openrewrite:rewrite-yaml")
    implementation("org.openrewrite:rewrite-properties")
    // Need to have a slf4j binding to see any output enabled from the parser.
    runtimeOnly("ch.qos.logback:logback-classic:1.2.+")

    testImplementation("org.junit.jupiter:junit-jupiter-api:latest.release")
    testImplementation("org.junit.jupiter:junit-jupiter-params:latest.release")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:latest.release")
    testRuntimeOnly("com.google.guava:guava:latest.release")
    testRuntimeOnly("org.codehaus.groovy:groovy:latest.release")
    testRuntimeOnly("io.micronaut:micronaut-context:4.0.0-M2")
    testRuntimeOnly("io.micronaut:micronaut-websocket:4.0.0-M2")
    testRuntimeOnly("io.micronaut.validation:micronaut-validation:4.0.0-M5")
    testRuntimeOnly("io.micronaut:micronaut-retry:4.0.0-M4")
    testRuntimeOnly("io.micronaut.email:micronaut-email:2.0.0-M1")
    testRuntimeOnly("javax.annotation:javax.annotation-api:1.3.2")
    testRuntimeOnly("javax.validation:validation-api:2.0.1.Final")
    testRuntimeOnly("javax.persistence:javax.persistence-api:2.2")
    testRuntimeOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    testRuntimeOnly("javax.mail:javax.mail-api:1.6.2")
    testRuntimeOnly("jakarta.mail:jakarta.mail-api:2.1.1")

    testImplementation("org.openrewrite:rewrite-test")
    testImplementation("org.assertj:assertj-core:latest.release")
    testRuntimeOnly(gradleApi())
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    jvmArgs = listOf("-XX:+UnlockDiagnosticVMOptions", "-XX:+ShowHiddenFrames")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
tasks.named<JavaCompile>("compileJava") {
    options.release.set(17)
}

configure<ContactsExtension> {
    val j = Contact("grellej@unityfoundation.org")
    j.moniker("Jeremy Grelle")
    people["grellej@unityfoundation.org"] = j
}

configure<PublishingExtension> {
    publications {
        named("nebula", MavenPublication::class.java) {
            suppressPomMetadataWarningsFor("runtimeElements")
        }
    }
}

publishing {
  repositories {
      maven {
          name = "moderne"
          url = uri("https://us-west1-maven.pkg.dev/moderne-dev/moderne-recipe")
      }
  }
}

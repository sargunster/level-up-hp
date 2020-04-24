import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.palantir.gradle.gitversion.VersionDetails
import net.minecraftforge.gradle.common.util.MinecraftExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.OffsetDateTime

val minecraftVersion: String by project
val curseProjectId: String by project
val curseMinecraftVersion: String by project
val modJarBaseName: String by project
val modMavenGroup: String by project

buildscript {
    repositories {
        maven(url = "https://files.minecraftforge.net/maven")
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(group="net.minecraftforge.gradle", name="ForgeGradle", version="3.+")
                .setChanging(true)
                .exclude(group="trove", module = "trove")
    }
}

plugins {
    java
    kotlin("jvm") version "1.3.61"
    idea
    id("com.palantir.git-version") version "0.12.3"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

apply(plugin="net.minecraftforge.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

base {
    archivesBaseName = modJarBaseName
}

repositories {
    maven(url = "https://files.minecraftforge.net/maven")
    mavenCentral()
    jcenter()
    maven(url= "https://minecraft.curseforge.com/api/maven/")
}

val gitVersion: groovy.lang.Closure<Any> by extra
val versionDetails: groovy.lang.Closure<VersionDetails> by extra

version = "5.0.0+mc$minecraftVersion+forge"
group = modMavenGroup

configure<MinecraftExtension> {
    mappings("snapshot", "20200424-1.15.1")
//    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        create("client")
        create("server")
        create("data") {
            args("--mod", "leveluphp", "--all", "--output", file("src/generated/resources"))
        }

        configureEach {
            workingDirectory(project.file("run/$name"))
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")

            mods {
                create("leveluphp") {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:1.15.2-31.1.47")
    implementation("kottle:Kottle:1.5.0")
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "leveluphp",
                "Specification-Vendor" to "sargunv",
                "Specification-Version" to project.version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "sargunv",
                "Implementation-Timestamp" to OffsetDateTime.now().toString()
        ))
    }
}

//if (versionDetails().isCleanTag) {
//
//    curseforge {
//        if (project.hasProperty("curseforge_api_key")) {
//            apiKey = project.property("curseforge_api_key")!!
//        }
//
//        project(closureOf<CurseProject> {
//            id = curseProjectId
//            changelog = file("changelog.txt")
//            releaseType = "release"
//            addGameVersion(curseMinecraftVersion)
//            addGameVersion("Fabric")
//            relations(closureOf<CurseRelation>{
//                requiredDependency("fabric-api")
//                requiredDependency("fabric-language-kotlin")
//                embeddedLibrary("cloth-config")
//                embeddedLibrary("auto-config-updated-api")
//                optionalDependency("health-overlay")
//            })
//            mainArtifact(file("${project.buildDir}/libs/${base.archivesBaseName}-$version.jar"))
//            afterEvaluate {
//                mainArtifact(remapJar)
//                uploadTask.dependsOn(remapJar)
//            }
//        })
//
//        options(closureOf<Options> {
//            forgeGradleIntegration = false
//        })
//    }
//
//}

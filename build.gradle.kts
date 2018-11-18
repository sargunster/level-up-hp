import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import net.minecraftforge.gradle.user.patcherUser.forge.ForgeExtension

buildscript {
    val vForgeGradle: String by project
    val vKotlin: String by project

    repositories {
        jcenter()
        maven(url = "http://files.minecraftforge.net/maven")
    }
    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = vForgeGradle)
        classpath(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = vKotlin)
    }
}

val modNamespace: String by project
val modId: String by project

val vMinecraft: String by project
val vMcpMappings: String by project
val vForge: String by project
val vKotlin: String by project
val vForgelin: String by project
val vShadowMc: String by project
val vDevWorld: String by project

val runClientPlayerName: String by project

fun getVersionFromGit(): String {
    val proc = Runtime.getRuntime().exec("git describe --tags --dirty")
    val exitCode = proc.waitFor()
    if (exitCode == 0) {
        return proc.inputStream.bufferedReader().readLine().trim()
    } else {
        throw Exception("Git describe failed with code: $exitCode")
    }
}

val vThisMod = getVersionFromGit()

apply(plugin = "net.minecraftforge.gradle.forge")
apply(plugin = "kotlin")

plugins {
    id("com.diffplug.gradle.spotless") version "3.16.0"
}

version = "$vMinecraft-$vThisMod"
group = modNamespace

project.setProperty("archivesBaseName", modId)

repositories {
    jcenter()
    maven(url = "http://maven.shadowfacts.net/")
    maven(url = "http://dl.tsr.me/artifactory/libs-release/")
}

configurations {
    create("mod")
}

dependencies {
    val deobfCompile = configurations["deobfCompile"]
    deobfCompile(group = "net.shadowfacts", name = "ShadowMC", version = vShadowMc)
    deobfCompile(group = "net.shadowfacts", name = "Forgelin", version = vForgelin)

    val mod = configurations["mod"]
    mod(group = "com.fireball1725.devworld", name = "devworld", version = vDevWorld)
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

configure<ForgeExtension> {
    version = "$vMinecraft-$vForge"
    runDir = "run"
    mappings = vMcpMappings
    replace(mapOf("@MOD_VERSION@" to vThisMod, "@MOD_ID" to modId))
}

tasks.named<JavaExec>("runClient") {
    args("--username", runClientPlayerName)
    outputs.upToDateWhen { false }
}

tasks.named<ProcessResources>("processResources") {
    val sourceSets = project.the<JavaPluginConvention>().sourceSets
    val minecraft = project.extensions.getByType<ForgeExtension>()

    inputs.property("version", project.version)
    inputs.property("mcversion", minecraft.version)

    from(sourceSets.getByName("main").resources.srcDirs) {
        include("mcmod.info")
        expand(
            mapOf(
                "modid" to modId,
                "version" to project.version,
                "mcversion" to minecraft.version
            )
        )
    }
    from(sourceSets.getByName("main").resources.srcDirs) {
        exclude("mcmod.info")
    }
}

configure<SpotlessExtension> {
    val ktlintSettings = mapOf(
        "indent_size" to "4",
        "continuation_indent_size" to "4",
        "max_line_length" to "100"
    )
    format("misc") {
        target(
            "**/*.md",
            "**/*.gitignore",
            "**/*.json",
            "**/*.info",
            "**/*.lang",
            "**/*.properties"
        )
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    kotlin {
        ktlint().userData(ktlintSettings)
    }

    kotlinGradle {
        ktlint().userData(ktlintSettings)
    }
}

tasks.create<Copy>("installMods") {
    val minecraft = project.extensions.getByType<ForgeExtension>()
    from(configurations["mod"])
    include("**/*.jar")
    into(file(minecraft.runDir + "/mods"))
}.dependsOn("deinstallMods")

tasks.create<Delete>("deinstallMods") {
    val minecraft = project.extensions.getByType<ForgeExtension>()
    delete(fileTree(
        "dir" to minecraft.runDir + "/mods",
        "include" to "*.jar"
    ))
}

tasks["setupDecompWorkspace"].dependsOn("installMods")
tasks["setupDevWorkspace"].dependsOn("installMods")

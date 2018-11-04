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

val vThisMod: String by project
val vMinecraft: String by project
val vMcpMappings: String by project
val vForge: String by project
val vKotlin: String by project
val vForgelin: String by project
val vShadowMc: String by project

val runClientPlayerName: String by project

apply(plugin = "net.minecraftforge.gradle.forge")
apply(plugin = "kotlin")

version = "$vMinecraft-$vThisMod"
group = modNamespace

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
}

tasks.named<ProcessResources>("processResources") {
    val sourceSets = project.the<JavaPluginConvention>().sourceSets
    val minecraft = project.extensions.getByType<ForgeExtension>()

    inputs.property("version", project.version)
    inputs.property("mcversion", minecraft.version)

    from(sourceSets.getByName("main").resources.srcDirs) {
        include("mcmod.info")
        expand(mapOf("modid" to modId, "version" to project.version, "mcversion" to minecraft.version))
    }
    from(sourceSets.getByName("main").resources.srcDirs) {
        exclude("mcmod.info")
    }
}

repositories {
    jcenter()
    maven(url = "http://maven.shadowfacts.net/")
}

dependencies {
    val deobfCompile = configurations.getByName("deobfCompile")
    deobfCompile(group = "net.shadowfacts", name = "ShadowMC", version = vShadowMc)
    deobfCompile(group = "net.shadowfacts", name = "Forgelin", version = vForgelin)
}
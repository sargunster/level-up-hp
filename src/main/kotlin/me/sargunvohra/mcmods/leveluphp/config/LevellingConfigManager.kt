package me.sargunvohra.mcmods.leveluphp.config

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.sargunvohra.mcmods.leveluphp.LuhpIds
import me.sargunvohra.mcmods.leveluphp.core.hpLeveller
import net.minecraft.client.resources.ReloadListener
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import java.io.InputStreamReader

class LevellingConfigManager(
    private val server: MinecraftServer
) : ReloadListener<LevellingConfig>() {

    private fun prepareOverrides(resourceManager: IResourceManager): Map<String, Int> {
        val mapOfStrToIntType = object : TypeToken<Map<String, Int>>() {}.type
        return resourceManager
            .getAllResourceLocations("leveluphp/override") { path ->
                path.endsWith(".json")
            }
            .map { id ->
                val resource = resourceManager.getResource(id)
                return@map gson.fromJson<Map<String, Int>>(
                    InputStreamReader(resource.inputStream),
                    mapOfStrToIntType
                )
            }
            .fold(HashMap()) { acc, overrides ->
                acc += overrides
                return@fold acc
            }
    }

    override fun prepare(resourceManager: IResourceManager, profiler: IProfiler): LevellingConfig {
        val overrides = prepareOverrides(resourceManager)
        val baseConfig = resourceManager.getResource(LuhpIds.LEVELLING_CONFIG)
        return gson.fromJson(
            InputStreamReader(baseConfig.inputStream),
            LevellingConfig::class.java
        ).copy(overrides = overrides)
    }

    override fun apply(
        loadedConfig: LevellingConfig,
        resourceManager: IResourceManager,
        profiler: IProfiler
    ) {
        LogManager.getLogger().info("Loaded {}", loadedConfig)
        loadedConfig.validate()
        config = loadedConfig
        server.playerList.players.forEach { it.hpLeveller.updateState() }
    }

    companion object {
        var config: LevellingConfig = LevellingConfig()

        private val gson = Gson()

        init {
            config.validate()
        }
    }
}

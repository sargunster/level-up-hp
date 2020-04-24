package me.sargunvohra.mcmods.leveluphp.config

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.sargunvohra.mcmods.leveluphp.LuhpMod
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.IResourceManagerReloadListener
import net.minecraft.server.MinecraftServer
import net.minecraftforge.resource.IResourceType
import org.apache.logging.log4j.LogManager
import java.io.InputStreamReader

class LevellingConfigLoader(val server: MinecraftServer) : IResourceManagerReloadListener {

    private val gson = Gson()

    override fun onResourceManagerReload(
        resourceManager: IResourceManager
    ) {

        val overrides = resourceManager
            .getAllResourceLocations("leveluphp/override") { it.endsWith(".json") }
            .fold(HashMap<String, Int>()) { map, resource ->
                val override: Map<String, Int> = gson.fromJson(
                    InputStreamReader(resourceManager.getResource(resource).inputStream),
                    object : TypeToken<Map<String, Int>>() {}.type
                )
                map += override
                return@fold map
            }

        val general = resourceManager.getResource(LuhpMod.id("leveluphp/general.json"))
        val config = gson.fromJson(
            InputStreamReader(general.inputStream),
            LevellingConfig::class.java
        ).copy(overrides = overrides)

        LogManager.getLogger().info("Loaded {}", config)

        config.validate()
        LevellingConfigLoader.config = config
        successfullyLoadedDataPack = true

        server.playerList.players.forEach { it.hpLevelHandler.onModified() }
    }

    override fun getResourceType(): IResourceType {
        return LuhpResourceTypes.LEVELLING_CONFIG
    }

    companion object {
        var config: LevellingConfig = LevellingConfig()
        private var successfullyLoadedDataPack = false

        init {
            config.validate()
        }

        fun ensureDataLoaded() {
            if (!successfullyLoadedDataPack)
                throw RuntimeException(
                    "One of your mods broke data loading;"
                        + " forcing a crash to preserve your levels!"
                )
        }
    }
}

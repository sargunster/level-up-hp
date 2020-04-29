package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.config.ClientConfig
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig

@Mod("leveluphp")
object LuhpMod {
    init {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.CLIENT,
            ClientConfig.spec,
            "leveluphp.toml"
        )
    }
}

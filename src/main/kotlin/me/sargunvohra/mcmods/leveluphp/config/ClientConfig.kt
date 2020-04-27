package me.sargunvohra.mcmods.leveluphp.config

import net.minecraftforge.common.ForgeConfigSpec

class ClientConfig(builder: ForgeConfigSpec.Builder) {

    init {
        builder.comment(
            "Values like starting health, xp gain, and death penalties are customizable"
                + " with data packs, not with this config. See the mod's wiki for more information,"
                + " including instructions."
        )
    }

    private val enableXpBarOverrideSpec: ForgeConfigSpec.ConfigValue<Boolean> = builder
        .define("enableXpBarOverride", true)

    val enableXpBarOverride: Boolean get() = enableXpBarOverrideSpec.get()

    companion object {
        private val specPair = ForgeConfigSpec.Builder().configure(::ClientConfig)
        val spec: ForgeConfigSpec = specPair.right
        val instance: ClientConfig = specPair.left
    }
}


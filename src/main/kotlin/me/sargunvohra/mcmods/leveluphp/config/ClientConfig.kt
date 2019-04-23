package me.sargunvohra.mcmods.leveluphp.config

import me.sargunvohra.mcmods.autoconfig1.ConfigData
import me.sargunvohra.mcmods.autoconfig1.annotation.Config

@Config(name = "leveluphp")
@Config.Gui.Background("textures/block/spruce_planks.png")
data class ClientConfig (
    val enableXpBarOverride: Boolean = true
) : ConfigData {
    override fun validatePostLoad() {}
}

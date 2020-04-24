package me.sargunvohra.mcmods.leveluphp

import net.minecraft.util.ResourceLocation

object LuhpIds {
    val LEVELLING_CONFIG = id("leveluphp/general.json")
    val LEVELLER_CAPABILITY = id("hp_leveller")
    val LEVEL_SYNC_CHANNEL = id("hp_level_sync")
    val LEVEL_UP_SOUND = id("levelup")
    val LEVEL_UP_TRIGGER = id("player_levelled_up")
    val EXP_BAR_ICONS_TEXTURE = id("textures/gui/icons.png")
    val HEART_CONTAINER_ITEM = id("heart_container")

    private fun id(path: String) = ResourceLocation("leveluphp", path)
}

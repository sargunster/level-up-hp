package me.sargunvohra.leveluphp

import net.minecraft.util.ResourceLocation

object Resources {
    val texIcons = get("textures/gui/icons.png")
    val dataGeneral = get("${LevelUpHp.MOD_ID}/general.json")
    val soundLevelUp = get("levelup")

    fun get(name: String) = ResourceLocation(LevelUpHp.MOD_ID, name)
}

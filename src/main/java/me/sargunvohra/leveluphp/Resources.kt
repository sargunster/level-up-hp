package me.sargunvohra.leveluphp

import net.minecraft.util.ResourceLocation

/** Utility class to generate ResourceLocations for this mod.  */
object Resources {
    val texIcons = get("textures/gui/icons.png")
    val dataGeneral = get("${LevelUpHp.MOD_ID}/general.json")
    fun get(name: String) = ResourceLocation(LevelUpHp.MOD_ID, name)
}

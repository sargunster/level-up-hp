package me.sargunvohra.leveluphp

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod

@Mod(LevelUpHp.MOD_ID)
object LevelUpHp {
    const val MOD_ID = BuildConfig.NAME
    fun res(name: String) = ResourceLocation(MOD_ID, name)
}

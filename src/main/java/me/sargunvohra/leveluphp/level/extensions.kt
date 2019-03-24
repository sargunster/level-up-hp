package me.sargunvohra.leveluphp.level

import net.minecraft.entity.player.EntityPlayer

val EntityPlayer.leveller
    get() = getCapability(LevellerLoader.CAPABILITY)

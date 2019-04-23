package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.level.HpLeveller
import net.minecraft.entity.player.PlayerEntity

val PlayerEntity.hpLevelHandler get() = (this as HpLeveller).hpLevelHandler

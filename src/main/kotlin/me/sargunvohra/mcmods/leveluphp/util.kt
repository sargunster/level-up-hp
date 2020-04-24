package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.IHpLeveller
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.common.util.LazyOptional

val PlayerEntity.hpLevelHandlerOpt: HpLevelHandler?
    get() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return getCapability(IHpLeveller.CAPABILITY)
            .map { it.hpLevelHandler }
            .orElse(null)
    }

val PlayerEntity.hpLevelHandler: HpLevelHandler
    get() = hpLevelHandlerOpt
        ?: throw RuntimeException("player didn't have levelling capability attached!")

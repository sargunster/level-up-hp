package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.capability.HpLevelHandler
import me.sargunvohra.mcmods.leveluphp.capability.IHpLeveller
import net.minecraft.entity.player.PlayerEntity

val PlayerEntity.hpLevelHandlerOpt: HpLevelHandler?
    get() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return getCapability(IHpLeveller.HP_LEVELLER_CAPABILITY)
            .map { it.hpLevelHandler }
            .orElse(null)
    }

val PlayerEntity.hpLevelHandler: HpLevelHandler
    get() = hpLevelHandlerOpt
        ?: throw RuntimeException("player didn't have levelling capability attached!")

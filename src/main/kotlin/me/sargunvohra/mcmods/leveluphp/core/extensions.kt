package me.sargunvohra.mcmods.leveluphp.core

import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigManager
import net.minecraft.entity.player.PlayerEntity

val HpLeveller.isMaxedOut get() = level >= LevellingConfigManager.config.maximumLevel

val HpLeveller.currentXpTarget get() = LevellingConfigManager.config.xpTargetFunction(level)

val PlayerEntity.hpLevellerOrNull: HpLeveller?
    get() {
        val opt = getCapability(CapabilityRegistrationSubscriber.HP_LEVELLER_CAPABILITY)
        return if (opt.isPresent)
            opt.orElseThrow { RuntimeException("capability disappeared!") }
        else null
    }

val PlayerEntity.hpLeveller: HpLeveller
    get() {
        return hpLevellerOrNull
            ?: throw RuntimeException("player didn't have leveller capability attached!")
    }

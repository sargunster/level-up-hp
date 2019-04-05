package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.level.HpLeveller
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.player.PlayerEntity

val PlayerEntity.hpLevelHandler get() = (this as HpLeveller).hpLevelHandler

fun i18n(key: String) = I18n.translate(key)

package me.sargunvohra.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.core.PlayerLevelHandler
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.literal
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString

private const val MIN_PERMISSION = 2

private fun check(handler: PlayerLevelHandler): ITextComponent {
    return TextComponentString(
        String.format("level: %d, xp: %d/%d", handler.level, handler.xp, handler.xpTarget)
    )
}

fun buildLevelUpHpCommand(): LiteralArgumentBuilder<CommandSource> {
    val base = literal(LevelUpHp.MOD_ID)
    listOf(
        setter("setxp", MIN_PERMISSION) { obj, xp -> obj.xp = xp },
        setter("addxp", MIN_PERMISSION) { obj, xp -> obj.xp += xp },
        setter("setlevel", MIN_PERMISSION) { obj, level -> obj.level = level },
        setter("addlevel", MIN_PERMISSION) { obj, levels -> obj.level += levels },
        getter("check", MIN_PERMISSION) { check(it) }
    ).forEach { base.then(it) }
    return base
}

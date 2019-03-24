package me.sargunvohra.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.level.Leveller
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.literal
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString

private fun check(handler: Leveller): ITextComponent {
    return TextComponentString("level: ${handler.level}, xp: ${handler.xp}/${handler.xpTarget}")
}

fun buildLevelUpHpCommand(): LiteralArgumentBuilder<CommandSource> {
    val base = literal(LevelUpHp.MOD_ID)

    listOf(
        setter("setxp") { obj, xp -> obj.xp = xp },
        setter("addxp") { obj, xp -> obj.xp += xp },
        setter("setlevel") { obj, level -> obj.level = level },
        setter("addlevel") { obj, levels -> obj.level += levels },
        getter("check") { check(it) },
        getter("config") { TextComponentString(it.config.toString()) }
            .requires { it.hasPermissionLevel(2) }
    ).forEach { base.then(it) }

    return base
}

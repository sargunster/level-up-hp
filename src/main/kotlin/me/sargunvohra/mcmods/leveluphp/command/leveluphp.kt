package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.Component

private fun check(handler: HpLevelHandler): Component {
    val message = "level: ${handler.level}, xp: ${handler.xp}/${handler.currentXpTarget}"
    return TextComponent(message)
}

fun buildLevelUpHpCommand(): LiteralArgumentBuilder<ServerCommandSource> {
    val base = literal("leveluphp")

    listOf(
        setter("setxp") { obj, xp -> obj.xp = xp },
        setter("addxp") { obj, xp -> obj.xp += xp },
        setter("setlevel") { obj, level -> obj.level = level },
        setter("addlevel") { obj, levels -> obj.level += levels },
        getter("check") { check(it) },
        getter("config") { TextComponent(it.config.toString()) }
            .requires { it.hasPermissionLevel(2) }
    ).forEach { base.then(it) }

    return base
}

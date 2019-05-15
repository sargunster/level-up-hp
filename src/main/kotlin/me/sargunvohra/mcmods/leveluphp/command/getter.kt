package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.command.arguments.EntityArgumentType.getPlayer
import net.minecraft.command.arguments.EntityArgumentType.player
import net.minecraft.network.chat.Component
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

fun getter(
    literal: String,
    response: (HpLevelHandler) -> Component
): LiteralArgumentBuilder<ServerCommandSource> {
    return literal(literal)
        .executes { ctx ->
            val handler = ctx.source.player.hpLevelHandler
            ctx.source.sendFeedback(response(handler), false)
            return@executes 0
        }
        .then(
            argument("player", player())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    val player = getPlayer(ctx, "player")
                    val handler = player.hpLevelHandler
                    ctx.source.sendFeedback(response(handler), true)
                    return@executes 0
                }
        )
}

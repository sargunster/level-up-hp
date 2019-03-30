package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.command.arguments.EntityArgumentType.getServerPlayerArgument
import net.minecraft.command.arguments.EntityArgumentType.onePlayer
import net.minecraft.server.command.ServerCommandManager.argument
import net.minecraft.server.command.ServerCommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TextComponent

fun getter(
    literal: String,
    response: (HpLevelHandler) -> TextComponent
): LiteralArgumentBuilder<ServerCommandSource> {
    return literal(literal)
        .executes { ctx ->
            val handler = ctx.source.player.hpLevelHandler
            ctx.source.sendFeedback(response(handler), true)
            return@executes 0
        }
        .then(
            argument("player", onePlayer())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    val player = getServerPlayerArgument(ctx, "player")
                    val handler = player.hpLevelHandler
                    ctx.source.sendFeedback(response(handler), true)
                    return@executes 0
                }
        )
}

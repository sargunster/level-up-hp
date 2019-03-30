package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.command.arguments.EntityArgumentType.method_9312
import net.minecraft.command.arguments.EntityArgumentType.multiplePlayer
import net.minecraft.server.command.ServerCommandManager.argument
import net.minecraft.server.command.ServerCommandManager.literal
import net.minecraft.server.command.ServerCommandSource

fun setter(
    literal: String,
    set: (HpLevelHandler, Int) -> Unit
): LiteralArgumentBuilder<ServerCommandSource> {
    return literal(literal)
        .requires { it.hasPermissionLevel(2) }
        .then(
            argument("amount", integer(0))
                .executes { ctx ->
                    val player = ctx.source.player
                    val amount = getInteger(ctx, "amount")
                    val handler = player.hpLevelHandler
                    set(handler, amount)
                    return@executes 0
                }
        )
        .then(argument("players", multiplePlayer())
            .then(argument("amount", integer(0))
                .executes { ctx ->
                    val players = method_9312(ctx, "players")
                    val amount = getInteger(ctx, "amount")
                    players.map {
                        it.hpLevelHandler
                    }.forEach {
                        set(it, amount)
                    }
                    return@executes 0
                }
            )
        )
}

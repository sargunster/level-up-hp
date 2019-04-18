package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.command.arguments.EntityArgumentType.getPlayers
import net.minecraft.command.arguments.EntityArgumentType.players
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
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
        .then(argument("players", players())
            .then(argument("amount", integer(0))
                .executes { ctx ->
                    val players = getPlayers(ctx, "players")
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

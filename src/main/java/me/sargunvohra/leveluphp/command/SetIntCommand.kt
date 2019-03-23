package me.sargunvohra.leveluphp.command

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.leveluphp.core.PlayerLevelHandler
import me.sargunvohra.leveluphp.core.playerLevelHandler
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.EntityArgument.getPlayers
import net.minecraft.command.arguments.EntityArgument.players

internal class SetIntCommand {
    companion object {

        fun register(
            literal: String,
            permissionLevel: Int,
            set: (PlayerLevelHandler, Int) -> Unit
        ): LiteralArgumentBuilder<CommandSource> {
            return literal(literal)
                .requires { it.hasPermissionLevel(permissionLevel) }
                .then(
                    argument("amount", integer(0))
                        .executes { ctx ->
                            val player = ctx.source.asPlayer()
                            val amount = getInteger(ctx, "amount")

                            player.playerLevelHandler.ifPresent {
                                set(it, amount)
                            }

                            return@executes 0
                        }
                )
                .then(argument("players", players())
                    .then(argument("amount", integer(0))
                        .executes { ctx ->
                            val players = getPlayers(ctx, "players")
                            val amount = getInteger(ctx, "amount")

                            players.forEach { player ->
                                player.playerLevelHandler.ifPresent {
                                    set(it, amount)
                                }
                            }

                            return@executes 0
                        }
                    )
                )
        }
    }
}

package me.sargunvohra.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.leveluphp.core.PlayerLevelHandler
import me.sargunvohra.leveluphp.core.playerLevelHandler
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.EntityArgument.getPlayer
import net.minecraft.command.arguments.EntityArgument.player
import net.minecraft.util.text.ITextComponent

internal class GetCommand {
    companion object {
        fun register(
            literal: String,
            permissionLevel: Int,
            response: (PlayerLevelHandler) -> ITextComponent
        ): LiteralArgumentBuilder<CommandSource> {
            return literal(literal)
                .requires { it.hasPermissionLevel(permissionLevel) }
                .executes { ctx ->
                    ctx.source.asPlayer().playerLevelHandler.ifPresent {
                        ctx.source.sendFeedback(response(it), true)
                    }
                    return@executes 0
                }
                .then(
                    argument("player", player())
                        .executes { ctx ->
                            getPlayer(ctx, "player").playerLevelHandler.ifPresent {
                                ctx.source.sendFeedback(response(it), true)
                            }
                            return@executes 0
                        }
                )
        }
    }
}

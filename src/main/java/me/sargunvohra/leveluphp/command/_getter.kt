package me.sargunvohra.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.leveluphp.level.Leveller
import me.sargunvohra.leveluphp.level.leveller
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.EntityArgument.getPlayer
import net.minecraft.command.arguments.EntityArgument.player
import net.minecraft.util.text.ITextComponent

fun getter(
    literal: String,
    response: (Leveller) -> ITextComponent
): LiteralArgumentBuilder<CommandSource> {
    return literal(literal)
        .executes { ctx ->
            ctx.source.asPlayer().leveller.ifPresent {
                ctx.source.sendFeedback(response(it), true)
            }
            return@executes 0
        }
        .then(
            argument("player", player())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    getPlayer(ctx, "player").leveller.ifPresent {
                        ctx.source.sendFeedback(response(it), true)
                    }
                    return@executes 0
                }
        )
}

package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigLoader
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

fun getter(
    literal: String,
    response: (HpLevelHandler) -> ITextComponent
): LiteralArgumentBuilder<CommandSource> {
    return literal(literal)
        .executes { ctx ->
            val handler = ctx.source.asPlayer().hpLevelHandler
            ctx.source.sendFeedback(response(handler), false)
            return@executes 0
        }
        .then(
            Commands.argument("player", EntityArgument.player())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    val player = EntityArgument.getPlayer(ctx, "player")
                    val handler = player.hpLevelHandler
                    ctx.source.sendFeedback(response(handler), true)
                    return@executes 0
                }
        )
}

fun setter(
    literal: String,
    set: (HpLevelHandler, Int) -> Unit
): LiteralArgumentBuilder<CommandSource> {
    return literal(literal)
        .requires { it.hasPermissionLevel(2) }
        .then(Commands.argument("players", EntityArgument.players())
            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                .executes { ctx ->
                    val players = EntityArgument.getPlayers(ctx, "players")
                    val amount = IntegerArgumentType.getInteger(ctx, "amount")
                    players
                        .map { it.hpLevelHandler }
                        .forEach { set(it, amount) }
                    return@executes 0
                }
            )
        )
}

fun buildLevelUpHpCommand(): LiteralArgumentBuilder<CommandSource> {
    val base = literal("leveluphp")

    listOf(
        setter("setxp") { target, xp -> target.xp = xp },
        setter("addxp") { target, xp -> target.xp += xp },
        setter("setlevel") { target, level -> target.level = level },
        setter("addlevel") { target, levels -> target.level += levels },
        getter("check") { target ->
            StringTextComponent("Level: ${target.level}\nXP: ${target.xp}/${target.currentXpTarget}")
        },
        literal("checkconfig")
            .executes { context ->
                context.source.sendFeedback(
                    StringTextComponent(LevellingConfigLoader.config.toString()),
                    false
                )
                return@executes 0
            }
    ).forEach { subCmd -> base.then(subCmd) }

    return base
}

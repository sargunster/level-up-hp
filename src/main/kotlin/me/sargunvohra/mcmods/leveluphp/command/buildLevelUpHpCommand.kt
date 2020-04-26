package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigManager
import me.sargunvohra.mcmods.leveluphp.core.HpLeveller
import me.sargunvohra.mcmods.leveluphp.core.currentXpTarget
import me.sargunvohra.mcmods.leveluphp.core.hpLeveller
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

private fun getter(
    literal: String,
    response: (HpLeveller) -> ITextComponent
): LiteralArgumentBuilder<CommandSource> {
    return literal(literal)
        .executes { ctx ->
            val handler = ctx.source.asPlayer().hpLeveller
            ctx.source.sendFeedback(response(handler), false)
            return@executes 0
        }
        .then(
            Commands.argument("player", EntityArgument.player())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    val player = EntityArgument.getPlayer(ctx, "player")
                    val handler = player.hpLeveller
                    ctx.source.sendFeedback(response(handler), true)
                    return@executes 0
                }
        )
}

private fun setter(
    literal: String,
    set: (HpLeveller, Int) -> Unit
): LiteralArgumentBuilder<CommandSource> {
    return literal(literal)
        .requires { it.hasPermissionLevel(2) }
        .then(Commands.argument("players", EntityArgument.players())
            .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                .executes { ctx ->
                    val players = EntityArgument.getPlayers(ctx, "players")
                    val amount = IntegerArgumentType.getInteger(ctx, "amount")
                    players
                        .map { it.hpLeveller }
                        .forEach { set(it, amount) }
                    return@executes 0
                }
            )
        )
}

fun buildLevelUpHpCommand(): LiteralArgumentBuilder<CommandSource> {
    val base = literal("leveluphp")

    listOf(
        getter("getxp") { target -> StringTextComponent("${target.xp}") },
        setter("setxp") { target, xp -> target.xp = xp },
        setter("addxp") { target, xp -> target.xp += xp },
        getter("getlevel") { target -> StringTextComponent("${target.level}") },
        setter("setlevel") { target, level -> target.level = level },
        setter("addlevel") { target, levels -> target.level += levels },
        getter("check") { target ->
            StringTextComponent(
                listOf(
                    "Level: ${target.level}",
                    "XP: ${target.xp}/${target.currentXpTarget}"
                ).joinToString("\n")
            )
        },
        literal("checkconfig").executes { context ->
            context.source.sendFeedback(
                StringTextComponent(LevellingConfigManager.config.toString()),
                false
            )
            return@executes 0
        }
    ).forEach { subCmd -> base.then(subCmd) }

    return base
}

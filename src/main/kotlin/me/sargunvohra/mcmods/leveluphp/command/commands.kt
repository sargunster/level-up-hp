package me.sargunvohra.mcmods.leveluphp.command

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.command.arguments.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

fun getter(
    literal: String,
    response: (HpLevelHandler) -> Text
): LiteralArgumentBuilder<ServerCommandSource> {
    return literal(literal)
        .executes { ctx ->
            val handler = ctx.source.player.hpLevelHandler
            ctx.source.sendFeedback(response(handler), false)
            return@executes 0
        }
        .then(
            CommandManager.argument("player", EntityArgumentType.player())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    val player = EntityArgumentType.getPlayer(ctx, "player")
                    val handler = player.hpLevelHandler
                    ctx.source.sendFeedback(response(handler), true)
                    return@executes 0
                }
        )
}

fun setter(
    literal: String,
    set: (HpLevelHandler, Int) -> Unit
): LiteralArgumentBuilder<ServerCommandSource> {
    return literal(literal)
        .requires { it.hasPermissionLevel(2) }
        .then(CommandManager.argument("players", EntityArgumentType.players())
            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                .executes { ctx ->
                    val players = EntityArgumentType.getPlayers(ctx, "players")
                    val amount = IntegerArgumentType.getInteger(ctx, "amount")
                    players
                        .map { it.hpLevelHandler }
                        .forEach { set(it, amount) }
                    return@executes 0
                }
            )
        )
}

fun buildLevelUpHpCommand(): LiteralArgumentBuilder<ServerCommandSource> {
    val base = literal("leveluphp")

    listOf(
        setter("setxp") { target, xp -> target.xp = xp },
        setter("addxp") { target, xp -> target.xp += xp },
        setter("setlevel") { target, level -> target.level = level },
        setter("addlevel") { target, levels -> target.level += levels },
        getter("check") { target ->
            LiteralText("Level: ${target.level}\nXP: ${target.xp}/${target.currentXpTarget}")
        },
        literal("checkconfig")
            .executes { context ->
                context.source.sendFeedback(
                    LiteralText(LevelUpHp.reloadListener.config.toString()),
                    false
                )
                return@executes 0
            }
    ).forEach { subCmd -> base.then(subCmd) }

    return base
}

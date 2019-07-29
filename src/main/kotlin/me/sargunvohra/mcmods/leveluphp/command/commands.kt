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
        setter("setxp") { obj, xp -> obj.xp = xp },
        setter("addxp") { obj, xp -> obj.xp += xp },
        setter("setlevel") { obj, level -> obj.level = level },
        setter("addlevel") { obj, levels -> obj.level += levels },
        getter("check") {
            LiteralText("level: ${it.level}, xp: ${it.xp}/${it.currentXpTarget}")
        },
        literal("config")
            .executes {
                it.source.sendFeedback(
                    LiteralText(LevelUpHp.reloadListener.config.toString()),
                    false
                )
                return@executes 0
            }
    ).forEach { base.then(it) }

    return base
}

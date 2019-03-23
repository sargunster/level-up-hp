package me.sargunvohra.leveluphp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.core.PlayerLevelHandler
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.literal
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString

class RootCommand {
    companion object {

        private const val MIN_PERMISSION = 2

        fun register(): LiteralArgumentBuilder<CommandSource> {
            val base = literal(LevelUpHp.MOD_ID)
            listOf(
                SetIntCommand.register("setxp", MIN_PERMISSION) { obj, xp -> obj.xp = xp },
                SetIntCommand.register("addxp", MIN_PERMISSION) { obj, xp -> obj.xp += xp },
                SetIntCommand.register("setlevel", MIN_PERMISSION) { obj, level -> obj.level = level },
                SetIntCommand.register("addlevel", MIN_PERMISSION) { obj, levels -> obj.level += levels },
                GetCommand.register("check", MIN_PERMISSION) { checkCommand(it) }
            ).forEach { base.then(it) }
            return base
        }

        private fun checkCommand(handler: PlayerLevelHandler): ITextComponent {
            return TextComponentString(
                String.format("level: %d, xp: %d/%d", handler.level, handler.xp, handler.xpTarget)
            )
        }
    }
}

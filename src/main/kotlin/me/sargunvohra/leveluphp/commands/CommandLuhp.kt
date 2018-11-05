package me.sargunvohra.leveluphp.commands

import me.sargunvohra.leveluphp.commands.sub.CommandAddLevel
import me.sargunvohra.leveluphp.commands.sub.CommandAddXp
import me.sargunvohra.leveluphp.commands.sub.CommandCheck
import me.sargunvohra.leveluphp.commands.sub.CommandHelp
import me.sargunvohra.leveluphp.commands.sub.CommandSetLevel
import me.sargunvohra.leveluphp.commands.sub.CommandSetXp
import net.minecraft.command.ICommandSender
import net.minecraftforge.server.command.CommandTreeBase

object CommandLuhp : CommandTreeBase() {

    init {
        addSubcommand(CommandHelp)
        addSubcommand(CommandCheck)
        addSubcommand(CommandSetLevel)
        addSubcommand(CommandAddLevel)
        addSubcommand(CommandSetXp)
        addSubcommand(CommandAddXp)
    }

    override fun getName() = "luhp"

    override fun getUsage(sender: ICommandSender) = "/luhp help"
}

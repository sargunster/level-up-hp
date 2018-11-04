package me.sargunvohra.leveluphp.commands

import me.sargunvohra.leveluphp.commands.sub.*
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
package me.sargunvohra.leveluphp.commands.sub

import me.sargunvohra.leveluphp.commands.CommandLuhp
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString

object CommandHelp : CommandBase() {

    override fun getName() = "help"

    override fun getUsage(sender: ICommandSender) = "/luhp help <command>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 1) throw WrongUsageException(getUsage(sender))

        val name = args[0]
        val command = CommandLuhp.commandMap[name]

        if (command != null)
            sender.sendMessage(TextComponentString(command.getUsage(sender)))
        else
            throw WrongUsageException(getUsage(sender))
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return CommandLuhp.commandMap.keys.toList()
    }
}
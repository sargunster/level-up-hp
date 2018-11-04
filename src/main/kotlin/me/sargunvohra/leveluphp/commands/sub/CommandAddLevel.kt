package me.sargunvohra.leveluphp.commands.sub

import me.sargunvohra.leveluphp.ModConfig
import me.sargunvohra.leveluphp.extensions.luhpLevel
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString

object CommandAddLevel : CommandBase() {

    override fun getName() = "addlevel"

    override fun getUsage(sender: ICommandSender?) = "/luhp addlevel <player> <level>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 2)
            throw WrongUsageException(getUsage(sender))
        val player = getPlayer(server, sender, args[0])
        player.luhpLevel += parseInt(args[1], 0, ModConfig.maximumLevel - player.luhpLevel)
        sender.sendMessage(TextComponentString("Successfully updated player HP-LVL!"))
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}
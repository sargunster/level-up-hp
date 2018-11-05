package me.sargunvohra.leveluphp.commands.sub

import me.sargunvohra.leveluphp.extensions.luhpXp
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString

object CommandAddXp : CommandBase() {

    override fun getName() = "addxp"

    override fun getUsage(sender: ICommandSender?) = "/luhp addxp <player> <xp>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 2)
            throw WrongUsageException(getUsage(sender))
        val player = getPlayer(server, sender, args[0])
        player.luhpXp += parseInt(args[1], 0, 100000)
        sender.sendMessage(TextComponentString("Successfully updated player XP!"))
    }

    override fun getTabCompletions(
        server: MinecraftServer,
        sender: ICommandSender,
        args: Array<out String>,
        targetPos: BlockPos?
    ): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}

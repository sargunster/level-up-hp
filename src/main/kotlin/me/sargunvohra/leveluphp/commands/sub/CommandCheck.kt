package me.sargunvohra.leveluphp.commands.sub

import me.sargunvohra.leveluphp.extensions.luhpLevel
import me.sargunvohra.leveluphp.extensions.luhpXp
import me.sargunvohra.leveluphp.extensions.neededLuhpXp
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString

object CommandCheck : CommandBase() {

    override fun getName() = "check"

    override fun getUsage(sender: ICommandSender?) = "/luhp check <player>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 1)
            throw WrongUsageException(getUsage(sender))
        val player = getPlayer(server, sender, args[0])

        val name = player.name
        val level = player.luhpLevel
        val xp = player.luhpXp
        val needed = player.neededLuhpXp
        val msg = "$name is HP-LVL $level hp and has $xp/$needed HP-XP towards the next level"

        sender.sendMessage(TextComponentString(msg))
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

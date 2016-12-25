package me.sargunvohra.leveluphp

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.server.command.CommandTreeBase

object CommandHp : CommandBase() {

    override fun getName() = "hp"

    override fun getUsage(sender: ICommandSender) = "/hp"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.isNotEmpty())
            throw WrongUsageException(CommandCheck.getUsage(sender))

        if (sender !is EntityPlayerMP)
            throw CommandException("Must be a player to check HP status")

        val msg = if (sender.luhpLevel >= LuhpConfig.maximumLevel)
            "You're maxed out!"
        else
            "You have ${ChatFormatting.RED}${sender.luhpXp}/${sender.neededLuhpXp}${ChatFormatting.RESET}" +
                    " xp towards the next HP upgrade"
        sender.sendMessage(TextComponentString(msg))
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender) = sender is EntityPlayerMP
}

object CommandLuhp : CommandTreeBase() {

    init {
        addSubcommand(CommandHelp)
        addSubcommand(CommandCheck)
        addSubcommand(CommandSetLevel)
        addSubcommand(CommandAddHp)
        addSubcommand(CommandSetXp)
        addSubcommand(CommandAddXp)
    }

    override fun getName() = "luhp"

    override fun getUsage(sender: ICommandSender) = "/luhp help"

}

object CommandSetLevel : CommandBase() {

    override fun getName() = "setlevel"

    override fun getUsage(sender: ICommandSender?) = "/luhp setlevel <player> <level>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 2)
            throw WrongUsageException(getUsage(sender))
        getPlayer(server, sender, args[0]).luhpLevel = parseInt(args[1], 1, LuhpConfig.maximumLevel)
        sender.sendMessage(TextComponentString("Successfully updated player HP!"))
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}

object CommandAddHp : CommandBase() {

    override fun getName() = "addlevel"

    override fun getUsage(sender: ICommandSender?) = "/luhp addlevel <player> <level>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 2)
            throw WrongUsageException(getUsage(sender))
        val player = getPlayer(server, sender, args[0])
        player.luhpLevel += parseInt(args[1], 0, LuhpConfig.maximumLevel - player.luhpLevel)
        sender.sendMessage(TextComponentString("Successfully updated player HP!"))
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}

object CommandSetXp : CommandBase() {

    override fun getName() = "setxp"

    override fun getUsage(sender: ICommandSender?) = "/luhp setxp <player> <xp>"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size != 2)
            throw WrongUsageException(getUsage(sender))
        val player = getPlayer(server, sender, args[0])
        player.luhpXp = parseInt(args[1], 0, 100000)
        sender.sendMessage(TextComponentString("Successfully updated player XP!"))
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}

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

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}

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

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<out String>, targetPos: BlockPos?): List<String> {
        return if (args.size == 1)
            getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else
            emptyList()
    }
}

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
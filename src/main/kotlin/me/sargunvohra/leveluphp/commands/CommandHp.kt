package me.sargunvohra.leveluphp.commands

import me.sargunvohra.leveluphp.ModConfig
import me.sargunvohra.leveluphp.commands.sub.CommandCheck
import me.sargunvohra.leveluphp.extensions.luhpLevel
import me.sargunvohra.leveluphp.extensions.luhpXp
import me.sargunvohra.leveluphp.extensions.neededLuhpXp
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString

object CommandHp : CommandBase() {

    override fun getName() = "hp"

    override fun getUsage(sender: ICommandSender) = "/hp"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.isNotEmpty())
            throw WrongUsageException(CommandCheck.getUsage(sender))

        if (sender !is EntityPlayerMP)
            throw CommandException("Must be a player to check HP status")

        val msg = if (sender.luhpLevel >= ModConfig.maximumLevel)
            "You're maxed out!"
        else
            "You have §c${sender.luhpXp}/${sender.neededLuhpXp}§r" +
                    " xp towards the next HP upgrade"
        sender.sendMessage(TextComponentString(msg))
    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender) = sender is EntityPlayerMP
}
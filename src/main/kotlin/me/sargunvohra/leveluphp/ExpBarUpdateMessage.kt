package me.sargunvohra.leveluphp

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

data class ExpBarUpdateMessage(
        var currentXp: Int = 0,
        var neededXp: Int = 0
) : IMessage, IMessageHandler<ExpBarUpdateMessage, IMessage> {

    companion object {
        var LATEST_CURRENT_XP = 0
        var LATEST_NEEDED_XP = 0
        val LATEST_FRACTION get() = LATEST_CURRENT_XP.toFloat() / LATEST_NEEDED_XP
    }

    override fun fromBytes(buf: ByteBuf) {
        currentXp = buf.readInt()
        neededXp = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(currentXp)
        buf.writeInt(neededXp)
    }

    override fun onMessage(message: ExpBarUpdateMessage, ctx: MessageContext): IMessage? {
        if (ctx.side != Side.CLIENT)
            return null
        LATEST_CURRENT_XP = message.currentXp
        LATEST_NEEDED_XP = message.neededXp
        return null
    }
}
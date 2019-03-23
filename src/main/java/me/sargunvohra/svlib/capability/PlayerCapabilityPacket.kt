package me.sargunvohra.svlib.capability

import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import net.minecraft.nbt.INBTBase
import net.minecraft.nbt.NBTSizeTracker
import net.minecraft.network.PacketBuffer

data class PlayerCapabilityPacket(val data: INBTBase) {
    companion object {
        fun encoder(message: PlayerCapabilityPacket, buffer: PacketBuffer) {
            val data = message.data
            val out = ByteBufOutputStream(buffer)
            out.writeByte(data.id.toInt())
            message.data.write(out)
        }

        fun decoder(buffer: PacketBuffer): PlayerCapabilityPacket {
            val `in` = ByteBufInputStream(buffer)
            val data = INBTBase.create(`in`.readByte())
            data.read(`in`, 0, NBTSizeTracker.INFINITE)
            return PlayerCapabilityPacket(data)
        }
    }
}

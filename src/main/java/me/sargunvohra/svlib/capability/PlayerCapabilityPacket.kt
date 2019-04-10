package me.sargunvohra.svlib.capability

import com.google.gson.Gson
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import me.sargunvohra.leveluphp.data.LevellingConfig
import net.minecraft.nbt.INBTBase
import net.minecraft.nbt.NBTSizeTracker
import net.minecraft.network.PacketBuffer
import java.lang.StringBuilder

// todo: fix the shitty hack to send the config to the client
// todo: actually I'll probably never fix it because MC 1.13 is EOL

data class PlayerCapabilityPacket(val data: INBTBase, val config: LevellingConfig) {
    companion object {
        val gson = Gson()

        fun encoder(message: PlayerCapabilityPacket, buffer: PacketBuffer) {
            val data = message.data
            val config = gson.toJson(message.config)
            val out = ByteBufOutputStream(buffer)

            // write tag
            out.writeByte(data.id.toInt())
            message.data.write(out)

            // write config
            out.writeInt(config.length)
            out.writeChars(config)
        }

        fun decoder(buffer: PacketBuffer): PlayerCapabilityPacket {
            val `in` = ByteBufInputStream(buffer)

            // read tag
            val data = INBTBase.create(`in`.readByte())
            data.read(`in`, 0, NBTSizeTracker.INFINITE)

            // read config
            val len = `in`.readInt()
            val json = (1..len)
                .fold(StringBuilder()) { s, _ -> s.append(`in`.readChar()) }
                .toString()
            val config = gson.fromJson(json, LevellingConfig::class.java)

            return PlayerCapabilityPacket(data, config)
        }
    }
}

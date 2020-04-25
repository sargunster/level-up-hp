package me.sargunvohra.mcmods.leveluphp.network

import com.google.gson.Gson
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import me.sargunvohra.mcmods.leveluphp.LuhpMod
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfig
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigManager
import me.sargunvohra.mcmods.leveluphp.hpLevelHandlerOpt
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.NBTSizeTracker
import net.minecraft.nbt.NBTTypes
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent

object SyncPacketConsumer {
    private val gson = Gson()

    fun accept(message: ByteBuf, context: NetworkEvent.Context) {
        val input = ByteBufInputStream(message)

        // read level data
        val reader = NBTTypes.func_229710_a_(input.readInt())
        val data = reader.func_225649_b_(input, 0, NBTSizeTracker.INFINITE)

        // read config data (shitty way to do it but ¯\_(ツ)_/¯)
        val len = input.readInt()
        val json = (1..len).fold(StringBuilder()) { s, _ -> s.append(input.readChar()) }.toString()
        val config = gson.fromJson(json, LevellingConfig::class.java)

        context.enqueueWork {
            try {
                config.validate()
                LevellingConfigManager.config = config
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            val player = Minecraft.getInstance().player ?: return@enqueueWork
            player.hpLevelHandlerOpt?.readFromTag(data)
        }

        context.packetHandled = true
    }

    fun send(player: ServerPlayerEntity, data: HpLevelHandler) {
        val buffer = PacketBuffer(Unpooled.buffer())
        val output = ByteBufOutputStream(buffer)

        // write level data
        val tag = data.writeToTag()
        output.writeInt(tag.id.toInt())
        tag.write(output)

        // write config data (shitty way to do it but ¯\_(ツ)_/¯)
        val configJson = gson.toJson(LevellingConfigManager.config)
        output.writeInt(configJson.length)
        output.writeChars(configJson)

        output.close()
        LuhpMod.channel.sendTo(
            output.buffer(),
            player.connection.netManager,
            NetworkDirection.PLAY_TO_CLIENT
        )
    }
}

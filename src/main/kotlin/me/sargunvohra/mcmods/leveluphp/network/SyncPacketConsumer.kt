package me.sargunvohra.mcmods.leveluphp.network

import com.google.gson.Gson
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfig
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.nbt.PositionTracker
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagReaders
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import java.lang.StringBuilder

class SyncPacketConsumer : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val input = ByteBufInputStream(buffer)

        // read level data
        val reader = TagReaders.of(input.readInt())
        val data = reader.read(input, 0, PositionTracker.DEFAULT)

        // read config data (shitty way to do it but ¯\_(ツ)_/¯)
        val len = input.readInt()
        val json = (1..len).fold(StringBuilder()) { s, _ -> s.append(input.readChar()) }.toString()
        val config = gson.fromJson(json, LevellingConfig::class.java)

        context.taskQueue.execute {
            try {
                config.validate()
                LevelUpHp.reloadListener.config = config
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            val player = context.player as ClientPlayerEntity
            player.hpLevelHandler.readFromTag(data)
        }
    }

    companion object {
        val CHANNEL = LevelUpHp.id("hp_level_sync_1") // include protocol version
        val gson = Gson()

        fun send(player: ServerPlayerEntity, data: HpLevelHandler) {
            val buffer = PacketByteBuf(Unpooled.buffer())
            val output = ByteBufOutputStream(buffer)

            // write level data
            val tag = data.writeToTag()
            output.writeInt(tag.type.toInt())
            tag.write(output)

            // write config data (shitty way to do it but ¯\_(ツ)_/¯)
            val configJson = gson.toJson(LevelUpHp.reloadListener.config)
            output.writeInt(configJson.length)
            output.writeChars(configJson)

            output.close()
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, CHANNEL, buffer)
        }
    }
}

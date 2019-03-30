package me.sargunvohra.mcmods.leveluphp.network

import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.nbt.PositionTracker
import net.minecraft.nbt.Tag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf

class SyncPacketConsumer : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val input = ByteBufInputStream(buffer)
        val data = Tag.createTag(input.readByte())
        data.read(input, 0, PositionTracker.DEFAULT)

        context.taskQueue.execute {
            val player = context.player as ClientPlayerEntity
            player.hpLevelHandler.readFromTag(data)
        }
    }

    companion object {
        val CHANNEL = LevelUpHp.id("hp_level_packets")

        fun send(player: ServerPlayerEntity, data: HpLevelHandler) {
            val buffer = PacketByteBuf(Unpooled.buffer())
            val output = ByteBufOutputStream(buffer)
            val tag = data.writeToTag()
            output.writeByte(tag.type.toInt())
            tag.write(output)
            output.close()
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, CHANNEL, buffer)
        }
    }
}

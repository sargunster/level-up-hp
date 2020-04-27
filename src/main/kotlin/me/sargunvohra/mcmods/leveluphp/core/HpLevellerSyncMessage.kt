package me.sargunvohra.mcmods.leveluphp.core

import com.google.gson.Gson
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfig
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigManager
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.INBT
import net.minecraft.nbt.NBTSizeTracker
import net.minecraft.nbt.NBTTypes
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

data class HpLevellerSyncMessage(
    val hpLevellerNbt: INBT,
    val levellingConfig: LevellingConfig
) {
    fun consume(contextSupplier: Supplier<NetworkEvent.Context>) {
        val context = contextSupplier.get()
        context.enqueueWork {
            try {
                levellingConfig.validate()
                LevellingConfigManager.config = levellingConfig
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            val player = Minecraft.getInstance().player ?: return@enqueueWork
            player.hpLevellerOrNull?.let {
                CapabilityRegistrationSubscriber.HP_LEVELLER_CAPABILITY.readNBT(it, null, hpLevellerNbt)
            }
        }
        context.packetHandled = true
    }

    fun send(target: ServerPlayerEntity) {
        HpLevellerSyncManager.CHANNEL.sendTo(
            this,
            target.connection.netManager,
            NetworkDirection.PLAY_TO_CLIENT
        )
    }

    companion object {
        private val GSON = Gson()

        fun create(leveller: HpLeveller): HpLevellerSyncMessage {
            return HpLevellerSyncMessage(
                HpLeveller.Serializer.writeNBT(CapabilityRegistrationSubscriber.HP_LEVELLER_CAPABILITY, leveller, null),
                LevellingConfigManager.config
            )
        }

        fun encode(message: HpLevellerSyncMessage, packet: PacketBuffer) {
            val output = ByteBufOutputStream(packet)

            // write hp leveller data
            output.writeInt(message.hpLevellerNbt.id.toInt())
            message.hpLevellerNbt.write(output)

            // write config data (shitty way to do it but ¯\_(ツ)_/¯)
            val configJson = GSON.toJson(LevellingConfigManager.config)
            output.writeUTF(configJson)

            output.close()
        }

        fun decode(packet: PacketBuffer): HpLevellerSyncMessage {
            val input = ByteBufInputStream(packet)

            // read hp leveller data
            val tagReader = NBTTypes.func_229710_a_(input.readInt())
            val levellerNbt = tagReader.func_225649_b_(input, 0, NBTSizeTracker.INFINITE)

            // read config data
            val configJson = input.readUTF()
            val config = GSON.fromJson(configJson, LevellingConfig::class.java)

            return HpLevellerSyncMessage(levellerNbt, config)
        }
    }
}

package me.sargunvohra.mcmods.leveluphp.level

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional


interface IHpLeveller {
    val hpLevelHandler: HpLevelHandler

    class Impl : IHpLeveller {
        override lateinit var hpLevelHandler: HpLevelHandler
    }

    object Storage : Capability.IStorage<IHpLeveller> {
        override fun readNBT(
            capability: Capability<IHpLeveller>,
            instance: IHpLeveller,
            side: Direction?,
            nbt: INBT
        ) {
            instance.hpLevelHandler.readFromTag(nbt)
        }

        override fun writeNBT(
            capability: Capability<IHpLeveller>,
            instance: IHpLeveller,
            side: Direction?
        ): INBT? {
            return instance.hpLevelHandler.writeToTag()
        }
    }

    class Provider(player: PlayerEntity) : ICapabilitySerializable<INBT> {
        private val capability = Impl().apply {
            hpLevelHandler = HpLevelHandler(player)
        }

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            if (cap == CAPABILITY) {
                return LazyOptional.of { capability }.cast()
            }
            return LazyOptional.empty()
        }

        override fun deserializeNBT(nbt: INBT) {
            CAPABILITY.storage.readNBT(CAPABILITY, capability, null, nbt)
        }

        override fun serializeNBT(): INBT? {
            return CAPABILITY.storage.writeNBT(CAPABILITY, capability, null)
        }
    }

    companion object {
        @JvmStatic
        @CapabilityInject(IHpLeveller::class)
        lateinit var CAPABILITY: Capability<IHpLeveller>
    }
}

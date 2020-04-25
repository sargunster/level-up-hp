package me.sargunvohra.mcmods.leveluphp.capability

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject


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

    class Provider(player: PlayerEntity)
        : SimpleCapabilityProvider<IHpLeveller>(HP_LEVELLER_CAPABILITY) {

        override val implementation = Impl().apply {
            hpLevelHandler = HpLevelHandler(player)
        }
    }

    companion object {
        @JvmStatic
        @CapabilityInject(IHpLeveller::class)
        lateinit var HP_LEVELLER_CAPABILITY: Capability<IHpLeveller>
    }
}

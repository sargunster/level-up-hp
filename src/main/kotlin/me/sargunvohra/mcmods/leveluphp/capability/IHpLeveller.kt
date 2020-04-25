package me.sargunvohra.mcmods.leveluphp.capability

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability


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
        : SimpleCapabilityProvider<IHpLeveller>(LuhpCapabilities.HP_LEVELLER_CAPABILITY) {

        override val instance = Impl().apply {
            hpLevelHandler = HpLevelHandler(player)
        }
    }
}

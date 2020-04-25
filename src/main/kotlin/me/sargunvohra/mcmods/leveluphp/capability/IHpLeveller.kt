package me.sargunvohra.mcmods.leveluphp.capability

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability


interface IHpLeveller {
    val hpLevelHandler: HpLevelHandler

    class PlayerHpLeveller : IHpLeveller {
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
        : SimpleStoredCapabilityHolder<IHpLeveller>(LuhpCapabilities.HP_LEVELLER_CAPABILITY) {

        override val instance = PlayerHpLeveller().apply {
            hpLevelHandler = HpLevelHandler(player)
        }
    }
}

@file:Suppress("unused")

package me.sargunvohra.leveluphp

import me.sargunvohra.leveluphp.Capabilities.LUHP_DATA
import me.sargunvohra.leveluphp.extensions.luhpData
import me.sargunvohra.leveluphp.extensions.luhpInitialize
import me.sargunvohra.leveluphp.extensions.luhpLevel
import me.sargunvohra.leveluphp.extensions.luhpUpdateClientOverlay
import me.sargunvohra.leveluphp.extensions.luhpUpdateHpModifier
import me.sargunvohra.leveluphp.extensions.luhpXp
import me.sargunvohra.leveluphp.extensions.penaltyLuhpXp
import me.sargunvohra.leveluphp.extensions.sendStatusMsg
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.passive.IAnimals
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.SoundCategory
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent as FMLPlayerEvent

object EventHandler {

    @SubscribeEvent
    fun onPlayerLoad(event: PlayerEvent.LoadFromFile) {
        val player = event.entityPlayer as? EntityPlayerMP ?: return
        val data = player.luhpData

        if (!data.initialized)
            player.luhpInitialize()
        else
            player.luhpUpdateHpModifier()
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        val newPlayer = event.entityPlayer as? EntityPlayerMP ?: return

        val shouldReset = event.isWasDeath && ModConfig.resetOnDeath

        if (shouldReset) {
            newPlayer.luhpInitialize()
        } else {
            val oldPlayer = event.original as? EntityPlayerMP ?: return
            LUHP_DATA.readNBT(
                newPlayer.luhpData,
                null,
                LUHP_DATA.writeNBT(oldPlayer.luhpData, null)
            )

            if (event.isWasDeath)
                newPlayer.luhpXp -= newPlayer.penaltyLuhpXp
        }

        newPlayer.luhpUpdateHpModifier()

        if (!shouldReset)
            newPlayer.health = newPlayer.maxHealth
    }

    @SubscribeEvent
    fun onPlayerChangeDim(event: FMLPlayerEvent.PlayerChangedDimensionEvent) {
        val player = (event.player as? EntityPlayerMP ?: return)
        player.luhpUpdateHpModifier()
        player.luhpUpdateClientOverlay()
    }

    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        val player = (event.entity as? EntityPlayerMP ?: return)
        player.luhpUpdateHpModifier()
        player.luhpUpdateClientOverlay()
    }

    @SubscribeEvent
    fun onPlayerKill(event: LivingDeathEvent) {
        val source = event.source.trueSource as? EntityPlayerMP ?: return
        val oldLevel = source.luhpLevel

        when (event.entity) {
            is IMob -> source.luhpXp += ModConfig.monsterGain
            is IAnimals -> source.luhpXp += ModConfig.livestockGain
        }

        if (source.luhpLevel > oldLevel) {

            source.world.playSound(
                null, source.posX, source.posY, source.posZ,
                Sounds.levelUp, SoundCategory.PLAYERS, 1f, 1f
            )

            source.sendStatusMsg("§c§lHP UP!")

            if (ModConfig.healOnLevelUp)
                source.health = source.maxHealth
        }
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Post) {
        val mc = Minecraft.getMinecraft()

        if (
            !mc.playerController.gameIsSurvivalOrAdventure()
            || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE
            || event.isCanceled
            || !ModConfig.showCustomXpBar
        )
            return

        val left = event.resolution.scaledWidth / 2 - 91
        val top = event.resolution.scaledHeight - 26
        val fullWidth = 182
        val filledWidth = (fullWidth * ExpBarUpdateMessage.LATEST_FRACTION).toInt()
        val height = 3

        mc.textureManager.bindTexture(Resources.textureIcons)
        mc.ingameGUI.drawTexturedModalRect(left, top, 0, 3, fullWidth, height)
        mc.ingameGUI.drawTexturedModalRect(left, top, 0, 0, filledWidth, height)

        mc.textureManager.bindTexture(Gui.ICONS)
    }
}

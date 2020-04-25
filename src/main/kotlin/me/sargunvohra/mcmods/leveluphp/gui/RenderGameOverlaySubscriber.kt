package me.sargunvohra.mcmods.leveluphp.gui

import me.sargunvohra.mcmods.leveluphp.config.ClientConfig
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object RenderGameOverlaySubscriber {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun renderXpBar(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE)
            return
        if (!ClientConfig.instance.enableXpBarOverride)
            return
        val client = Minecraft.getInstance()
        val player = client.player ?: return
        event.isCanceled = true
        renderLuhpXpBars(client, player)
    }
}

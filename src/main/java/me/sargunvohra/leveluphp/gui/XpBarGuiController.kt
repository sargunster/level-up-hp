package me.sargunvohra.leveluphp.gui

import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.Resources
import me.sargunvohra.leveluphp.core.playerLevelHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent

class XpBarGuiController {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    internal fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        val mc = Minecraft.getInstance()

        if (!mc.playerController.gameIsSurvivalOrAdventure() ||
            event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE ||
            event.isCanceled)
            return

        mc.player.playerLevelHandler.ifPresent {
            event.isCanceled = true

            mc.profiler.startSection(LevelUpHp.MOD_ID)
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableBlend()
            mc.getTextureManager().bindTexture(Resources.texIcons)

            val scaledWidth = mc.mainWindow.scaledWidth
            val centerX = scaledWidth / 2
            val scaledHeight = mc.mainWindow.scaledHeight

            val fraction = it.xp.toFloat() / it.xpTarget
            renderBar(mc, centerX - 91, scaledHeight - 29, 0, (91 * fraction).toInt())
            renderBar(mc, centerX, scaledHeight - 29, 91, (91 * mc.player.experience).toInt())

            var str = "" + it.level
            val strWidth = mc.ingameGUI.fontRenderer.getStringWidth(str)
            renderLevel(mc, str, centerX - 92 - strWidth, scaledHeight - 30, 0xff3f3f)

            str = "" + mc.player.experienceLevel
            renderLevel(mc, str, centerX + 93, scaledHeight - 30, 0x80FF20)

            mc.getTextureManager().bindTexture(Gui.ICONS)
            GlStateManager.enableBlend()
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
            mc.profiler.endSection()
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun renderBar(mc: Minecraft, left: Int, top: Int, texX: Int, filledWidth: Int) {
        mc.ingameGUI.drawTexturedModalRect(left, top, texX, 0, 91, 5)
        mc.ingameGUI.drawTexturedModalRect(left, top, texX, 5, filledWidth, 5)
    }

    @OnlyIn(Dist.CLIENT)
    private fun renderLevel(mc: Minecraft, s: String, left: Int, top: Int, color: Int) {
        val gui = mc.ingameGUI
        gui.fontRenderer.drawString(s, (left + 1).toFloat(), top.toFloat(), 0)
        gui.fontRenderer.drawString(s, (left - 1).toFloat(), top.toFloat(), 0)
        gui.fontRenderer.drawString(s, left.toFloat(), (top + 1).toFloat(), 0)
        gui.fontRenderer.drawString(s, left.toFloat(), (top - 1).toFloat(), 0)
        gui.fontRenderer.drawString(s, left.toFloat(), top.toFloat(), color)
    }

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }
}

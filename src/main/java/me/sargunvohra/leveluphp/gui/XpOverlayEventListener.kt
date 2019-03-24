package me.sargunvohra.leveluphp.gui

import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.level.leveller
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@KotlinEventBusSubscriber(Dist.CLIENT, modid = LevelUpHp.MOD_ID)
object XpOverlayEventListener {

    private val modIcons = LevelUpHp.res("textures/gui/icons.png")

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        val mc = Minecraft.getInstance()

        if (!mc.playerController.gameIsSurvivalOrAdventure() ||
            event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE ||
            event.isCanceled
        )
            return

        mc.player.leveller.ifPresent {
            event.isCanceled = true

            mc.profiler.startSection(LevelUpHp.MOD_ID)
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableBlend()
            mc.getTextureManager().bindTexture(modIcons)

            val scaledWidth = mc.mainWindow.scaledWidth
            val centerX = scaledWidth / 2
            val scaledHeight = mc.mainWindow.scaledHeight

            val fraction = if (it.maxedOut) 1f else it.xp.toFloat() / it.xpTarget
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

    private fun renderBar(mc: Minecraft, left: Int, top: Int, texX: Int, filledWidth: Int) {
        mc.ingameGUI.drawTexturedModalRect(left, top, texX, 0, 91, 5)
        mc.ingameGUI.drawTexturedModalRect(left, top, texX, 5, filledWidth, 5)
    }

    private fun renderLevel(mc: Minecraft, s: String, left: Int, top: Int, color: Int) {
        val gui = mc.ingameGUI
        gui.fontRenderer.drawString(s, (left + 1).toFloat(), top.toFloat(), 0)
        gui.fontRenderer.drawString(s, (left - 1).toFloat(), top.toFloat(), 0)
        gui.fontRenderer.drawString(s, left.toFloat(), (top + 1).toFloat(), 0)
        gui.fontRenderer.drawString(s, left.toFloat(), (top - 1).toFloat(), 0)
        gui.fontRenderer.drawString(s, left.toFloat(), top.toFloat(), color)
    }
}

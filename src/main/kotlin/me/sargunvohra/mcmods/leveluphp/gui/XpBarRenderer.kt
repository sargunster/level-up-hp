package me.sargunvohra.mcmods.leveluphp.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.sargunvohra.mcmods.leveluphp.LuhpMod
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import me.sargunvohra.mcmods.leveluphp.hpLevelHandlerOpt
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.AbstractGui.GUI_ICONS_LOCATION
import net.minecraft.client.gui.FontRenderer

object XpBarRenderer {
    private val TEX_ICONS = LuhpMod.id("textures/gui/icons.png")

    fun render(client: Minecraft, player: ClientPlayerEntity) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableBlend()

        if (client.playerController!!.gameIsSurvivalOrAdventure()) {
            val window = client.mainWindow
            val left = window.scaledWidth / 2 - 91
            val levelHandler = player.hpLevelHandlerOpt
            client.textureManager.bindTexture(TEX_ICONS)

            client.profiler.startSection("levelUpHpBars")
            run {
                val top: Int = window.scaledHeight - 32 + 3

                if (levelHandler != null) {
                    val target = levelHandler.currentXpTarget
                    val hpXpBarWidth = if (target != 0) levelHandler.xp * 91 / target else 0
                    renderProgress(left, top, 0, hpXpBarWidth)
                }

                val mcXpBarWidth = (player.experience * 91).toInt()
                renderProgress(left + 91, top, 91, mcXpBarWidth)
            }
            client.profiler.endSection()

            client.profiler.startSection("levelUpHpBars")
            run {
                val fontRenderer = client.fontRenderer
                val centerX: Int = window.scaledWidth / 2
                val textTop = window.scaledHeight - 30

                if (levelHandler != null) {
                    val hpLevel = "" + levelHandler.level
                    val hpLevelWidth: Int = fontRenderer.getStringWidth(hpLevel)
                    renderLevel(
                        fontRenderer,
                        hpLevel,
                        textTop,
                        centerX - 92 - hpLevelWidth,
                        0xff3f3f
                    )
                }

                val mcLevel = "" + player.experienceLevel
                renderLevel(
                    fontRenderer,
                    mcLevel,
                    textTop,
                    centerX + 93,
                    0x80FF20
                )
            }
            client.profiler.endSection()

            client.textureManager.bindTexture(GUI_ICONS_LOCATION)
        }
        RenderSystem.enableBlend()
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private fun renderProgress(
        left: Int,
        top: Int,
        texX: Int,
        filled: Int
    ) {
        AbstractGui.blit(
            left,
            top,
            -90,
            texX.toFloat(),
            0F,
            91,
            5,
            256,
            256
        )
        if (filled > 0) {
            AbstractGui.blit(
                left,
                top,
                -90,
                texX.toFloat(),
                5F,
                filled,
                5,
                256,
                256
            )
        }
    }

    private fun renderLevel(
        fontRenderer: FontRenderer,
        str: String,
        top: Int,
        left: Int,
        color: Int
    ) {
        fontRenderer.drawString(str, (left + 1).toFloat(), top.toFloat(), 0)
        fontRenderer.drawString(str, (left - 1).toFloat(), top.toFloat(), 0)
        fontRenderer.drawString(str, left.toFloat(), (top + 1).toFloat(), 0)
        fontRenderer.drawString(str, left.toFloat(), (top - 1).toFloat(), 0)
        fontRenderer.drawString(str, left.toFloat(), top.toFloat(), color)
    }
}

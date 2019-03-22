package me.sargunvohra.leveluphp.gui;

import lombok.val;
import lombok.var;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.leveluphp.Resources;
import me.sargunvohra.leveluphp.core.PlayerLevelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class XpBarGuiController {

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
    val mc = Minecraft.getInstance();

    if (!mc.playerController.gameIsSurvivalOrAdventure()
        || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE
        || event.isCanceled()) return;

    mc.player
        .getCapability(PlayerLevelHandler.CAPABILITY)
        .ifPresent(
            handler -> {
              event.setCanceled(true);

              mc.profiler.startSection(LevelUpHp.MOD_ID);
              GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
              GlStateManager.disableBlend();
              mc.getTextureManager().bindTexture(Resources.texIcons);

              val scaledWidth = mc.mainWindow.getScaledWidth();
              val centerX = scaledWidth / 2;
              val scaledHeight = mc.mainWindow.getScaledHeight();

              renderBar(mc, centerX - 91, scaledHeight - 29, 0, (int) (91 * handler.xpFraction()));
              renderBar(mc, centerX, scaledHeight - 29, 91, (int) (91 * mc.player.experience));

              var str = "" + handler.getLevel();
              val strWidth = mc.ingameGUI.getFontRenderer().getStringWidth(str);
              renderLevel(mc, str, centerX - 92 - strWidth, scaledHeight - 30, 0xff3f3f);

              str = "" + mc.player.experienceLevel;
              renderLevel(mc, str, centerX + 92, scaledHeight - 30, 0x80FF20);

              mc.getTextureManager().bindTexture(Gui.ICONS);
              GlStateManager.enableBlend();
              GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
              mc.profiler.endSection();
            });
  }

  @OnlyIn(Dist.CLIENT)
  private void renderBar(Minecraft mc, int left, int top, int texX, int filledWidth) {
    mc.ingameGUI.drawTexturedModalRect(left, top, texX, 0, 91, 5);
    mc.ingameGUI.drawTexturedModalRect(left, top, texX, 5, filledWidth, 5);
  }

  @OnlyIn(Dist.CLIENT)
  private void renderLevel(Minecraft mc, String s, int left, int top, int color) {
    val gui = mc.ingameGUI;
    gui.getFontRenderer().drawString(s, (float) (left + 1), (float) top, 0);
    gui.getFontRenderer().drawString(s, (float) (left - 1), (float) top, 0);
    gui.getFontRenderer().drawString(s, (float) left, (float) (top + 1), 0);
    gui.getFontRenderer().drawString(s, (float) left, (float) (top - 1), 0);
    gui.getFontRenderer().drawString(s, (float) left, (float) top, color);
  }

  public void register() {
    MinecraftForge.EVENT_BUS.register(this);
  }
}

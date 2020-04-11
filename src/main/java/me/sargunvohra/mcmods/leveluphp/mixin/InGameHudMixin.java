package me.sargunvohra.mcmods.leveluphp.mixin;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.leveluphp.LevelUpHp;
import me.sargunvohra.mcmods.leveluphp.UtilKt;
import me.sargunvohra.mcmods.leveluphp.config.ClientConfig;
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    private static final Identifier TEX_ICONS = LevelUpHp.INSTANCE.id("textures/gui/icons.png");

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;

    @Shadow
    public abstract TextRenderer getFontRenderer();

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(int left, CallbackInfo ci) {
        if (!AutoConfig.getConfigHolder(ClientConfig.class).getConfig().getEnableXpBarOverride()) {
            return;
        }

        ClientPlayerEntity player = this.client.player;

        if (player == null) {
            return;
        }

        ci.cancel();

        this.client.getTextureManager().bindTexture(TEX_ICONS);

        HpLevelHandler levelHandler = UtilKt.getHpLevelHandler(player);

        this.client.getProfiler().push("levelUpHpBars");
        {
            int target = levelHandler.getCurrentXpTarget();
            int hpXpBarWidth = target != 0 ? levelHandler.getXp() * 91 / target : 0;
            int mcXpBarWidth = (int) (player.experienceProgress * 91);

            int top = this.scaledHeight - 32 + 3;

            this.renderProgress(left, top, 0, hpXpBarWidth);
            this.renderProgress(left + 91, top, 91, mcXpBarWidth);
        }
        this.client.getProfiler().pop();

        this.client.getProfiler().push("levelUpHpLevels");
        {
            String hpLevel = "" + levelHandler.getLevel();
            String mcLevel = "" + player.experienceLevel;

            int centerX = this.scaledWidth / 2;
            int hpLevelWidth = this.getFontRenderer().getStringWidth(hpLevel);

            renderLevel(hpLevel, centerX - 92 - hpLevelWidth, 0xff3f3f);
            renderLevel(mcLevel, centerX + 93, 0x80FF20);
        }
        this.client.getProfiler().pop();

        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
    }

    private void renderProgress(int left, int top, int texX, int filled) {
        this.blit(left, top, texX, 0, 91, 5);
        if (filled > 0) {
            this.blit(left, top, texX, 5, filled, 5);
        }
    }

    private void renderLevel(String str, int left, int color) {
        int top = this.scaledHeight - 30;
        this.getFontRenderer().draw(str, (float) (left + 1), (float) top, 0);
        this.getFontRenderer().draw(str, (float) (left - 1), (float) top, 0);
        this.getFontRenderer().draw(str, (float) left, (float) (top + 1), 0);
        this.getFontRenderer().draw(str, (float) left, (float) (top - 1), 0);
        this.getFontRenderer().draw(str, (float) left, (float) top, color);
    }
}

package me.sargunvohra.mcmods.leveluphp.mixin;

import me.sargunvohra.mcmods.leveluphp.advancement.LevelUpCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.Criterions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Criterions.class)
public abstract class CriterionsMixin {
    @Shadow
    private static <T extends Criterion<?>> T register(T criterion_1) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addLevelUpCriterion(CallbackInfo ci) {
        //noinspection ResultOfMethodCallIgnored
        register(LevelUpCriterion.INSTANCE);
    }
}

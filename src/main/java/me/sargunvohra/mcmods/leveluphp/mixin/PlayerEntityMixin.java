package me.sargunvohra.mcmods.leveluphp.mixin;

import me.sargunvohra.mcmods.leveluphp.UtilKt;
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler;
import me.sargunvohra.mcmods.leveluphp.level.HpLeveller;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements HpLeveller {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    private HpLevelHandler hpLevelHandler = new HpLevelHandler();

    @NotNull
    @Override
    public HpLevelHandler getHpLevelHandler() {
        return this.hpLevelHandler;
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        PlayerEntity p = (PlayerEntity) (Object) this;
        UtilKt.getHpLevelHandler(p).setPlayer(p);
    }
}

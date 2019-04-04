package me.sargunvohra.mcmods.leveluphp.mixin;

import me.sargunvohra.mcmods.leveluphp.ExtKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected int playerHitTimer;

    @Shadow protected PlayerEntity attackingPlayer;

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "drop", at = @At("HEAD"))
    private void onDrop(CallbackInfo ci) {
        PlayerEntity player = this.attackingPlayer;
        if (this.playerHitTimer > 0 && player instanceof ServerPlayerEntity) {
            ExtKt.getHpLevelHandler(player).applyKill(this);
        }
    }
}

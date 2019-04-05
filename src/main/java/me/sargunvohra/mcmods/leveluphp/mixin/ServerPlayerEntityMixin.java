package me.sargunvohra.mcmods.leveluphp.mixin;

import com.mojang.authlib.GameProfile;
import me.sargunvohra.mcmods.leveluphp.UtilKt;
import me.sargunvohra.mcmods.leveluphp.level.HpLevelHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private ServerPlayerEntityMixin(World world_1, GameProfile gameProfile_1) {
        super(world_1, gameProfile_1);
    }

    @Inject(method = "changeDimension", at = @At(value = "RETURN", ordinal = 1))
    private void onChangeDimension(CallbackInfoReturnable<Entity> ci) {
        HpLevelHandler handler = UtilKt.getHpLevelHandler(this);
        handler.onModified();
        // mark health as dirty
        this.setHealth(getHealth() - 1);
        this.setHealth(getHealth() + 1);
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void onCopyFrom(ServerPlayerEntity oldPlayer, boolean isLivingCopy, CallbackInfo ci) {
        HpLevelHandler newHandler = UtilKt.getHpLevelHandler(this);
        HpLevelHandler oldHandler = UtilKt.getHpLevelHandler(oldPlayer);

        newHandler.copyFrom(oldHandler);
        if (!isLivingCopy) {
            newHandler.applyDeathPenalty();
            setHealth(this.getHealthMaximum());
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
    private void onWriteCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        HpLevelHandler handler = UtilKt.getHpLevelHandler(this);
        tag.put("leveluphp", handler.writeToTag());
    }

    @Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
    private void onReadCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        HpLevelHandler handler = UtilKt.getHpLevelHandler(this);
        Tag data = tag.getTag("leveluphp");
        if (data != null) {
            handler.readFromTag(data);
        }
    }
}

package me.sargunvohra.mcmods.leveluphp.mixin;

import me.sargunvohra.mcmods.leveluphp.ExtKt;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void onRespawn(CallbackInfoReturnable<ServerPlayerEntity> cir) {
        ExtKt.getHpLevelHandler(cir.getReturnValue()).onModified();
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, CallbackInfo ci) {
        ExtKt.getHpLevelHandler(player).onModified();
    }

    @Inject(method = "onDataPacksReloaded", at = @At("RETURN"))
    private void onDataPacksReloaded(CallbackInfo ci) {
        this.players.forEach(player -> ExtKt.getHpLevelHandler(player).onModified());
    }
}

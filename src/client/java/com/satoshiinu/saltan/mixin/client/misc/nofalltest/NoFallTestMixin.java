package com.satoshiinu.saltan.mixin.client.misc.nofalltest;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class NoFallTestMixin extends NofallTestEntityMixin {
    @Unique
    private static final boolean enabled = false;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void sendMovementPacketsPrevent(CallbackInfo ci){
        if(!enabled)return;

        if(!isOnGround()) ci.cancel();
    }


}

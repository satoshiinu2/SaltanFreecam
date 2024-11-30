package com.satoshiinu.saltan.mixin.client.misc.nofalltest;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class NoFallTestMixin extends NofallTestEntityMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void sendMovementPacketsPrevent(CallbackInfo ci){
        if(true)return;

        if(!isOnGround()) ci.cancel();
    }


}

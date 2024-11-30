package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerInputMixin extends ClientPlayerInputShadowMixin {

    @Inject(method = "tickNewAi", at = @At("RETURN"))
    private void isCameraOverride(CallbackInfo ci){
        if(!SaltanFreecamClient.canMoveCamera())return;
        // only freecam enabled

        this.sidewaysSpeed = 0;
        this.forwardSpeed = 0;
        this.jumping = false;

        // prevent player move when camera move mode is enabled
    }


}

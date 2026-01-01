package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class PlayerViewableMixin {

    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
    private void isThredPersonOverride(CallbackInfoReturnable<Boolean> cir){
        if(SaltanClient.shouldViewAltCam())
            cir.setReturnValue(true);
        // only freecam enabled
    }



}

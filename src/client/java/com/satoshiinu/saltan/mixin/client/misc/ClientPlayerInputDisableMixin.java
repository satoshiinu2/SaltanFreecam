package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class ClientPlayerInputDisableMixin extends ClientPlayerInputShadowMixin {
    @Inject(method = "tick",at = @At("HEAD"), cancellable = true)
    private void isCameraOverride(CallbackInfo ci){
        if(SaltanFreecamClient.canMoveCamera()){
            this.playerInput = PlayerInput.DEFAULT;
            this.movementForward = 0;
            this.movementSideways = 0;
            ci.cancel();
        }

    }


}

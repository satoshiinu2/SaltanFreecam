package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class ClientPlayerInputDisableMixin extends ClientPlayerInputShadowMixin {
    @Inject(method = "tick",at = @At("HEAD"), cancellable = true)
    private void isCameraOverride(CallbackInfo ci){
        if(SaltanClient.shouldControlAltCam()){
            this.playerInput = PlayerInput.DEFAULT;
            this.movementVector = Vec2f.ZERO;
            ci.cancel();
        }

    }


}

package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class BobDisableMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void disableBob(MatrixStack matrices, float tickDelta, CallbackInfo ci){
        if(SaltanFreecamClient.freecamEnabled)
            ci.cancel();
        // only freecam enabled
    }
}

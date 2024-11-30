package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ClientPlayerTurnMixin {

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void preventChangeLookDir(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci){
        if(!SaltanFreecamClient.canMoveCamera()) return;
        // only freecam enabled

        SaltanFreecamClient.changeLookDirection(cursorDeltaX, cursorDeltaY);

        ci.cancel();
        // prevent default
    }
}

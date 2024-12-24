package com.satoshiinu.saltan.mixin.client.misc.nightvision;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class NightVisionMixin {
    @Inject(method = "getNightVisionStrength", at = @At("HEAD"), cancellable = true)
    private static void overrideNightVision(LivingEntity entity, float tickDelta, CallbackInfoReturnable<Float> cir){
//        if(SaltanFreecamClient.nightVisionEnabled)
            cir.setReturnValue(1.0f);

        // special future
    }
}

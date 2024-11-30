package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public abstract class SetCamTypeMixin {
    @Inject(method = "getPerspective", at = @At("HEAD"), cancellable = true)
    private void firstPersonoverride(CallbackInfoReturnable<Perspective> cir){
        if (SaltanFreecamClient.freecamEnabled)
            cir.setReturnValue(Perspective.FIRST_PERSON);
        // only freecam enabled


        // for render hand
    }
}

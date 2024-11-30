package com.satoshiinu.saltan.mixin.client;

import com.mojang.logging.LogUtils;
import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class TargetMixin {
    @Shadow public double prevX;
    @Shadow public double prevY;
    @Shadow public double prevZ;

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();


    @Inject(method = "getCameraPosVec", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if(!SaltanFreecamClient.canMoveCamera())return;
        // only freecam enabled

        cir.setReturnValue(SaltanFreecamClient.freecamPos);
    }

}

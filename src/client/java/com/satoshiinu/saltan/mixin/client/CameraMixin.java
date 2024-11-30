package com.satoshiinu.saltan.mixin.client;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void moveBy(float f, float g, float h);

    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("RETURN"), cancellable = true)
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if(!SaltanFreecamClient.freecamEnabled)return;
        // only freecam enabled

        Vec3d lerpedPos = SaltanFreecamClient.getLerpedPos(tickDelta);
        float lerpedYaw = SaltanFreecamClient.getLerpedYaw(tickDelta);
        float lerpedPitch = SaltanFreecamClient.getLerpedPitch(tickDelta);
        setPos(lerpedPos.x,lerpedPos.y,lerpedPos.z);
        setRotation(lerpedYaw,lerpedPitch);
    }


}

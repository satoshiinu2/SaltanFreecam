package com.satoshiinu.saltan.mixin.client;

import com.satoshiinu.saltan.SaltanClient;
import com.satoshiinu.saltan.imixin.client.ICameraGetter;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICameraGetter {
    private Vec3d originalPos = Vec3d.ZERO;
    private float originalYaw = 0;
    private float originalPitch = 0;


    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    public abstract Vec3d getCameraPos();

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract float getPitch();

    @Inject(method = "update", at = @At("RETURN"), cancellable = true)
    private void onUpdate(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        originalPos = getCameraPos();
        originalYaw = getYaw();
        originalPitch = getPitch();
        if(!SaltanClient.isFreecamEnabled())return;
        // only freecam enabled

        Vec3d lerpedPos = SaltanClient.getLerpedVisualPos(tickProgress);
        float lerpedYaw = SaltanClient.getLerpedVisualYaw(tickProgress);
        float lerpedPitch = SaltanClient.getLerpedVisualPitch(tickProgress);
        setPos(lerpedPos.x,lerpedPos.y,lerpedPos.z);
        setRotation(lerpedYaw,lerpedPitch);
    }


    public float saltan_freecam$getOriginalPitch() {
        return originalPitch;
    }
    public float saltan_freecam$getOriginalYaw() {
        return originalYaw;
    }
    public Vec3d saltan_freecam$getOriginalPos() {
        return originalPos;
    }

}

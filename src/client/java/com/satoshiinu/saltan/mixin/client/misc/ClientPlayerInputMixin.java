package com.satoshiinu.saltan.mixin.client.misc;

import com.satoshiinu.saltan.SaltanFreecamClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerInputMixin extends ClientPlayerInputShadowMixin {

    @Shadow @Final protected MinecraftClient client;

    @Shadow public float renderYaw;

    @Inject(method = "tickNewAi", at = @At("RETURN"))
    private void isCameraOverride(CallbackInfo ci){
        if(!SaltanFreecamClient.canMoveCamera())return;

        // below is called if only freecam is enabled
        this.sidewaysSpeed = 0;
        this.forwardSpeed = 0;
        this.jumping = false;

        // prevent player move when camera move mode is enabled
        // player can move only when freecam is disabled
    }


    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    private boolean canCreativeFly(ClientPlayerEntity instance) {
        boolean def = client.getCameraEntity() == (ClientPlayerEntity)(Object)this;
        if(!SaltanFreecamClient.canMoveCamera())return def;

        return false;
        // prevent creative fly
    }

//    @ModifyVariable(method = "tickMovement", at = @At("STORE"), ordinal = 3)
//    private boolean inSneakingPose(boolean def){
//        System.out.println(def);
//        return !SaltanFreecamClient.canMoveCamera() && def;
//    }


}

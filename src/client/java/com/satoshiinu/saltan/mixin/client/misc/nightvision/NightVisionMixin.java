package com.satoshiinu.saltan.mixin.client.misc.nightvision;

import com.satoshiinu.saltan.SaltanClient;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LightmapTextureManager.class)
public abstract class NightVisionMixin {
    @ModifyConstant(method = "update", constant = @Constant(floatValue = 0.0f ,ordinal = 1))
    private float overrideNightVision2(float x){
        // special future
        if(SaltanClient.isNightVisionEnabled())
            return 1.0F;
        return x;
    }
}

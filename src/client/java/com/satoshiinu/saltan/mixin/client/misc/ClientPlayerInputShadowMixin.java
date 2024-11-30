package com.satoshiinu.saltan.mixin.client.misc;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;



@Mixin(LivingEntity.class)
public abstract class ClientPlayerInputShadowMixin {

    @Shadow
    public float sidewaysSpeed;
    @Shadow
    public float upwardSpeed;
    @Shadow
    public float forwardSpeed;
    @Shadow
    protected boolean jumping;


}
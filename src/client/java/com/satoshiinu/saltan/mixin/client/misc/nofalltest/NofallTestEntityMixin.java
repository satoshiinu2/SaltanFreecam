package com.satoshiinu.saltan.mixin.client.misc.nofalltest;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class NofallTestEntityMixin {

    @Shadow
    public abstract boolean isOnGround();
}

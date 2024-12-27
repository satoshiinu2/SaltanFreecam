package com.satoshiinu.saltan.mixin.client.misc;

import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Input.class)
public abstract class ClientPlayerInputShadowMixin {
    @Shadow public PlayerInput playerInput;
    @Shadow public float movementForward;
    @Shadow public float movementSideways;


}

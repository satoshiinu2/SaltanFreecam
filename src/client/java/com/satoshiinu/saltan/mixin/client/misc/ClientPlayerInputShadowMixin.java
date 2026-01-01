package com.satoshiinu.saltan.mixin.client.misc;

import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Input.class)
public abstract class ClientPlayerInputShadowMixin {
    @Shadow public PlayerInput playerInput;
    @Shadow protected Vec2f movementVector;


}

package com.satoshiinu.saltan.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.satoshiinu.saltan.SaltanFreecamClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class CamPointRenderer {
    public void renderCamPos(MatrixStack matrices, VertexConsumerProvider consumers, Box box, Vec3d renderPos) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.push();
        matrices.translate(renderPos);

        VertexConsumer vertexConsumer;

        vertexConsumer = consumers.getBuffer(RenderLayer.getTranslucent());
//        VertexRendering.drawBox(matrices, vertexConsumer, box.minX,box.minY,box.minZ, box.maxX, box.maxY, box.maxZ, 0.9F, 0.1F, 0.1F, 0.25F);

        vertexConsumer = consumers.getBuffer(RenderLayer.getLines());
        VertexRendering.drawBox(matrices, vertexConsumer, box.minX,box.minY,box.minZ, box.maxX, box.maxY, box.maxZ, 0.9F, 0.1F, 0.1F, 1.0F);

        matrices.pop();
    }
    public static final int maxLight = LightmapTextureManager.pack(15,15);//15728880


}

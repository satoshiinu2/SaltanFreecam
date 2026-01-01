package com.satoshiinu.saltan.render;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import java.awt.*;

public class CamPointRenderer {
    public void renderCamPos(MatrixStack matrices, OrderedRenderCommandQueue cmdQueue, Box box, Vec3d renderPos, Color color) {
        matrices.push();
        matrices.translate(renderPos);
        GizmoDrawing.box(box,DrawStyle.stroked(ColorHelper.fromFloats(1.0F, color.getRed(),color.getGreen(),color.getBlue())));
        matrices.pop();
    }


}

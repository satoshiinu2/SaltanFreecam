package com.satoshiinu.saltan.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;

public class CamPointModel extends Model {
    private static final String CUBE_ROOT = "cubeRoot";
    private final ModelPart cubeRoot;

    public CamPointModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
        cubeRoot = root.getChild(CUBE_ROOT);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(CUBE_ROOT, ModelPartBuilder.create().uv(0, 0).cuboid(-8,0,-8,8,16,8), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }
}

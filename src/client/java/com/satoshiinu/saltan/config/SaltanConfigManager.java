package com.satoshiinu.saltan.config;

import   com.satoshiinu.saltan.SaltanClient.CamHudRenderType;
import com.satoshiinu.satosconfig.ConfigManager;
import com.satoshiinu.satosconfig.configType.*;
import net.minecraft.text.Text;

public class SaltanConfigManager extends ConfigManager {

    @Override
    public Text getOptionTitle() {
        return Text.translatable("options.saltan.title");
    }

    @Override
    public String getOptionPrefix() {
        return "options.saltan.";
    }

    @Override
    public String getConfigFileName() {
        return "saltan-freecam";
    }

    Text categoryGeneral = Text.translatable("options.category.saltan.general");
    Text categoryRender = Text.translatable("options.category.saltan.render");
    Text categoryHud = Text.translatable("options.category.saltan.hud");
    Text categoryOther = Text.translatable("options.category.saltan.other");
    Text categoryNightVision = Text.translatable("options.category.saltan.nightvision");
    public  ConfigFloatField freecamMoveSpeed = register(new ConfigFloatField(this,"freecam_move_speed",categoryGeneral,0.1f));
    public  ConfigFloatField freecamSprintSpeedMultiplier = register(new ConfigFloatField(this,"freecam_move_speed_multiplier",categoryGeneral,4.0f));

    public  ConfigBooleanToggle freecamPointEnabled = register(new ConfigBooleanToggle(this,"freecam_point_enabled", categoryRender,true));
    public  ConfigColor freecamPointColor = register(new ConfigColor(this,"freecam_point_color",categoryRender, new java.awt.Color(0.9F, 0.1F, 0.1F,0)));

//    public  ConfigBooleanToggle freecamPosHudEnabled = register(new ConfigBooleanToggle(this,"freecam_pos_hud_enabled", categoryHud,true));
    public  ConfigEnum<CamHudRenderType> freecamPosHudType = register(new ConfigEnum<>(this,"freecam_pos_hud_render_type", categoryHud,CamHudRenderType.WHEN_ENABLED,CamHudRenderType.class));
    public  ConfigBooleanToggleCustomText freecamPosHudTop = register(new ConfigBooleanToggleCustomText(this,"freecam_pos_hud_top", categoryHud,true, Text.translatable("options.saltan.vertical.top"),Text.translatable("options.saltan.vertical.bottom")));
    public  ConfigBooleanToggleCustomText freecamPosHudLeft = register(new ConfigBooleanToggleCustomText(this,"freecam_pos_hud_left", categoryHud,true,Text.translatable("options.saltan.horizontal.left"),Text.translatable("options.saltan.horizontal.right")));
    public  ConfigIntSlider freecamPosHudOffsetX = register(new ConfigIntSlider(this,"freecam_pos_hud_offset_x", categoryHud,0,-400,400));
    public  ConfigIntSlider freecamPosHudOffsetY = register(new ConfigIntSlider(this,"freecam_pos_hud_offset_y", categoryHud,0,-400,400));

    public  ConfigBooleanToggle freecamClampYaw = register(new ConfigBooleanToggle(this,"freecam_clamp_yaw",categoryGeneral,true));
    public  ConfigBooleanToggle freecamSwapLastSession = register(new ConfigBooleanToggle(this,"freecam_swap_last_session",categoryGeneral,true));

    public  ConfigBooleanToggle nightVisionEnabled = register(new ConfigBooleanToggle(this,"nightVisionEnabled",categoryNightVision,false));

}

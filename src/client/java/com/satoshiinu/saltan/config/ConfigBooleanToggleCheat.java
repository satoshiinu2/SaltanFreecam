package com.satoshiinu.saltan.config;

import com.satoshiinu.satosconfig.ConfigManager;
import com.satoshiinu.satosconfig.configType.ConfigBooleanToggle;
import net.minecraft.text.Text;

public class ConfigBooleanToggleCheat extends ConfigBooleanToggle implements  ICheatConfig{
    public ConfigBooleanToggleCheat(ConfigManager configManager,String saveKey, String translateKey, Text category, boolean defaultValue) {
        super(configManager, saveKey, translateKey,category, defaultValue);
    }
    public ConfigBooleanToggleCheat(ConfigManager configManager, String saveKey, Text category, boolean defaultValue) {
        this(configManager, saveKey,getTranslateKeyBySaveKey(configManager,saveKey),category,defaultValue);
    }

    @Override
    public boolean isEnabled() {
        return getValue();
    }
}

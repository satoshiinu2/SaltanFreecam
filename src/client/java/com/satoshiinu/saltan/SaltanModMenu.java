package com.satoshiinu.saltan;

import com.satoshiinu.satosconfig.ConfigManager;
import com.satoshiinu.satosconfig.ConfigModMenuBase;

public class SaltanModMenu extends ConfigModMenuBase {

    protected ConfigManager getConfigManager() {
        return SaltanClient.configManager;
    }
}

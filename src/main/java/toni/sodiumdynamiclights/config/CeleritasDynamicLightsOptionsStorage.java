package toni.sodiumdynamiclights.config;

import org.taumc.celeritas.api.options.structure.OptionStorage;
import net.minecraftforge.common.config.ConfigManager;

public class CeleritasDynamicLightsOptionsStorage implements OptionStorage<DynamicLightsConfig> {

    @Override
    public void save() {
        ConfigManager.sync("celeritasdynamiclights", net.minecraftforge.common.config.Config.Type.INSTANCE);
    }

    @Override
    public DynamicLightsConfig getData() {
        return null;
    }
}

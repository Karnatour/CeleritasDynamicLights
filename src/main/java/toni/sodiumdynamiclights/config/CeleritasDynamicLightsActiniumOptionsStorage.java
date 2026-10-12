package toni.sodiumdynamiclights.config;


import net.minecraftforge.common.config.ConfigManager;
import dhj.embeddedt.embeddium.api.options.structure.OptionStorage;

public class CeleritasDynamicLightsActiniumOptionsStorage implements OptionStorage<DynamicLightsConfig> {

    @Override
    public void save() {
        ConfigManager.sync("celeritasdynamiclights", net.minecraftforge.common.config.Config.Type.INSTANCE);
    }

    @Override
    public DynamicLightsConfig getData() {
        return null;
    }
}

package toni.sodiumdynamiclights.config;

import com.google.common.collect.ImmutableList;
import org.embeddedt.embeddium.impl.gui.framework.TextComponent;
import org.taumc.celeritas.api.options.OptionIdentifier;
import org.taumc.celeritas.api.options.control.CyclingControl;
import org.taumc.celeritas.api.options.control.TickBoxControl;
import org.taumc.celeritas.api.options.structure.*;
import org.taumc.celeritas.impl.gui.MinecraftOptionsStorage;
import toni.sodiumdynamiclights.DynamicLightsMode;
import toni.sodiumdynamiclights.ExplosiveLightingMode;

import java.util.ArrayList;
import java.util.List;

public class DynamicLightsCeleritasPage {

    private static final MinecraftOptionsStorage optionsStorage = new MinecraftOptionsStorage();

    public static OptionPage celeritasDynamicLights() {
        final List<OptionGroup> groups = new ArrayList<>();

        final String MOD_NAME = "CeleritasDynamicLights";

        groups.add(OptionGroup.createBuilder()
                .setId(OptionIdentifier.create(MOD_NAME, "common"))
                .add(OptionImpl.createBuilder(DynamicLightsMode.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "mode", DynamicLightsMode.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.mode"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.mode.desc"))
                        .setControl(option -> new CyclingControl<>(option, DynamicLightsMode.class, new TextComponent[]{

                                TextComponent.translatable(DynamicLightsMode.OFF.getTranslationComponent().getKey()),
                                TextComponent.translatable(DynamicLightsMode.SLOW.getTranslationComponent().getKey()),
                                TextComponent.translatable(DynamicLightsMode.FAST.getTranslationComponent().getKey()),
                                TextComponent.translatable(DynamicLightsMode.REALTIME.getTranslationComponent().getKey())
                        }))
                        .setBinding((options, value) -> DynamicLightsConfig.dynamicLightsMode = value,
                                (options) -> DynamicLightsConfig.dynamicLightsMode)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "self", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.self"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.self.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.selfLightSource = value,
                                (options) -> DynamicLightsConfig.selfLightSource)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "entities", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.entities"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.entities.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.entitiesLightSource = value,
                                (options) -> DynamicLightsConfig.entitiesLightSource)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "blockentities", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.blockentities"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.blockentities.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.blockEntitiesLightSource = value,
                                (options) -> DynamicLightsConfig.blockEntitiesLightSource)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "underwater", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.underwater"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.underwater.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.waterSensitiveCheck = value,
                                (options) -> DynamicLightsConfig.waterSensitiveCheck)
                        .build())
                .add(OptionImpl.createBuilder(ExplosiveLightingMode.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "tnt", ExplosiveLightingMode.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.tnt"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.tnt.desc"))
                        .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class, new TextComponent[]{
                                TextComponent.translatable(ExplosiveLightingMode.OFF.getTranslationComponent().getKey()),
                                TextComponent.translatable(ExplosiveLightingMode.SIMPLE.getTranslationComponent().getKey()),
                                TextComponent.translatable(ExplosiveLightingMode.FANCY.getTranslationComponent().getKey()),
                        }))
                        .setBinding((options, value) -> DynamicLightsConfig.tntLightingMode = (value),
                                (options) -> DynamicLightsConfig.tntLightingMode)
                        .build())
                .add(OptionImpl.createBuilder(ExplosiveLightingMode.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_NAME, "creeper", ExplosiveLightingMode.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.creeper"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.creeper.desc"))
                        .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class, new TextComponent[]{
                                TextComponent.translatable(ExplosiveLightingMode.OFF.getTranslationComponent().getKey()),
                                TextComponent.translatable(ExplosiveLightingMode.SIMPLE.getTranslationComponent().getKey()),
                                TextComponent.translatable(ExplosiveLightingMode.FANCY.getTranslationComponent().getKey()),
                        }))
                        .setBinding((options, value) -> DynamicLightsConfig.creeperLightingMode = (value),
                                (options) -> DynamicLightsConfig.creeperLightingMode)
                        .build())
                .build());

        OptionIdentifier<Void> pageId = OptionIdentifier.create(MOD_NAME, "page");
        TextComponent pageName = TextComponent.translatable("sodium.dynamiclights.options.page");

        return new OptionPage(pageId, pageName, ImmutableList.copyOf(groups));
    }
}
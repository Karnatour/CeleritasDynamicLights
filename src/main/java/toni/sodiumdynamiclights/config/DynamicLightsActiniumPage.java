package toni.sodiumdynamiclights.config;

import com.google.common.collect.ImmutableList;
import dhj.embeddedt.embeddium.impl.gui.framework.TextComponent;
import dhj.embeddedt.embeddium.api.options.OptionIdentifier;
import dhj.embeddedt.embeddium.api.options.control.CyclingControl;
import dhj.embeddedt.embeddium.api.options.control.TickBoxControl;
import dhj.embeddedt.embeddium.api.options.structure.OptionFlag;
import dhj.embeddedt.embeddium.api.options.structure.OptionGroup;
import dhj.embeddedt.embeddium.api.options.structure.OptionImpl;
import dhj.embeddedt.embeddium.api.options.structure.OptionPage;
import dhj.embeddedt.embeddium.impl.gui.framework.TextFormattingStyle;
import toni.sodiumdynamiclights.DynamicLightsMode;
import toni.sodiumdynamiclights.ExplosiveLightingMode;

import java.util.ArrayList;
import java.util.List;

public class DynamicLightsActiniumPage {
    private static final CeleritasDynamicLightsActiniumOptionsStorage optionsStorage = new CeleritasDynamicLightsActiniumOptionsStorage();

    private static TextComponent name(ExplosiveLightingMode mode) {
        return TextComponent.translatable(mode.getTranslationKey())
                .withStyle(TextFormattingStyle.valueOf(mode.getFormatting().name()));
    }

    public static OptionPage celeritasDynamicLights() {
        final List<OptionGroup> groups = new ArrayList<>();

        final String MOD_ID = "celeritasdynamiclights";

        groups.add(OptionGroup.createBuilder()
                .setId(OptionIdentifier.create(MOD_ID, "common"))
                .add(OptionImpl.createBuilder(DynamicLightsMode.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "mode", DynamicLightsMode.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.mode"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.mode.desc"))
                        .setControl(option -> new CyclingControl<>(option, DynamicLightsMode.class))
                        .setBinding((options, value) -> DynamicLightsConfig.dynamicLightsMode = value,
                                (options) -> DynamicLightsConfig.dynamicLightsMode)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "self", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.self"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.self.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.selfLightSource = value,
                                (options) -> DynamicLightsConfig.selfLightSource)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "entities", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.entities"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.entities.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.entitiesLightSource = value,
                                (options) -> DynamicLightsConfig.entitiesLightSource)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "blockentities", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.blockentities"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.blockentities.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.blockEntitiesLightSource = value,
                                (options) -> DynamicLightsConfig.blockEntitiesLightSource)
                        .build())
                .add(OptionImpl.createBuilder(Boolean.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "underwater", Boolean.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.underwater"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.underwater.desc"))
                        .setControl(TickBoxControl::new)
                        .setBinding((options, value) -> DynamicLightsConfig.waterSensitiveCheck = value,
                                (options) -> DynamicLightsConfig.waterSensitiveCheck)
                        .build())
                .add(OptionImpl.createBuilder(ExplosiveLightingMode.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "tnt", ExplosiveLightingMode.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.tnt"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.tnt.desc",
                                name(ExplosiveLightingMode.OFF),
                                name(ExplosiveLightingMode.SIMPLE),
                                name(ExplosiveLightingMode.FANCY)))
                        .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class))
                        .setBinding((options, value) -> DynamicLightsConfig.tntLightingMode = (value),
                                (options) -> DynamicLightsConfig.tntLightingMode)
                        .build())
                .add(OptionImpl.createBuilder(ExplosiveLightingMode.class, optionsStorage)
                        .setId(OptionIdentifier.create(MOD_ID, "creeper", ExplosiveLightingMode.class))
                        .setName(TextComponent.translatable("sodium.dynamiclights.options.creeper"))
                        .setTooltip(TextComponent.translatable("sodium.dynamiclights.options.creeper.desc",
                                name(ExplosiveLightingMode.OFF),
                                name(ExplosiveLightingMode.SIMPLE),
                                name(ExplosiveLightingMode.FANCY)))
                        .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class))
                        .setBinding((options, value) -> DynamicLightsConfig.creeperLightingMode = (value),
                                (options) -> DynamicLightsConfig.creeperLightingMode)
                        .build())
                .build());

        OptionIdentifier<Void> pageId = OptionIdentifier.create(MOD_ID, "page");
        TextComponent pageName = TextComponent.translatable("sodium.dynamiclights.options.page");

        return new OptionPage(pageId, pageName, ImmutableList.copyOf(groups));
    }
}

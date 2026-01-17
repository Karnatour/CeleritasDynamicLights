/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.config;

import net.minecraftforge.common.config.Config;
import toni.sodiumdynamiclights.DynamicLightsMode;
import toni.sodiumdynamiclights.ExplosiveLightingMode;

import java.util.HashMap;

@Config(modid = "celeritasdynamiclights")
public class DynamicLightsConfig {
    public static DynamicLightsMode dynamicLightsMode = DynamicLightsMode.REALTIME;

    public static boolean entitiesLightSource = true;
    public static boolean selfLightSource = true;
    public static boolean blockEntitiesLightSource = true;
    public static boolean waterSensitiveCheck = true;

    public static ExplosiveLightingMode creeperLightingMode = ExplosiveLightingMode.FANCY;
    public static ExplosiveLightingMode tntLightingMode = ExplosiveLightingMode.FANCY;

    public static HashMap<String, Boolean> ENTITIES_SETTINGS = new HashMap<>();
}

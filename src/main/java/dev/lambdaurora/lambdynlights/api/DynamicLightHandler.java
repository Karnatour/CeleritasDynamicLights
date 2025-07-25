/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static toni.sodiumdynamiclights.SodiumDynamicLights.isEyeSubmergedInFluid;

/**
 * Represents a dynamic light handler.
 *
 * @param <T> The type of the light source.
 * @author LambdAurora
 * @version 1.3.0
 * @since 1.1.0
 */
public interface DynamicLightHandler<T> {
    /**
     * Returns the luminance of the light source.
     *
     * @param lightSource The light source.
     * @return The luminance.
     */
    int getLuminance(T lightSource);

    /**
     * Returns whether the light source is water-sensitive or not.
     *
     * @param lightSource The light source.
     * @return True if the light source is water-sensitive, else false.
     */
    default boolean isWaterSensitive(T lightSource) {
        return false;
    }

    /**
     * Returns a dynamic light handler.
     *
     * @param luminance      The luminance function.
     * @param waterSensitive The water sensitive function.
     * @param <T>            The type of the entity.
     * @return The completed handler.
     */
    static <T extends EntityLiving> @NotNull DynamicLightHandler<T> makeHandler(
            Function<T, Integer> luminance, Function<T, Boolean> waterSensitive
    ) {
        return new DynamicLightHandler<>() {
            @Override
            public int getLuminance(T lightSource) {
                return luminance.apply(lightSource);
            }

            @Override
            public boolean isWaterSensitive(T lightSource) {
                return waterSensitive.apply(lightSource);
            }
        };
    }

    /**
     * Returns a living entity dynamic light handler.
     *
     * @param handler The handler.
     * @param <T>     The type of the entity.
     * @return The completed handler.
     */
    static <T extends EntityLiving> @NotNull DynamicLightHandler<T> makeLivingEntityHandler(@NotNull DynamicLightHandler<T> handler) {
        return entity -> {
            boolean submergedInFluid = isEyeSubmergedInFluid(entity);
            int luminance = 0;

            for (ItemStack equipped : entity.getHeldEquipment()) {
                luminance = Math.max(luminance, SodiumDynamicLights.getLuminanceFromItemStack(equipped, submergedInFluid));
            }

            for (ItemStack armor : entity.getArmorInventoryList()) {
                luminance = Math.max(luminance, SodiumDynamicLights.getLuminanceFromItemStack(armor, submergedInFluid));
            }

            luminance = Math.max(luminance, handler.getLuminance(entity));

            return luminance;
        };
    }

    /**
     * Returns a Creeper dynamic light handler.
     *
     * @param handler Extra handler.
     * @param <T>     The type of Creeper entity.
     * @return The completed handler.
     */
    static <T extends EntityCreeper> @NotNull DynamicLightHandler<T> makeCreeperEntityHandler(@Nullable DynamicLightHandler<T> handler) {
        return new DynamicLightHandler<>() {
            @Override
            public int getLuminance(T entity) {
                int luminance = 0;

                if (entity.getCreeperFlashIntensity(0.f) > 0.001) {
                    luminance = switch (DynamicLightsConfig.creeperLightingMode) {
                        case OFF -> 0;
                        case SIMPLE -> 10;
                        case FANCY -> (int) (entity.getCreeperFlashIntensity(0.f) * 10.0);
                    };
                }

                if (handler != null) {
                    luminance = Math.max(luminance, handler.getLuminance(entity));
                }

                return luminance;
            }

            @Override
            public boolean isWaterSensitive(T lightSource) {
                return true;
            }
        };
    }
}

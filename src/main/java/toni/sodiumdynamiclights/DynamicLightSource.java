/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;

/**
 * Represents a dynamic light source.
 *
 * @author LambdAurora
 * @version 3.0.0
 * @since 1.0.0
 */
public interface DynamicLightSource {
	/**
	 * Returns the dynamic light source X coordinate.
	 *
	 * @return the X coordinate
	 */
	double sdl$getDynamicLightX();

	/**
	 * Returns the dynamic light source Y coordinate.
	 *
	 * @return the Y coordinate
	 */
	double sdl$getDynamicLightY();

	/**
	 * Returns the dynamic light source Z coordinate.
	 *
	 * @return the Z coordinate
	 */
	double sdl$getDynamicLightZ();

	/**
	 * Returns the dynamic light source world.
	 *
	 * @return the world instance
	 */
	World sdl$getDynamicLightLevel();

	/**
	 * Returns whether the dynamic light is enabled or not.
	 *
	 * @return {@code true} if the dynamic light is enabled, else {@code false}
	 */
	default boolean sdl$isDynamicLightEnabled() {
		return DynamicLightsConfig.dynamicLightsMode.isEnabled() && SodiumDynamicLights.get().containsLightSource(this);
	}

	/**
	 * Sets whether the dynamic light is enabled or not.
	 * <p>
	 * Note: please do not call this function in your mod or you will break things.
	 *
	 * @param enabled {@code true} if the dynamic light is enabled, else {@code false}
	 */
	@ApiStatus.Internal
	default void sdl$setDynamicLightEnabled(boolean enabled) {
		this.sdl$resetDynamicLight();
		if (enabled)
			SodiumDynamicLights.get().addLightSource(this);
		else
			SodiumDynamicLights.get().removeLightSource(this);
	}

	void sdl$resetDynamicLight();

	/**
	 * Returns the luminance of the light source.
	 * The maximum is 15, below 1 values are ignored.
	 *
	 * @return the luminance of the light source
	 */
	int sdl$getLuminance();

	/**
	 * Executed at each tick.
	 */
	void sdl$dynamicLightTick();

	/**
	 * Returns whether this dynamic light source should update.
	 *
	 * @return {@code true} if this dynamic light source should update, else {@code false}
	 */
	boolean sdl$shouldUpdateDynamicLight();

	boolean sodiumdynamiclights$updateDynamicLight(@NotNull RenderGlobal renderer);

	void sodiumdynamiclights$scheduleTrackedChunksRebuild(@NotNull RenderGlobal renderer);
}

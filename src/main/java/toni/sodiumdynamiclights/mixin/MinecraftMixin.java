/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.multiplayer.WorldClient;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to MinecraftClient.
 * <p>
 * Goal: clear light sources cache when changing world.
 *
 * @author LambdAurora
 * @version 1.3.2
 * @since 1.3.2
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
	private void onLoadWorld(WorldClient world, String loadingMessage, CallbackInfo ci) {
		SodiumDynamicLights.get().clearLightSources();
	}
}

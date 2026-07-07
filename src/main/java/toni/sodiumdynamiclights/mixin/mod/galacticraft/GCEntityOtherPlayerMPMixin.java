package toni.sodiumdynamiclights.mixin.mod.galacticraft;

import micdoodle8.mods.galacticraft.core.entities.player.GCEntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@Mixin(GCEntityOtherPlayerMP.class)
public class GCEntityOtherPlayerMPMixin {

    @ModifyArgs(method = "getBrightnessForRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCombinedLight(Lnet/minecraft/util/math/BlockPos;I)I"))
    private void onGetBrightnessForRender(Args args) {
        BlockPos pos = args.get(0);
        int vanillaLight = args.get(1);

        int dynamicLight = (int) SodiumDynamicLights.get().getDynamicLightLevel(pos);

        int finalLight = Math.max(vanillaLight, dynamicLight);

        args.set(1, finalLight);
    }
}

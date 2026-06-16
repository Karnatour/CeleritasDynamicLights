package toni.sodiumdynamiclights.mixin;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@SideOnly(Side.CLIENT)
@Mixin(BlockStateContainer.StateImplementation.class)
public class StateImplementationMixin {

    @Inject(method = "getPackedLightmapCoords", at = @At("RETURN"), cancellable = true)
    private void onGetPackedLightmapCoords(IBlockAccess source, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        IBlockState state = source.getBlockState(pos);

        // isOpaqueCube check to prevent AO issues
        if (DynamicLightsConfig.dynamicLightsMode.isEnabled() && (!state.isOpaqueCube() || state.getLightValue(source, pos) > 0)) {
            cir.setReturnValue(SodiumDynamicLights.get().getLightmapWithDynamicLight(pos, cir.getReturnValue()));
        }
    }
}

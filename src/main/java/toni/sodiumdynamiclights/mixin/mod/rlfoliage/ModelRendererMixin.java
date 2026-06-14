package toni.sodiumdynamiclights.mixin.mod.rlfoliage;

import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin {

    @Shadow
    @Final
    public BlockContext BLOCK_CONTEXT;

    @ModifyArgs(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/BufferBuilder;lightmap(II)Lnet/minecraft/client/renderer/BufferBuilder;"
            )
    )
    private void modifyLightmap(Args args) {
        int blockLight = args.get(1);
        BlockPos pos = this.BLOCK_CONTEXT.getPos();

        int dynamicBlockLight = ((int) SodiumDynamicLights.get().getDynamicLightLevel(pos)) << 4;

        int finalBlockLight = Math.max(blockLight, dynamicBlockLight);

        args.set(1, finalBlockLight);
    }
}

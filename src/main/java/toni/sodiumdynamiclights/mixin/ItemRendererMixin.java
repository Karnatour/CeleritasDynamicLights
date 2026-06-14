package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import toni.sodiumdynamiclights.SodiumDynamicLights;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow
    private ItemStack itemStackMainHand;

    @Shadow
    private ItemStack itemStackOffHand;

    @Shadow
    @Final
    private Minecraft mc;

    @ModifyArg(method = "setLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getCombinedLight(Lnet/minecraft/util/math/BlockPos;I)I"), index = 1)
    private int modifyLightValueArg(int par2) {
        int mainHand = SodiumDynamicLights.getLuminanceFromItemStack(itemStackMainHand, SodiumDynamicLights.isEyeSubmergedInFluid(this.mc.player));
        int secondHand = SodiumDynamicLights.getLuminanceFromItemStack(itemStackOffHand, SodiumDynamicLights.isEyeSubmergedInFluid(this.mc.player));

        AbstractClientPlayer player = this.mc.player;
        BlockPos pos = new BlockPos(
                player.posX,
                player.posY + player.getEyeHeight(),
                player.posZ
        );

        int dynamicLight = (int) SodiumDynamicLights.get().getDynamicLightLevel(pos);

        return Math.max(Math.max(mainHand, secondHand), dynamicLight);
    }
}


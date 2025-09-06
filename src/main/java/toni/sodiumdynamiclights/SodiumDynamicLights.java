/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSources;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.logging.log4j.Logger;
import org.taumc.celeritas.api.OptionGUIConstructionEvent;
import toni.sodiumdynamiclights.accessor.WorldRendererAccessor;
import toni.sodiumdynamiclights.config.CeleritasOptionsListener;
import toni.sodiumdynamiclights.config.ConfigEventHandler;
import toni.sodiumdynamiclights.config.DynamicLightsConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * Represents the SodiumDynamicLights mod.
 *
 * @author LambdAurora
 * @version 2.3.2
 * @since 1.0.0
 */

@Mod(modid = "celeritasdynamiclights", name = "Celeritas Dynamic Lights", version = "1.2.4", clientSideOnly = true, acceptableRemoteVersions = "*")
public class SodiumDynamicLights {
    public static final Logger LOGGER = LogManager.getLogger("Celeritas Dynamic Lights");
    @Mod.Instance
    public static SodiumDynamicLights INSTANCE;

    private static final double MAX_RADIUS = 7.75;
    private static final double MAX_RADIUS_SQUARED = MAX_RADIUS * MAX_RADIUS;
    private final Set<DynamicLightSource> dynamicLightSources = new HashSet<>();
    private final ReentrantReadWriteLock lightSourcesLock = new ReentrantReadWriteLock();
    private long lastUpdate = System.currentTimeMillis();
    private int lastUpdateCount = 0;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        if (Loader.isModLoaded("celeritas")) {
            try {
                Class.forName("org.taumc.celeritas.api.OptionGUIConstructionEvent");
                MinecraftForge.EVENT_BUS.register(CeleritasOptionsListener.class);
            } catch (Throwable t) {
                if (t instanceof ClassNotFoundException) {
                    LOGGER.error("Celeritas is not up-to-date, cannot insert options into Celeritas' video options menu.");
                } else {
                    LOGGER.error("Unable to check if Celeritas is up-to-date.", t);
                }
            }

        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
            IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
            reloadableResourceManager.registerReloadListener(ItemLightSources::load);
            if (Loader.isModLoaded("celeritas")) {
                try {
                    OptionGUIConstructionEvent.BUS.addListener(CeleritasOptionsListener::onCeleritasOptionsConstruct);
                    System.out.println("[Celeritas Dynamic Lights] Successfully initialized Celeritas compatibility.");
                } catch (Throwable t) {
                    System.err.println("[Celeritas Dynamic Lights] Failed to initialize Celeritas compatibility.");
                    t.printStackTrace();
                }
            } else {
                System.out.println("[Celeritas Dynamic Lights] Celeritas not found, skipping compatibility features.");
            }
        }
    }

    public void onInitializeClient() {
        DynamicLightHandlers.registerDefaultHandlers();
        LOGGER.info("Initializing SodiumDynamicLights...");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.onInitializeClient();
        }
    }

    /**
     * Updates all light sources.
     *
     * @param renderer the renderer
     */
    public void updateAll(RenderGlobal renderer) {
        if (!DynamicLightsConfig.dynamicLightsMode.isEnabled())
            return;

        long now = System.currentTimeMillis();
        if (now >= this.lastUpdate + 50) {
            this.lastUpdate = now;
            this.lastUpdateCount = 0;

            this.lightSourcesLock.readLock().lock();
            for (var lightSource : this.dynamicLightSources) {
                if (lightSource.sodiumdynamiclights$updateDynamicLight(renderer)) this.lastUpdateCount++;
            }
            this.lightSourcesLock.readLock().unlock();
        }
    }

    /**
     * Returns the last number of dynamic light source updates.
     *
     * @return the last number of dynamic light source updates
     */
    public int getLastUpdateCount() {
        return this.lastUpdateCount;
    }

    /**
     * Returns the lightmap with combined light levels.
     *
     * @param pos      the position
     * @param lightmap the vanilla lightmap coordinates
     * @return the modified lightmap coordinates
     */
    public int getLightmapWithDynamicLight(@NotNull BlockPos pos, int lightmap) {
        return this.getLightmapWithDynamicLight(this.getDynamicLightLevel(pos), lightmap);
    }

    /**
     * Returns the lightmap with combined light levels.
     *
     * @param entity   the entity
     * @param lightmap the vanilla lightmap coordinates
     * @return the modified lightmap coordinates
     */
    public int getLightmapWithDynamicLight(Entity entity, int lightmap) {
        int posLightlevel = (int) this.getDynamicLightLevel(new BlockPos(entity.posX,entity.posY,entity.posZ));
        int entityLuminance = ((DynamicLightSource) entity).sdl$getLuminance();

        return getLightmapWithDynamicLight(Math.max(posLightlevel, entityLuminance), lightmap);
    }

    public int getLightmapWithDynamicLight(double dynamicLightLevel, int lightmap) {
        if (dynamicLightLevel > 0) {
            int blockLight = (lightmap >> 4) & 0xF;
            int skyLight = (lightmap >> 20) & 0xF;

            if (dynamicLightLevel > blockLight) {
                blockLight = (int) dynamicLightLevel;
            }

            return (skyLight << 20) | (blockLight << 4);
        }

        return lightmap;
    }

    /**
     * Returns the dynamic light level at the specified position.
     *
     * @param pos the position
     * @return the dynamic light level at the specified position
     */
    public double getDynamicLightLevel(@NotNull BlockPos pos) {
        double result = 0;
        this.lightSourcesLock.readLock().lock();
        for (var lightSource : this.dynamicLightSources) {
            result = maxDynamicLightLevel(pos, lightSource, result);
        }
        this.lightSourcesLock.readLock().unlock();

        return Math.clamp(result, 0, 15);
    }

    /**
     * Returns the dynamic light level generated by the light source at the specified position.
     *
     * @param pos               the position
     * @param lightSource       the light source
     * @param currentLightLevel the current surrounding dynamic light level
     * @return the dynamic light level at the specified position
     */
    public static double maxDynamicLightLevel(@NotNull BlockPos pos, @NotNull DynamicLightSource lightSource, double currentLightLevel) {
        int luminance = lightSource.sdl$getLuminance();
        if (luminance > 0) {
            // Can't use Entity#squaredDistanceTo because of eye Y coordinate.
            double dx = (pos.getX() + 0.5) - lightSource.sdl$getDynamicLightX();
            double dy = (pos.getY() + 0.5) - lightSource.sdl$getDynamicLightY();
            double dz = (pos.getZ() + 0.5) - lightSource.sdl$getDynamicLightZ();

            double distanceSquared = dx * dx + dy * dy + dz * dz;
            // 7.75 because else we would have to update more chunks and that's not a good idea.
            // 15 (max range for blocks) would be too much and a bit cheaty.
            if (distanceSquared <= MAX_RADIUS_SQUARED) {
                double multiplier = 1.0 - Math.sqrt(distanceSquared) / MAX_RADIUS;
                double lightLevel = multiplier * (double) luminance;
                if (lightLevel > currentLightLevel) {
                    return lightLevel;
                }
            }
        }
        return currentLightLevel;
    }

    /**
     * Adds the light source to the tracked light sources.
     *
     * @param lightSource the light source to add
     */
    public void addLightSource(@NotNull DynamicLightSource lightSource) {
        if (!lightSource.sdl$getDynamicLightLevel().isRemote)
            return;
        if (!DynamicLightsConfig.dynamicLightsMode.isEnabled())
            return;
        if (this.containsLightSource(lightSource))
            return;
        this.lightSourcesLock.writeLock().lock();
        this.dynamicLightSources.add(lightSource);
        this.lightSourcesLock.writeLock().unlock();
    }

    /**
     * Returns whether the light source is tracked or not.
     *
     * @param lightSource the light source to check
     * @return {@code true} if the light source is tracked, else {@code false}
     */
    public boolean containsLightSource(@NotNull DynamicLightSource lightSource) {
        if (!lightSource.sdl$getDynamicLightLevel().isRemote)
            return false;

        boolean result;
        this.lightSourcesLock.readLock().lock();
        result = this.dynamicLightSources.contains(lightSource);
        this.lightSourcesLock.readLock().unlock();
        return result;
    }

    /**
     * Returns the number of dynamic light sources that currently emit lights.
     *
     * @return the number of dynamic light sources emitting light
     */
    public int getLightSourcesCount() {
        int result;

        this.lightSourcesLock.readLock().lock();
        result = this.dynamicLightSources.size();
        this.lightSourcesLock.readLock().unlock();
        return result;
    }

    /**
     * Removes the light source from the tracked light sources.
     *
     * @param lightSource the light source to remove
     */
    public void removeLightSource(@NotNull DynamicLightSource lightSource) {
        this.lightSourcesLock.writeLock().lock();

        var dynamicLightSources = this.dynamicLightSources.iterator();
        DynamicLightSource it;
        while (dynamicLightSources.hasNext()) {
            it = dynamicLightSources.next();
            if (it.equals(lightSource)) {
                dynamicLightSources.remove();
                lightSource.sodiumdynamiclights$scheduleTrackedChunksRebuild(Minecraft.getMinecraft().renderGlobal);
                break;
            }
        }

        this.lightSourcesLock.writeLock().unlock();
    }

    /**
     * Clears light sources.
     */
    public void clearLightSources() {
        this.lightSourcesLock.writeLock().lock();

        var dynamicLightSources = this.dynamicLightSources.iterator();
        DynamicLightSource it;
        while (dynamicLightSources.hasNext()) {
            it = dynamicLightSources.next();
            dynamicLightSources.remove();
            if (it.sdl$getLuminance() > 0)
                it.sdl$resetDynamicLight();
            it.sodiumdynamiclights$scheduleTrackedChunksRebuild(Minecraft.getMinecraft().renderGlobal);
        }

        this.lightSourcesLock.writeLock().unlock();
    }

    /**
     * Removes light sources if the filter matches.
     *
     * @param filter the removal filter
     */
    public void removeLightSources(@NotNull Predicate<DynamicLightSource> filter) {
        this.lightSourcesLock.writeLock().lock();

        var dynamicLightSources = this.dynamicLightSources.iterator();
        DynamicLightSource it;
        while (dynamicLightSources.hasNext()) {
            it = dynamicLightSources.next();
            if (filter.test(it)) {
                dynamicLightSources.remove();
                if (it.sdl$getLuminance() > 0)
                    it.sdl$resetDynamicLight();
                it.sodiumdynamiclights$scheduleTrackedChunksRebuild(Minecraft.getMinecraft().renderGlobal);
                break;
            }
        }

        this.lightSourcesLock.writeLock().unlock();
    }

    /**
     * Removes entities light source from tracked light sources.
     */
    public void removeEntitiesLightSource() {
        this.removeLightSources(lightSource -> (lightSource instanceof Entity && !(lightSource instanceof EntityPlayer)));
    }

    /**
     * Removes Creeper light sources from tracked light sources.
     */
    public void removeCreeperLightSources() {
        this.removeLightSources(entity -> entity instanceof EntityCreeper);
    }

    /**
     * Removes TNT light sources from tracked light sources.
     */
    public void removeTntLightSources() {
        this.removeLightSources(entity -> entity instanceof EntityTNTPrimed);
    }

    /**
     * Removes block entities light source from tracked light sources.
     */
    public void removeBlockEntitiesLightSource() {
        this.removeLightSources(lightSource -> lightSource instanceof TileEntity);
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info the message to print
     */
    public void log(String info) {
        LOGGER.info("[LambDynLights] " + info);
    }

    /**
     * Prints a warning message to the terminal.
     *
     * @param info the message to print
     */
    public void warn(String info) {
        LOGGER.warn("[LambDynLights] " + info);
    }

    /**
     * Schedules a chunk rebuild at the specified chunk position.
     *
     * @param renderer the renderer
     * @param chunkPos the chunk position
     */
    public static void scheduleChunkRebuild(@NotNull RenderGlobal renderer, @NotNull BlockPos chunkPos) {
        scheduleChunkRebuild(renderer, chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
    }

    public static void scheduleChunkRebuild(@NotNull RenderGlobal renderer, long chunkPos) {
        int x = getX(chunkPos);
        int y = getY(chunkPos);
        int z = getZ(chunkPos);

        scheduleChunkRebuild(renderer, x, y, z);
    }

    public static void scheduleChunkRebuild(@NotNull RenderGlobal renderer, int x, int y, int z) {
        if (Minecraft.getMinecraft().world != null) {
            int minX = x * 16;
            int minZ = z * 16;
            int maxX = minX + 15;
            int maxZ = minZ + 15;

            int sectionY = y << 4;
            int maxY = sectionY + 15;


            ((WorldRendererAccessor) renderer).sodiumdynamiclights$scheduleChunkRebuild(minX, sectionY, minZ, maxX, maxY, maxZ, false);
        }
    }

    public static int getX(long packed) {
        return (int) (packed >> 38);
    }

    public static int getY(long packed) {
        return (int) ((packed >> 26) & 0xFFF);
    }

    public static int getZ(long packed) {
        return (int) (packed << 38 >> 38);
    }

    public static long blockPosToLong(BlockPos pos) {
        return (((long) pos.getX() & 0x3FFFFFF) << 38) |
                (((long) pos.getY() & 0xFFF) << 26) |
                ((long) pos.getZ() & 0x3FFFFFF);
    }

    /**
     * Updates the tracked chunk sets.
     *
     * @param chunkPos the packed chunk position
     * @param old      the set of old chunk coordinates to remove this chunk from it
     * @param newPos   the set of new chunk coordinates to add this chunk to it
     */

    public static void updateTrackedChunks(@NotNull BlockPos chunkPos, @Nullable LongOpenHashSet old, @Nullable LongOpenHashSet newPos) {
        if (old != null || newPos != null) {
            long pos = blockPosToLong(chunkPos);
            if (old != null)
                old.remove(pos);
            if (newPos != null)
                newPos.add(pos);
        }
    }

    /**
     * Updates the dynamic lights tracking.
     *
     * @param lightSource the light source
     */
    public static void updateTracking(@NotNull DynamicLightSource lightSource) {
        boolean enabled = lightSource.sdl$isDynamicLightEnabled();
        int luminance = lightSource.sdl$getLuminance();

        if (!enabled && luminance > 0) {
            lightSource.sdl$setDynamicLightEnabled(true);
        } else if (enabled && luminance < 1) {
            lightSource.sdl$setDynamicLightEnabled(false);
        }
    }

    public static boolean isEyeSubmergedInFluid(EntityLivingBase entity) {
        if (!DynamicLightsConfig.waterSensitiveCheck) {
            return false;
        }

        BlockPos eyePos = new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY + entity.getEyeHeight()), Math.floor(entity.posZ));
        IBlockState state = entity.world.getBlockState(eyePos);
        Block block = state.getBlock();
        return block instanceof IFluidBlock || block instanceof BlockLiquid;
    }

    public static int getLivingEntityLuminanceFromItems(EntityLivingBase entity) {
        boolean submergedInFluid = isEyeSubmergedInFluid(entity);
        int luminance = 0;

        for (ItemStack equipped : entity.getHeldEquipment()) {
            luminance = Math.max(luminance, SodiumDynamicLights.getLuminanceFromItemStack(equipped, submergedInFluid));
        }

        for (ItemStack armor : entity.getArmorInventoryList()) {
            luminance = Math.max(luminance, SodiumDynamicLights.getLuminanceFromItemStack(armor, submergedInFluid));
        }

        return luminance;
    }

    /**
     * Returns the luminance from an item stack.
     *
     * @param stack            the item stack
     * @param submergedInWater {@code true} if the stack is submerged in water, else {@code false}
     * @return the luminance of the item
     */
    public static int getLuminanceFromItemStack(@NotNull ItemStack stack, boolean submergedInWater) {
        if (ItemLightSources.getLuminance(stack, submergedInWater) >= 15) {
            return 14;
        } else {
            return ItemLightSources.getLuminance(stack, submergedInWater);
        }
    }

    /**
     * Returns the SodiumDynamicLights mod instance.
     *
     * @return the mod instance
     */
    public static SodiumDynamicLights get() {
        return INSTANCE;
    }
}

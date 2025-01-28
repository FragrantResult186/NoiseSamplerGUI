/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package nl.kallestruik.noisesampler.minecraft;

import nl.kallestruik.noisesampler.minecraft.util.NoiseSamplingConfig;
import nl.kallestruik.noisesampler.minecraft.util.SlideConfig;
import nl.kallestruik.noisesampler.minecraft.util.Util;

public record GenerationShapeConfig(int minimumY, int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int horizontalSize, int verticalSize, boolean islandNoiseOverride, boolean amplified, boolean largeBiomes, VanillaTerrainParameters terrainParameters) {
    public int verticalBlockSize() {
        return Util.toBlock(this.verticalSize());
    }

    public int horizontalBlockSize() {
        return Util.toBlock(this.horizontalSize());
    }

    public int minimumBlockY() {
        return Math.floorDiv(this.minimumY(), this.verticalBlockSize());
    }
}


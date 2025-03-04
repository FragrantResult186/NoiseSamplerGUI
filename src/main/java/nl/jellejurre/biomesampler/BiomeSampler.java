package nl.jellejurre.biomesampler;

import nl.jellejurre.biomesampler.minecraft.Biome;
import nl.jellejurre.biomesampler.minecraft.MathHelper;
import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.minecraft.Dimension;
import nl.kallestruik.noisesampler.minecraft.NoiseColumnSampler;
import nl.kallestruik.noisesampler.minecraft.util.NoiseValuePoint;

public class BiomeSampler {
    long seed;
    NoiseColumnSampler noiseColumnSampler;
    Dimension dimension;

    public BiomeSampler(long seed) {
        this(seed, Dimension.OVERWORLD);
    }

    public BiomeSampler(long seed, Dimension dimension) {
        this.seed = seed;
        this.dimension = dimension;
        this.noiseColumnSampler = new NoiseSampler(seed, dimension).getNoiseColumnSampler();
    }

    public Biome getBiomeFromBlockPos(int x, int y, int z){
        return getBiomeFromBiomePos(x>>2, y, z>>2);
    }

    public Biome getBiomeFromBiomePos(int x, int y, int z){
        switch (dimension){
            case OVERWORLD:
                return getBiomeAtPoint(noiseColumnSampler.sample(x, y, z), dimension);
            case NETHER:
                return getBiomeAtPoint(noiseColumnSampler.sample(x, y, z), dimension);
            case THEEND:
                return getEndBiome(x, z);
        }
        return null;
    }

    public Biome getBiomeAtPoint(NoiseValuePoint point, Dimension dimension) {
        return new BiomeEntries().lists.get(dimension).getNoiseValue(point);
    }

    private float getEndNoiseAt(int x, int z) {
        int k = x / 2;
        int l = z / 2;
        int m = x % 2;
        int n = z % 2;
        float f = 100.0F - MathHelper.sqrt(x * x + z * z) * 8.0F;
        f = MathHelper.clamp(f, -100.0F, 80.0F);
        for (int o = -12; o <= 12; ++o) {
            for (int p = -12; p <= 12; ++p) {
                long q = k + o;
                long r = l + p;
                if (q * q + r * r <= 4096L || !(noiseColumnSampler.islandNoise.sample(q, r) < (double)-0.9F)) continue;
                float g = (MathHelper.abs(q) * 3439.0F + MathHelper.abs(r) * 147.0F) % 13.0F + 9.0F;
                float h = m - o * 2;
                float s = n - p * 2;
                float t = 100.0F - MathHelper.sqrt(h * h + s * s) * g;
                t = MathHelper.clamp(t, -100.0F, 80.0F);
                f = Math.max(f, t);
            }
        }
        return f;
    }

    private Biome getEndBiome(int x, int z) {
        int i = x >> 2;
        int j = z >> 2;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return Biome.THE_END;
        }
        int k = i * 2 + 1;
        int l = j * 2 + 1;
        double d0 = ((double) getEndNoiseAt(k, l) - 8.0D) / 128.0D;
        if (d0 > 0.25D) {
            return Biome.END_HIGHLANDS;
        } else if (d0 >= -0.0625D) {
            return Biome.END_MIDLANDS;
        } else if (d0 < -0.21875D) {
            return Biome.SMALL_END_ISLANDS;
        } else {
            return Biome.END_BARRENS;
        }
    }
}
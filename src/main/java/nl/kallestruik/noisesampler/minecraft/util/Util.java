package nl.kallestruik.noisesampler.minecraft.util;

import nl.kallestruik.noisesampler.minecraft.noise.NoiseSampler;

public class Util {
    public static int toBlock(int biomeCoord) {
        return biomeCoord << 2;
    }

    public static double lerpFromProgress(NoiseSampler sampler, double x, double y, double z, double start, double end) {
        double i = sampler.sample(x, y, z);
        return MathHelper.lerpFromProgress(i, -1.0, 1.0, start, end);
    }
}

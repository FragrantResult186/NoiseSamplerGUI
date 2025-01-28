package nl.kallestruik.noisesampler.minecraft.noise;

public record ShiftedNoise(NoiseSampler shiftX, NoiseSampler shiftY, NoiseSampler shiftZ, double xzScale, double yScale, NoiseSampler noise) implements
    NoiseSampler {
    

    public static NoiseSampler shiftedNoise2d(NoiseSampler p_208297_, NoiseSampler p_208298_, double p_208299_, NoiseSampler p_208300_) {
        return new ShiftedNoise(p_208297_, new Constant(0), p_208298_, p_208299_, 0.0D, p_208300_);
    }

    public double sample(double x, double y, double z) {
        double d0 = (double)x * this.xzScale + this.shiftX.sample(x, y, z);
        double d1 = (double)y * this.yScale + this.shiftY.sample(x, y, z);
        double d2 = (double)z * this.xzScale + this.shiftZ.sample(x, y, z);
        return this.noise.sample(d0, d1, d2);
    }
}


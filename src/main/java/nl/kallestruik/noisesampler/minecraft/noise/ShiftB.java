package nl.kallestruik.noisesampler.minecraft.noise;

public class ShiftB implements NoiseSampler {
    NoiseSampler offset;

    public ShiftB(NoiseSampler offset) {
        this.offset = offset;
    }

    public double sample(double x, double y, double z) {
        return this.compute(z, x, 0);
    }

    double compute(double p_208918_, double p_208919_, double p_208920_) {
        return this.offset.sample(p_208918_ * 0.25D, p_208919_ * 0.25D, p_208920_ * 0.25D) * 4.0D;
    }
}

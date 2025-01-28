package nl.kallestruik.noisesampler.minecraft.noise;

public record Constant(double value) implements NoiseSampler{
    @Override
    public double sample(double d0, double d1, double d2) {
        return value;
    }
}

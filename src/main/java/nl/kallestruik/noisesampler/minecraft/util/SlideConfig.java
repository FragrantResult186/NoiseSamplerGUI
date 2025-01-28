package nl.kallestruik.noisesampler.minecraft.util;

public record SlideConfig(double target, int size, int offset) {

    public double method_38414(double d, int i) {
        if (this.size <= 0) {
            return d;
        }
        double e = (double)(i - this.offset) / (double)this.size;
        return MathHelper.clampedLerp(this.target, d, e);
    }
}

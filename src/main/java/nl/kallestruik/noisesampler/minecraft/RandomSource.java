package nl.kallestruik.noisesampler.minecraft;

public interface RandomSource extends SimplexNoiseRandom{
    /** @deprecated */
    @Deprecated
    double GAUSSIAN_SPREAD_FACTOR = 2.297D;
    

    void setSeed(long p_216342_);

    int nextInt();

    int nextInt(int p_216331_);

    default int nextIntBetweenInclusive(int p_216333_, int p_216334_) {
        return this.nextInt(p_216334_ - p_216333_ + 1) + p_216333_;
    }

    long nextLong();

    boolean nextBoolean();

    float nextFloat();

    double nextDouble();
    
    default double triangle(double p_216329_, double p_216330_) {
        return p_216329_ + p_216330_ * (this.nextDouble() - this.nextDouble());
    }

    default void consumeCount(int p_216338_) {
        for(int i = 0; i < p_216338_; ++i) {
            this.nextInt();
        }

    }

    default int nextInt(int p_216340_, int p_216341_) {
        if (p_216340_ >= p_216341_) {
            throw new IllegalArgumentException("bound - origin is non positive");
        } else {
            return p_216340_ + this.nextInt(p_216341_ - p_216340_);
        }
    }
}

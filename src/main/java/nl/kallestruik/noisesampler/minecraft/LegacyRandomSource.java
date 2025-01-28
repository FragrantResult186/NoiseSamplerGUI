package nl.kallestruik.noisesampler.minecraft;

import java.util.concurrent.atomic.AtomicLong;

public class LegacyRandomSource implements BitRandomSource {
    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    public LegacyRandomSource(long p_188578_) {
        this.setSeed(p_188578_);
    }

    public RandomSource fork() {
        return new LegacyRandomSource(this.nextLong());
    }

    public void setSeed(long p_188585_) {
        this.seed.compareAndSet(this.seed.get(), (p_188585_ ^ 25214903917L) & 281474976710655L);
    }

    public int next(int p_188581_) {
        long i = this.seed.get();
        long j = i * 25214903917L + 11L & 281474976710655L;
        if (!this.seed.compareAndSet(i, j)) {
            return -1;
        } else {
            return (int)(j >> 48 - p_188581_);
        }
    }


    public static long getSeed(int p_14131_, int p_14132_, int p_14133_) {
        long i = (long)(p_14131_ * 3129871) ^ (long)p_14133_ * 116129781L ^ (long)p_14132_;
        i = i * i * 42317861L + i * 11L;
        return i >> 16;
    }
}


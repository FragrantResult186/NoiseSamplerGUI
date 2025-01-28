package nl.kallestruik.noisesampler.minecraft;

import java.util.Random;

public class ChunkRand extends Random {
    public ChunkRand() {
        super(0L);
    }

    public ChunkRand(long seed) {
        super(seed);
    }
}
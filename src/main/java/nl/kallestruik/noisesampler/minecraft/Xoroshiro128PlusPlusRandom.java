package nl.kallestruik.noisesampler.minecraft;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Xoroshiro128PlusPlusRandom implements SimplexNoiseRandom, BitRandomSource {
    public Seed128 seed;
    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public Xoroshiro128PlusPlusRandom(long seed) {
        this.seed = upgradeSeedTo128bit(seed);
    }

    public Xoroshiro128PlusPlusRandom(long loSeed, long hiSeed) {
        this.seed = new Seed128(loSeed, hiSeed);
        if((loSeed | hiSeed) == 0L) {
            this.seed = new Seed128(-7046029254386353131L, 7640891576956012809L);
        }
    }

    public Xoroshiro128PlusPlusRandom createRandomDeriver() {
        return new Xoroshiro128PlusPlusRandom(this.nextLong(), this.nextLong());
    }

    public Xoroshiro128PlusPlusRandom createRandom(String string) {
        byte[] bs = md5Hash(string);
        long l = fromBytes(bs, 0);
        long m = fromBytes(bs, 8);
        return new Xoroshiro128PlusPlusRandom(l ^ this.seed.loSeed, m ^ this.seed.hiSeed);
    }

    public void skip(int count) {
        for (int j = 0; j < count; ++j) {
            this.nextLong();
        }
    }

    public static long mixStafford13(long l) {
        l = (l ^ l >>> 30) * -4658895280553007687L;
        l = (l ^ l >>> 27) * -7723592293110705685L;
        return l ^ l >>> 31;
    }

    public static Seed128 upgradeSeedTo128bit(long seed) {
        long silverSeed = seed ^ 7640891576956012809L;
        long goldenSeed = silverSeed - 7046029254386353131L;
        return new Seed128(mixStafford13(silverSeed), mixStafford13(goldenSeed));
    }

    private byte[] md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private long fromBytes(byte[] bytes, int offset) {
        return ((long) bytes[offset] & 0xff) << 56
                | ((long) bytes[offset + 1] & 0xff) << 48
                | ((long) bytes[offset + 2] & 0xff) << 40
                | ((long) bytes[offset + 3] & 0xff) << 32
                | ((long) bytes[offset + 4] & 0xff) << 24
                | ((long) bytes[offset + 5] & 0xff) << 16
                | ((long) bytes[offset + 6] & 0xff) << 8
                | ((long) bytes[offset + 7] & 0xff);
    }

    @Override
    public int next(int p_188498_) {
        return 0;
    }

    @Override
    public void setSeed(long p_216342_) {

    }

    public int nextInt() {
        return (int)this.nextLong();
    }

    public int nextInt(int n) {
        if(n <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        long r = this.nextLong() & 4294967295L;
        r *= n;
        long lowerBits = r & 4294967295L;
        if(lowerBits < (long)n) {
            int bound = Integer.remainderUnsigned(~n + 1, n);
            while(lowerBits < bound) {
                r = this.nextLong() & 4294967295L;
                r *= n;
                lowerBits = r & 4294967295L;
            }
        }
        return (int)(r >> 32);
    }

    @Override
    public int nextIntBetweenInclusive(int p_216333_, int p_216334_) {
        return BitRandomSource.super.nextIntBetweenInclusive(p_216333_, p_216334_);
    }

    public boolean nextBoolean() {
        return (this.nextLong() & 1L) != 0L;
    }

    public float nextFloat() {
        return (float)this.nextBits(24) * 5.9604645E-8f;
    }

    public double nextDouble() {
        return (double)this.nextBits(53) * 1.1102230246251565E-16;
    }

    @Override
    public double triangle(double p_216329_, double p_216330_) {
        return BitRandomSource.super.triangle(p_216329_, p_216330_);
    }

    @Override
    public void consumeCount(int p_216338_) {
        BitRandomSource.super.consumeCount(p_216338_);
    }

    @Override
    public int nextInt(int p_216340_, int p_216341_) {
        return BitRandomSource.super.nextInt(p_216340_, p_216341_);
    }

    public double nextGaussian() {
        if(this.haveNextNextGaussian) {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        } else {
            double s;
            double v1;
            double v2;
            do {
                v1 = 2.0 * this.nextDouble() - 1.0;
                v2 = 2.0 * this.nextDouble() - 1.0;
                s = v1 * v1 + v2 * v2;
            } while(s >= 1.0 || s == 0.0);
            double multiplier = StrictMath.sqrt(-2.0 * StrictMath.log(s) / s);
            this.nextNextGaussian = v2 * multiplier;
            this.haveNextNextGaussian = true;
            return v1 * multiplier;
        }
    }

    private long nextBits(int n) {
        return this.nextLong() >>> 64 - n;
    }

    public long nextLong() {
        long lowSeed = this.seed.loSeed;
        long hiSeed = this.seed.hiSeed;
        long res = Long.rotateLeft(lowSeed + hiSeed, 17) + lowSeed;
        this.seed.loSeed = Long.rotateLeft(lowSeed, 49) ^ (hiSeed ^= lowSeed) ^ (hiSeed << 21);
        this.seed.hiSeed = Long.rotateLeft(hiSeed, 28);
        return res;
    }

    public static class Seed128 {
        public long loSeed;
        public long hiSeed;

        public Seed128(long lo, long hi) {
            loSeed = lo;
            hiSeed = hi;
        }
    }
}
package nl.kallestruik.noisesampler.minecraft.noise;

import nl.kallestruik.noisesampler.minecraft.BitRandomSource;
import nl.kallestruik.noisesampler.minecraft.util.MathHelper;

public final class PerlinNoiseSampler {
    private static final int[][] GRADIENTS = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    private final byte[] permutations;
    public final double originX;
    public final double originY;
    public final double originZ;

    public PerlinNoiseSampler(BitRandomSource random) {
        int i;
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;
        this.permutations = new byte[256];
        for (i = 0; i < 256; ++i) {
            this.permutations[i] = (byte)i;
        }
        for (i = 0; i < 256; ++i) {
            int j = random.nextInt(256 - i);
            byte b = this.permutations[i];
            this.permutations[i] = this.permutations[i + j];
            this.permutations[i + j] = b;
        }
    }

    public double sample(double x, double y, double z, double yScale, double yMax) {
        double s;
        double i = x + this.originX;
        double j = y + this.originY;
        double k = z + this.originZ;
        int l = MathHelper.floor(i);
        int m = MathHelper.floor(j);
        int n = MathHelper.floor(k);
        double o = i - (double)l;
        double p = j - (double)m;
        double q = k - (double)n;
        if (yScale != 0.0) {
            double r = yMax >= 0.0 && yMax < p ? yMax : p;
            s = (double)MathHelper.floor(r / yScale + (double)1.0E-7f) * yScale;
        } else {
            s = 0.0;
        }
        return this.sample(l, m, n, o, p - s, q, p);
    }

    private static double grad(int hash, double x, double y, double z) {
        return dot(GRADIENTS[hash & 0xF], x, y, z);
    }

    private int getGradient(int hash) {
        return this.permutations[hash & 0xFF] & 0xFF;
    }

    private double sample(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalX) {
        int l = this.getGradient(sectionX);
        int m = this.getGradient(sectionX + 1);
        int n = this.getGradient(l + sectionY);
        int o = this.getGradient(l + sectionY + 1);
        int p = this.getGradient(m + sectionY);
        int q = this.getGradient(m + sectionY + 1);
        double h = PerlinNoiseSampler.grad(this.getGradient(n + sectionZ), localX, localY, localZ);
        double r = PerlinNoiseSampler.grad(this.getGradient(p + sectionZ), localX - 1.0, localY, localZ);
        double s = PerlinNoiseSampler.grad(this.getGradient(o + sectionZ), localX, localY - 1.0, localZ);
        double t = PerlinNoiseSampler.grad(this.getGradient(q + sectionZ), localX - 1.0, localY - 1.0, localZ);
        double u = PerlinNoiseSampler.grad(this.getGradient(n + sectionZ + 1), localX, localY, localZ - 1.0);
        double v = PerlinNoiseSampler.grad(this.getGradient(p + sectionZ + 1), localX - 1.0, localY, localZ - 1.0);
        double w = PerlinNoiseSampler.grad(this.getGradient(o + sectionZ + 1), localX, localY - 1.0, localZ - 1.0);
        double x = PerlinNoiseSampler.grad(this.getGradient(q + sectionZ + 1), localX - 1.0, localY - 1.0, localZ - 1.0);
        double y = MathHelper.perlinFade(localX);
        double z = MathHelper.perlinFade(fadeLocalX);
        double aa = MathHelper.perlinFade(localZ);
        return MathHelper.lerp3(y, z, aa, h, r, s, t, u, v, w, x);
    }

    private static double dot(int[] gArr, double x, double y, double z) {
        return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
    }
}
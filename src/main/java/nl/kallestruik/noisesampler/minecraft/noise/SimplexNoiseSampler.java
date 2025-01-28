package nl.kallestruik.noisesampler.minecraft.noise;

import nl.kallestruik.noisesampler.minecraft.SimplexNoiseRandom;
import nl.kallestruik.noisesampler.minecraft.util.MathHelper;

public class SimplexNoiseSampler {
    protected static final int[][] GRADIENTS = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    private static final double SQRT_3 = Math.sqrt(3.0);
    private static final double SKEW_FACTOR_2D = 0.5 * (SQRT_3 - 1.0);
    private static final double UNSKEW_FACTOR_2D = (3.0 - SQRT_3) / 6.0;
    private final int[] permutations = new int[512];
    public final double originX;
    public final double originY;
    public final double originZ;

    public SimplexNoiseSampler(SimplexNoiseRandom random) {
        int i;
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;
        for (i = 0; i < 256; ++i) {
            this.permutations[i] = i;
        }
        for (i = 0; i < 256; ++i) {
            int j = random.nextInt(256 - i);
            int k = this.permutations[i];
            this.permutations[i] = this.permutations[j + i];
            this.permutations[j + i] = k;
        }
    }

    private int getGradient(int hash) {
        return this.permutations[hash & 0xFF];
    }

    protected static double dot(int[] gArr, double x, double y, double z) {
        return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
    }

    private double grad(int hash, double x, double y, double z, double distance) {
        double j;
        double h = distance - x * x - y * y - z * z;
        if (h < 0.0) {
            j = 0.0;
        } else {
            h *= h;
            j = h * h * SimplexNoiseSampler.dot(GRADIENTS[hash], x, y, z);
        }
        return j;
    }

    public double sample(double x, double y) {
        int o;
        int n;
        double m;
        int j;
        double g;
        double f = (x + y) * SKEW_FACTOR_2D;
        int i = MathHelper.floor(x + f);
        double h = (double)i - (g = (double)(i + (j = MathHelper.floor(y + f))) * UNSKEW_FACTOR_2D);
        double l = x - h;
        if (l > (m = y - ((double)j - g))) {
            n = 1;
            o = 0;
        } else {
            n = 0;
            o = 1;
        }
        double p = l - (double)n + UNSKEW_FACTOR_2D;
        double q = m - (double)o + UNSKEW_FACTOR_2D;
        double r = l - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
        double s = m - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
        int t = i & 0xFF;
        int u = j & 0xFF;
        int v = this.getGradient(t + this.getGradient(u)) % 12;
        int w = this.getGradient(t + n + this.getGradient(u + o)) % 12;
        int x2 = this.getGradient(t + 1 + this.getGradient(u + 1)) % 12;
        double y2 = this.grad(v, l, m, 0.0, 0.5);
        double z = this.grad(w, p, q, 0.0, 0.5);
        double aa = this.grad(x2, r, s, 0.0, 0.5);
        return 70.0 * (y2 + z + aa);
    }

    public double sample(double x, double y, double z) {
        int y2;
        int x2;
        int w;
        int v;
        int u;
        int t;
        double h = (x + y + z) * 0.3333333333333333;
        int i = MathHelper.floor(x + h);
        int j = MathHelper.floor(y + h);
        int k = MathHelper.floor(z + h);
        double m = (double)(i + j + k) * 0.16666666666666666;
        double n = (double)i - m;
        double o = (double)j - m;
        double p = (double)k - m;
        double q = x - n;
        double r = y - o;
        double s = z - p;
        if (q >= r) {
            if (r >= s) {
                t = 1;
                u = 0;
                v = 0;
                w = 1;
                x2 = 1;
                y2 = 0;
            } else if (q >= s) {
                t = 1;
                u = 0;
                v = 0;
                w = 1;
                x2 = 0;
                y2 = 1;
            } else {
                t = 0;
                u = 0;
                v = 1;
                w = 1;
                x2 = 0;
                y2 = 1;
            }
        } else if (r < s) {
            t = 0;
            u = 0;
            v = 1;
            w = 0;
            x2 = 1;
            y2 = 1;
        } else if (q < s) {
            t = 0;
            u = 1;
            v = 0;
            w = 0;
            x2 = 1;
            y2 = 1;
        } else {
            t = 0;
            u = 1;
            v = 0;
            w = 1;
            x2 = 1;
            y2 = 0;
        }
        double z2 = q - (double)t + 0.16666666666666666;
        double aa = r - (double)u + 0.16666666666666666;
        double ab = s - (double)v + 0.16666666666666666;
        double ac = q - (double)w + 0.3333333333333333;
        double ad = r - (double)x2 + 0.3333333333333333;
        double ae = s - (double)y2 + 0.3333333333333333;
        double af = q - 1.0 + 0.5;
        double ag = r - 1.0 + 0.5;
        double ah = s - 1.0 + 0.5;
        int ai = i & 0xFF;
        int aj = j & 0xFF;
        int ak = k & 0xFF;
        int al = this.getGradient(ai + this.getGradient(aj + this.getGradient(ak))) % 12;
        int am = this.getGradient(ai + t + this.getGradient(aj + u + this.getGradient(ak + v))) % 12;
        int an = this.getGradient(ai + w + this.getGradient(aj + x2 + this.getGradient(ak + y2))) % 12;
        int ao = this.getGradient(ai + 1 + this.getGradient(aj + 1 + this.getGradient(ak + 1))) % 12;
        double ap = this.grad(al, q, r, s, 0.6);
        double aq = this.grad(am, z2, aa, ab, 0.6);
        double ar = this.grad(an, ac, ad, ae, 0.6);
        double as = this.grad(ao, af, ag, ah, 0.6);
        return 32.0 * (ap + aq + ar + as);
    }
}
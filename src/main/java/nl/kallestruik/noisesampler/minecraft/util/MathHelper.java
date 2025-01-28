/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package nl.kallestruik.noisesampler.minecraft.util;

import java.util.function.IntPredicate;

public class MathHelper {
    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static long lfloor(double value) {
        long l = (long)value;
        return value < (double)l ? l - 1L : l;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static double clampedLerp(double start, double end, double delta) {
        if (delta < 0.0) {
            return start;
        }
        if (delta > 1.0) {
            return end;
        }
        return MathHelper.lerp(delta, start, end);
    }

    public static double getLerpProgress(double value, double start, double end) {
        return (value - start) / (end - start);
    }

    public static int binarySearch(int start, int end, IntPredicate leftPredicate) {
        int k = end - start;
        while (k > 0) {
            int l = k / 2;
            int m = start + l;
            if (leftPredicate.test(m)) {
                k = l;
                continue;
            }
            start = m + 1;
            k -= l + 1;
        }
        return start;
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
        return MathHelper.lerp(deltaY, MathHelper.lerp(deltaX, x0y0, x1y0), MathHelper.lerp(deltaX, x0y1, x1y1));
    }

    public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
        return MathHelper.lerp(deltaZ, MathHelper.lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), MathHelper.lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
    }

    public static double perlinFade(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }

    public static double square(double n) {
        return n * n;
    }

    public static double lerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
        return MathHelper.lerp(MathHelper.getLerpProgress(lerpValue, lerpStart, lerpEnd), start, end);
    }
}


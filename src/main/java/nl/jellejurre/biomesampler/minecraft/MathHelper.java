package nl.jellejurre.biomesampler.minecraft;

public class MathHelper {
    public static float sqrt(float value) {
        return (float)Math.sqrt(value);
    }

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static long square(long n) {
        return n * n;
    }
}
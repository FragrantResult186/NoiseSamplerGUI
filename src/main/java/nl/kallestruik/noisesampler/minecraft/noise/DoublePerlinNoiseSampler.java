package nl.kallestruik.noisesampler.minecraft.noise;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import nl.kallestruik.noisesampler.minecraft.BitRandomSource;
import nl.kallestruik.noisesampler.minecraft.Xoroshiro128PlusPlusRandom;

public class DoublePerlinNoiseSampler {
    private final double amplitude;
    private final OctavePerlinNoiseSampler firstSampler;
    private final OctavePerlinNoiseSampler secondSampler;

    public static DoublePerlinNoiseSampler create(BitRandomSource random, NoiseParameters parameters) {
        return DoublePerlinNoiseSampler.create(random, parameters, false);
    }

    public static DoublePerlinNoiseSampler create(BitRandomSource random, NoiseParameters parameters, boolean nether) {
        return new DoublePerlinNoiseSampler(random, parameters.getFirstOctave(), parameters.getAmplitudes(), nether);
    }

    private DoublePerlinNoiseSampler(Xoroshiro128PlusPlusRandom random, int offset, List<Double> octaves) {
        this(random, offset, octaves, false);
    }
    private DoublePerlinNoiseSampler(BitRandomSource random, int offset, List<Double> octaves, boolean nether) {
        if (nether){
            this.firstSampler = OctavePerlinNoiseSampler.createLegacyForLegacyNetherBiome(random, offset, octaves);
            this.secondSampler = OctavePerlinNoiseSampler.createLegacyForLegacyNetherBiome(random, offset, octaves);
        } else {
            this.firstSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
            this.secondSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
        }
        int j = Integer.MAX_VALUE;
        int k = Integer.MIN_VALUE;
        for (int l = 0; l < octaves.size(); l++) {
            double d = octaves.get(l);
            if (d == 0.0) continue;
            j = Math.min(j, l);
            k = Math.max(k, l);
        }
        this.amplitude = 0.16666666666666666 / DoublePerlinNoiseSampler.createAmplitude(k - j);
    }

    private static double createAmplitude(int octaves) {
        return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
    }

    public double sample(double x, double y, double z) {
        double g = x * 1.0181268882175227;
        double h = y * 1.0181268882175227;
        double i = z * 1.0181268882175227;
        return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(g, h, i)) * this.amplitude;
    }

    public static class NoiseParameters {
        public final int firstOctave;
        public final List<Double> amplitudes;

        public NoiseParameters(int firstOctave, List<Double> amplitudes) {
            this.firstOctave = firstOctave;
            this.amplitudes = new ArrayList<>(amplitudes);
        }

        public NoiseParameters(int firstOctave, double firstAmplitude, double ... amplitudes) {
            this.firstOctave = firstOctave;
            this.amplitudes = DoubleStream.of(amplitudes).boxed().collect(Collectors.toList());
            this.amplitudes.add(0, firstAmplitude);
        }

        public int getFirstOctave() {
            return this.firstOctave;
        }

        public List<Double> getAmplitudes() {
            return this.amplitudes;
        }
    }
}
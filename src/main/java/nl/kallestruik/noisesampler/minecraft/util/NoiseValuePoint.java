package nl.kallestruik.noisesampler.minecraft.util;

public record NoiseValuePoint(long temperatureNoise, long humidityNoise, long continentalnessNoise, long erosionNoise, long depth, long weirdnessNoise) {

    public static NoiseValuePoint createNoiseValuePoint(float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise) {
        return new NoiseValuePoint((long)(temperatureNoise * 10000.0f), (long)(humidityNoise * 10000.0f), (long)(continentalnessNoise * 10000.0f), (long)(erosionNoise * 10000.0f), (long)(depth * 10000.0f), (long)(weirdnessNoise * 10000.0f));
    }
}

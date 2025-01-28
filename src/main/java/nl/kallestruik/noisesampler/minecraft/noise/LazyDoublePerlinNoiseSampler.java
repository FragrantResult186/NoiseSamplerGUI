package nl.kallestruik.noisesampler.minecraft.noise;


import nl.kallestruik.noisesampler.minecraft.BitRandomSource;
import nl.kallestruik.noisesampler.minecraft.NoiseParameterKey;
import nl.kallestruik.noisesampler.minecraft.NoiseRegistry;
import nl.kallestruik.noisesampler.minecraft.Xoroshiro128PlusPlusRandom;

public class LazyDoublePerlinNoiseSampler implements NoiseSampler{
    private boolean initialized = false;
    private DoublePerlinNoiseSampler sampler = null;

    private BitRandomSource randomDeriver;
    private String identifier;
    private DoublePerlinNoiseSampler.NoiseParameters parameters;
    private boolean nether;
    public static LazyDoublePerlinNoiseSampler create(BitRandomSource randomDeriver, String identifier, DoublePerlinNoiseSampler.NoiseParameters parameters) {
        return create(randomDeriver, identifier, parameters, false);
    }
    public static LazyDoublePerlinNoiseSampler create(BitRandomSource randomDeriver, String identifier, DoublePerlinNoiseSampler.NoiseParameters parameters, boolean nether) {
        LazyDoublePerlinNoiseSampler sampler = new LazyDoublePerlinNoiseSampler();
        sampler.nether = nether;
        sampler.randomDeriver = randomDeriver;
        sampler.identifier = identifier;
        sampler.parameters = parameters;
        return sampler;
    }

    public double sample(double x, double y, double z) {
        if (!initialized) {
            if (nether){
                sampler = DoublePerlinNoiseSampler.create(randomDeriver, parameters, nether);

            } else{
                sampler = DoublePerlinNoiseSampler.create(((Xoroshiro128PlusPlusRandom) randomDeriver).createRandom(identifier), parameters, nether);

            }
            initialized = true;
        }
        return sampler.sample(x, y, z);
    }
    public static LazyDoublePerlinNoiseSampler createNoiseSampler(BitRandomSource randomDeriver, NoiseParameterKey noise) {
        return createNoiseSampler(randomDeriver, noise, false);
    }

    public static LazyDoublePerlinNoiseSampler createNoiseSampler(BitRandomSource randomDeriver, NoiseParameterKey noise, boolean nether) {
        if (nether){
            return LazyDoublePerlinNoiseSampler.create(randomDeriver, "minecraft:" + noise.value, new DoublePerlinNoiseSampler.NoiseParameters(-7, 1, 1), nether);
        } else {
            return LazyDoublePerlinNoiseSampler.create(randomDeriver, "minecraft:" + noise.value, NoiseRegistry.registry.get(noise), nether);
        }
    }
}
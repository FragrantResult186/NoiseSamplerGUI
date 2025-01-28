package nl.kallestruik.noisesampler.minecraft;

import java.util.HashMap;
import java.util.Map;
import nl.kallestruik.noisesampler.minecraft.noise.DoublePerlinNoiseSampler;

public class NoiseRegistry {
    public static final Map<NoiseParameterKey, DoublePerlinNoiseSampler.NoiseParameters> registry = new HashMap<>();

    static {
        NoiseRegistry.register(0, NoiseParameterKey.TEMPERATURE, NoiseParameterKey.VEGETATION, NoiseParameterKey.CONTINENTALNESS, NoiseParameterKey.EROSION);
        NoiseRegistry.register(-2, NoiseParameterKey.TEMPERATURE_LARGE, NoiseParameterKey.VEGETATION_LARGE, NoiseParameterKey.CONTINENTALNESS_LARGE, NoiseParameterKey.EROSION_LARGE);
        NoiseRegistry.register(NoiseParameterKey.RIDGE, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
        NoiseRegistry.register(NoiseParameterKey.OFFSET, -3, 1.0, 1.0, 1.0, 0.0);
        NoiseRegistry.register(NoiseParameterKey.AQUIFER_BARRIER, -3, 1.0);
        NoiseRegistry.register(NoiseParameterKey.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.AQUIFER_LAVA, -1, 1.0);
        NoiseRegistry.register(NoiseParameterKey.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0);
        NoiseRegistry.register(NoiseParameterKey.PILLAR, -7, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.PILLAR_RARENESS, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.PILLAR_THICKNESS, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_2D, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_2D_ELEVATION, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_2D_MODULATOR, -11, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_2D_THICKNESS, -11, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_3D_1, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_3D_2, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_3D_RARITY, -11, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_3D_THICKNESS, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_ROUGHNESS, -5, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.CAVE_ENTRANCE, -7, 0.4, 0.5, 1.0);
        NoiseRegistry.register(NoiseParameterKey.CAVE_LAYER, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.CAVE_CHEESE, -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
        NoiseRegistry.register(NoiseParameterKey.ORE_VEININESS, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ORE_VEIN_A, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ORE_VEIN_B, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ORE_GAP, -5, 1.0);
        NoiseRegistry.register(NoiseParameterKey.NOODLE, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.NOODLE_THICKNESS, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.NOODLE_RIDGE_A, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.NOODLE_RIDGE_B, -7, 1.0);
        NoiseRegistry.register(NoiseParameterKey.JAGGED, -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SURFACE, -6, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SURFACE_SECONDARY, -6, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.CLAY_BANDS_OFFSET, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.BADLANDS_PILLAR, -2, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.BADLANDS_PILLAR_ROOF, -8, 1.0);
        NoiseRegistry.register(NoiseParameterKey.BADLANDS_SURFACE, -6, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ICEBERG_PILLAR, -6, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ICEBERG_PILLAR_ROOF, -3, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ICEBERG_SURFACE, -6, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SURFACE_SWAMP, -2, 1.0);
        NoiseRegistry.register(NoiseParameterKey.CALCITE, -9, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.GRAVEL, -8, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.POWDER_SNOW, -6, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.PACKED_ICE, -7, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.ICE, -4, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(NoiseParameterKey.SOUL_SAND_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
        NoiseRegistry.register(NoiseParameterKey.GRAVEL_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
        NoiseRegistry.register(NoiseParameterKey.PATCH, -5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
        NoiseRegistry.register(NoiseParameterKey.NETHERRACK, -3, 1.0, 0.0, 0.0, 0.35);
        NoiseRegistry.register(NoiseParameterKey.NETHER_WART, -3, 1.0, 0.0, 0.0, 0.9);
        NoiseRegistry.register(NoiseParameterKey.NETHER_STATE_SELECTOR, -4, 1.0);
    }

    private static void register(int firstOctaveOffset, NoiseParameterKey temperature, NoiseParameterKey vegetation, NoiseParameterKey continentalness, NoiseParameterKey erosion) {
        NoiseRegistry.register(temperature, -10 + firstOctaveOffset, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
        NoiseRegistry.register(vegetation, -8 + firstOctaveOffset, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
        NoiseRegistry.register(continentalness, -9 + firstOctaveOffset, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
        NoiseRegistry.register(erosion, -9 + firstOctaveOffset, 1.0, 1.0, 0.0, 1.0, 1.0);
    }

    private static void register(NoiseParameterKey noise, int firstOctave, double firstAmplitude, double ... amplitudes) {
        registry.put(noise, new DoublePerlinNoiseSampler.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
    }
}

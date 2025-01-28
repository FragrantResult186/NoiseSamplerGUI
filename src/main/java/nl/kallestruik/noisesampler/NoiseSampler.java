package nl.kallestruik.noisesampler;

import java.util.HashMap;
import java.util.Map;
import nl.kallestruik.noisesampler.minecraft.Dimension;
import nl.kallestruik.noisesampler.minecraft.util.NoiseSamplingConfig;
import nl.kallestruik.noisesampler.minecraft.util.NoiseValuePoint;
import nl.kallestruik.noisesampler.minecraft.util.SlideConfig;
import nl.kallestruik.noisesampler.minecraft.util.TerrainNoisePoint;
import nl.kallestruik.noisesampler.minecraft.GenerationShapeConfig;
import nl.kallestruik.noisesampler.minecraft.NoiseColumnSampler;
import nl.kallestruik.noisesampler.minecraft.NoiseRegistry;
import nl.kallestruik.noisesampler.minecraft.VanillaTerrainParameters;
import nl.kallestruik.noisesampler.minecraft.util.Util;

public class NoiseSampler {
    private static final GenerationShapeConfig overworldConfig = new GenerationShapeConfig(-64, 384,
        new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0),
        new SlideConfig(-0.078125, 2, 8),
        new SlideConfig(0.1171875, 3, 0),
        1, 2, false, false, false,
        VanillaTerrainParameters.createSurfaceParameters());

    private static final GenerationShapeConfig netherConfig = new GenerationShapeConfig(0, 128,
        new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
        new SlideConfig(0.9375, 3, 0),
        new SlideConfig(2.5, 4, -1),
        1, 2, false, false, false,
        VanillaTerrainParameters.createNetherParameters());

    private static final GenerationShapeConfig endConfig = new GenerationShapeConfig(0, 128,
        new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
        new SlideConfig(-23.4375, 64, -46),
        new SlideConfig(-0.234375, 7, 1),
        2, 1, true, false, false,
        VanillaTerrainParameters.createEndParameters());


    private NoiseColumnSampler noiseColumnSampler;

    public NoiseSampler(long seed, Dimension dimension) {
        switch(dimension){
            case OVERWORLD:
                noiseColumnSampler = new NoiseColumnSampler(overworldConfig, seed, dimension);
                break;
            case NETHER:
                noiseColumnSampler = new NoiseColumnSampler(netherConfig, seed, dimension);
                break;
            case THEEND:
                noiseColumnSampler = new NoiseColumnSampler(endConfig, seed, dimension);
        }
    }

    public Map<NoiseType, Double> queryNoiseFromBlockPos(int x, int y, int z, NoiseType... noiseTypes){
        return queryNoise(x>>2, y, z>>2, noiseTypes);
    }

    public Map<NoiseType, Double> queryNoise(int x, int y, int z, NoiseType... noiseTypes) {
        NoiseValuePoint noiseValuePoint = noiseColumnSampler.sample(x, y, z);
        TerrainNoisePoint terrainNoisePoint = noiseColumnSampler.createTerrainNoisePoint(
                noiseValuePoint.continentalnessNoise() / 10000.0f,
                noiseValuePoint.weirdnessNoise() / 10000.0f,
                noiseValuePoint.erosionNoise() / 10000.0f
        );

        Map<NoiseType, Double> noises = new HashMap<>();
        for (NoiseType noiseType : noiseTypes) {
            noises.put(noiseType, sampleNoise(noiseValuePoint, terrainNoisePoint, noiseType, x, y, z));
        }

        return noises;
    }

        public double sampleNoise(NoiseValuePoint noiseValuePoint, TerrainNoisePoint terrainNoisePoint, NoiseType type, int x, int y, int z) {
        switch (type) {
            case TEMPERATURE -> {
                return (float)noiseValuePoint.temperatureNoise() / 10000.0f;
            }
            case HUMIDITY -> {
                return (float)noiseValuePoint.humidityNoise() / 10000.0f;
            }
            case CONTINENTALNESS -> {
                return (float)noiseValuePoint.continentalnessNoise() / 10000.0f;
            }
            case EROSION -> {
                return (float)noiseValuePoint.erosionNoise() / 10000.0f;
            }
            case WEIRDNESS -> {
                return (float)noiseValuePoint.weirdnessNoise() / 10000.0f;
            }
            case DEPTH -> {
                return (float)noiseValuePoint.depth() / 10000.0f;
            }
            case SHIFT_X -> {
                return x + noiseColumnSampler.sampleShiftNoise(x, 0, z);
            }
            case SHIFT_Y -> {
                return y + noiseColumnSampler.sampleShiftNoise(y, z, x);
            }
            case SHIFT_Z -> {
                return z + noiseColumnSampler.sampleShiftNoise(z, x, 0);
            }
            case TERRAIN -> {
                return noiseColumnSampler.terrainNoise.calculateNoise(x, y, z);
            }
            case ISLAND -> {
                return noiseColumnSampler.islandNoise == null ? 0 : noiseColumnSampler.islandNoise.sample(x, y, z);
            }
            case JAGGED -> {
                return noiseColumnSampler.sampleJaggedNoise(terrainNoisePoint.peaks(), x, z);
            }
            case AQUIFER_BARRIER -> {
                return noiseColumnSampler.aquiferBarrierNoise.sample(x, y, z);
            }
            case AQUIFER_FLUID_LEVEL_FLOODEDNESS -> {
                return noiseColumnSampler.aquiferFluidLevelFloodednessNoise.sample(x, y, z);
            }
            case AQUIFER_FLUID_LEVEL_SPREAD -> {
                return noiseColumnSampler.aquiferFluidLevelSpreadNoise.sample(x, y, z);
            }
            case AQUIFER_LAVA -> {
                return noiseColumnSampler.aquiferLavaNoise.sample(x, y, z);
            }
            case PILLAR -> {
                return noiseColumnSampler.samplePillarNoise(x, y, z);
            }
            case PILLAR_RARENESS -> {
                return Util.lerpFromProgress(noiseColumnSampler.pillarRarenessNoise, x, y, z, 0.0D, 2.0D);
            }
            case PILLAR_THICKNESS -> {
                return Util.lerpFromProgress(noiseColumnSampler.pillarThicknessNoise, x, y, z, 0.0D, 1.1D);
            }
            case SPAGHETTI_2D -> {
                return noiseColumnSampler.sampleSpaghetti2dNoise(x, y, z);
            }
            case SPAGHETTI_2D_ELEVATION -> {
                return Util.lerpFromProgress(noiseColumnSampler.spaghetti2dElevationNoise, x, 0.0, z, noiseColumnSampler.config.minimumBlockY(), 8.0);
            }
            case SPAGHETTI_2D_MODULATOR -> {
                return noiseColumnSampler.spaghetti2dModulatorNoise.sample(x * 2, y, z * 2);
            }
            case SPAGHETTI_2D_THICKNESS -> {
                return Util.lerpFromProgress(noiseColumnSampler.spaghetti2dThicknessNoise, x * 2, y, z * 2, 0.6, 1.3);
            }
            case SPAGHETTI_3D -> {
                return noiseColumnSampler.sampleSpaghetti3dNoise(x, y, z);
            }
            case SPAGHETTI_3D_FIRST -> {
                double d = noiseColumnSampler.spaghetti3dRarityNoise.sample(x * 2, y, z * 2);
                double e = NoiseColumnSampler.CaveScaler.scaleTunnels(d);
                return NoiseColumnSampler.sample(noiseColumnSampler.spaghetti3dFirstNoise, x, y, z, e);
            }
            case SPAGHETTI_3D_SECOND -> {
                double d = noiseColumnSampler.spaghetti3dRarityNoise.sample(x * 2, y, z * 2);
                double e = NoiseColumnSampler.CaveScaler.scaleTunnels(d);
                return NoiseColumnSampler.sample(noiseColumnSampler.spaghetti3dSecondNoise, x, y, z, e);
            }
            case SPAGHETTI_3D_RARITY -> {
                return noiseColumnSampler.spaghetti3dRarityNoise.sample(x * 2, y, z * 2);
            }
            case SPAGHETTI_3D_THICKNESS -> {
                return Util.lerpFromProgress(noiseColumnSampler.spaghetti3dThicknessNoise, x, y, z, 0.065, 0.088);
            }
            case SPAGHETTI_ROUGHNESS -> {
                return noiseColumnSampler.sampleSpaghettiRoughnessNoise(x, y, z);
            }
            case SPAGHETTI_ROUGHNESS_MODULATOR -> {
                return Util.lerpFromProgress(noiseColumnSampler.spaghettiRoughnessModulatorNoise, x, y, z, 0.0, 0.1);
            }
            case CAVE_ENTRANCE -> {
                return noiseColumnSampler.sampleCaveEntranceNoise(
                        Util.toBlock(x),
                        Util.toBlock(y),
                        Util.toBlock(z)
                );
            }
            case CAVE_LAYER -> {
                // scaling them by 4 because it's sampled in normal coords
                return noiseColumnSampler.sampleCaveLayerNoise(
                        Util.toBlock(x),
                        Util.toBlock(y),
                        Util.toBlock(z)
                );
            }
            case CAVE_CHEESE -> {
                // same reason as above
                return noiseColumnSampler.caveCheeseNoise.sample(
                        Util.toBlock(x),
                        Util.toBlock(y) / 1.5,
                        Util.toBlock(z)
                );
            }
            case TERRAIN_PEAKS -> {
                return terrainNoisePoint.peaks();
            }
            case TERRAIN_OFFSET -> {
                return terrainNoisePoint.offset();
            }
            case TERRAIN_FACTOR -> {
                return terrainNoisePoint.factor();
            }
            case ORE_GAP -> {
                return noiseColumnSampler.oreGapNoise.sample(x, y, z);
            }
            default -> throw new IllegalArgumentException("Unknown noise type: " + type.name());
        }
    }

    public NoiseColumnSampler getNoiseColumnSampler() {
        return noiseColumnSampler;
    }
}

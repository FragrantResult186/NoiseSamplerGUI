package nl.jellejurre.biomesampler.minecraft;

import java.util.function.Consumer;
import nl.kallestruik.noisesampler.minecraft.util.Pair;
import nl.jellejurre.biomesampler.minecraft.MultiNoiseUtil.ParameterRange;
public final class VanillaBiomeParameters {
    public VanillaBiomeParameters() {
        SPECIAL_NEAR_MOUNTAIN_BIOMES = new Biome[][]{{Biome.ICE_SPIKES, null, null, null, null}, {Biome.CHERRY_GROVE, null, Biome.MEADOW, Biome.MEADOW, Biome.OLD_GROWTH_PINE_TAIGA}, {Biome.CHERRY_GROVE, Biome.CHERRY_GROVE, Biome.FOREST, Biome.BIRCH_FOREST, null}, {null, null, null, null, null}, {Biome.ERODED_BADLANDS, Biome.ERODED_BADLANDS, null, null, null}};
    }

    private final ParameterRange DEFAULT_PARAMETER = ParameterRange.of(-1.0F, 1.0F);
    private final ParameterRange[] TEMPERATURE_PARAMETERS = new ParameterRange[]{ParameterRange.of(-1.0F, -0.45F), ParameterRange.of(-0.45F, -0.15F), ParameterRange.of(-0.15F, 0.2F), ParameterRange.of(0.2F, 0.55F), ParameterRange.of(0.55F, 1.0F)};
    private final ParameterRange[] HUMIDITY_PARAMETERS = new ParameterRange[]{ParameterRange.of(-1.0F, -0.35F), ParameterRange.of(-0.35F, -0.1F), ParameterRange.of(-0.1F, 0.1F), ParameterRange.of(0.1F, 0.3F), ParameterRange.of(0.3F, 1.0F)};
    private final ParameterRange[] EROSION_PARAMETERS = new ParameterRange[]{ParameterRange.of(-1.0F, -0.78F), ParameterRange.of(-0.78F, -0.375F), ParameterRange.of(-0.375F, -0.2225F), ParameterRange.of(-0.2225F, 0.05F), ParameterRange.of(0.05F, 0.45F), ParameterRange.of(0.45F, 0.55F), ParameterRange.of(0.55F, 1.0F)};
    private final ParameterRange FROZEN_TEMPERATURE = this.TEMPERATURE_PARAMETERS[0];
    private final ParameterRange NON_FROZEN_TEMPERATURE_PARAMETERS = ParameterRange.combine(this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[4]);
    private final ParameterRange MUSHROOM_FIELDS_CONTINENTALNESS = ParameterRange.of(-1.2F, -1.05F);
    private final ParameterRange DEEP_OCEAN_CONTINENTALNESS = ParameterRange.of(-1.05F, -0.455F);
    private final ParameterRange OCEAN_CONTINENTALNESS = ParameterRange.of(-0.455F, -0.19F);
    private final ParameterRange SHORE_CONTINENTALNESS = ParameterRange.of(-0.19F, -0.11F);
    private final ParameterRange RIVER_CONTINENTALNESS = ParameterRange.of(-0.11F, 0.55F);
    private final ParameterRange NEAR_INLAND_CONTINENTALNESS = ParameterRange.of(-0.11F, 0.03F);
    private final ParameterRange MID_INLAND_CONTINENTALNESS = ParameterRange.of(0.03F, 0.3F);
    private final ParameterRange FAR_INLAND_CONTINENTALNESS = ParameterRange.of(0.3F, 1.0F);
    private final Biome[][] OCEAN_BIOMES = new Biome[][]{{Biome.DEEP_FROZEN_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.WARM_OCEAN}, {Biome.FROZEN_OCEAN, Biome.COLD_OCEAN, Biome.OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN}};
    private final Biome[][] COMMON_BIOMES = new Biome[][]{{Biome.SNOWY_PLAINS, Biome.SNOWY_PLAINS, Biome.SNOWY_PLAINS, Biome.SNOWY_TAIGA, Biome.TAIGA}, {Biome.PLAINS, Biome.PLAINS, Biome.FOREST, Biome.TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA}, {Biome.FLOWER_FOREST, Biome.PLAINS, Biome.FOREST, Biome.BIRCH_FOREST, Biome.DARK_FOREST}, {Biome.SAVANNA, Biome.SAVANNA, Biome.FOREST, Biome.JUNGLE, Biome.JUNGLE}, {Biome.DESERT, Biome.DESERT, Biome.DESERT, Biome.DESERT, Biome.DESERT}};
    private final Biome[][] UNCOMMON_BIOMES = new Biome[][]{{Biome.ICE_SPIKES, null, Biome.SNOWY_TAIGA, null, null}, {null, null, null, null, Biome.OLD_GROWTH_PINE_TAIGA}, {Biome.SUNFLOWER_PLAINS, null, null, Biome.OLD_GROWTH_BIRCH_FOREST, null}, {null, null, Biome.PLAINS, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE}, {null, null, null, null, null}};
    private final Biome[][] NEAR_MOUNTAIN_BIOMES = new Biome[][]{{Biome.SNOWY_PLAINS, Biome.SNOWY_PLAINS, Biome.SNOWY_PLAINS, Biome.SNOWY_TAIGA, Biome.SNOWY_TAIGA}, {Biome.MEADOW, Biome.MEADOW, Biome.FOREST, Biome.TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA}, {Biome.MEADOW, Biome.MEADOW, Biome.MEADOW, Biome.MEADOW, Biome.DARK_FOREST}, {Biome.SAVANNA_PLATEAU, Biome.SAVANNA_PLATEAU, Biome.FOREST, Biome.FOREST, Biome.JUNGLE}, {Biome.BADLANDS, Biome.BADLANDS, Biome.BADLANDS, Biome.WOODED_BADLANDS, Biome.WOODED_BADLANDS}};
    private final Biome[][] SPECIAL_NEAR_MOUNTAIN_BIOMES;
    private final Biome[][] HILL_BIOMES = new Biome[][]{{Biome.WINDSWEPT_GRAVELLY_HILLS, Biome.WINDSWEPT_GRAVELLY_HILLS, Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_FOREST, Biome.WINDSWEPT_FOREST}, {Biome.WINDSWEPT_GRAVELLY_HILLS, Biome.WINDSWEPT_GRAVELLY_HILLS, Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_FOREST, Biome.WINDSWEPT_FOREST}, {Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_FOREST, Biome.WINDSWEPT_FOREST}, {null, null, null, null, null}, {null, null, null, null, null}};

    public void writeVanillaBiomeParameters(
        Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters) {
        this.writeOceanBiomes(parameters);
        this.writeLandBiomes(parameters);
        this.writeCaveBiomes(parameters);
    }

    private void writeOceanBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters) {
        this.writeBiomeParameters(parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.MUSHROOM_FIELDS_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0F, Biome.MUSHROOM_FIELDS);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            ParameterRange biome = this.TEMPERATURE_PARAMETERS[i];
            this.writeBiomeParameters(parameters, biome, this.DEFAULT_PARAMETER, this.DEEP_OCEAN_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0F,  (this.OCEAN_BIOMES[0][i]));
            this.writeBiomeParameters(parameters, biome, this.DEFAULT_PARAMETER, this.OCEAN_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0F,  (this.OCEAN_BIOMES[1][i]));
        }
    }

    private void writeLandBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters) {
        this.writeMixedBiomes(parameters, ParameterRange.of(-1.0F, -0.93333334F));
        this.writePlainBiomes(parameters, ParameterRange.of(-0.93333334F, -0.7666667F));
        this.writeMountainousBiomes(parameters, ParameterRange.of(-0.7666667F, -0.56666666F));
        this.writePlainBiomes(parameters, ParameterRange.of(-0.56666666F, -0.4F));
        this.writeMixedBiomes(parameters, ParameterRange.of(-0.4F, -0.26666668F));
        this.writeBiomesNearRivers(parameters, ParameterRange.of(-0.26666668F, -0.05F));
        this.writeRiverBiomes(parameters, ParameterRange.of(-0.05F, 0.05F));
        this.writeBiomesNearRivers(parameters, ParameterRange.of(0.05F, 0.26666668F));
        this.writeMixedBiomes(parameters, ParameterRange.of(0.26666668F, 0.4F));
        this.writePlainBiomes(parameters, ParameterRange.of(0.4F, 0.56666666F));
        this.writeMountainousBiomes(parameters, ParameterRange.of(0.56666666F, 0.7666667F));
        this.writePlainBiomes(parameters, ParameterRange.of(0.7666667F, 0.93333334F));
        this.writeMixedBiomes(parameters, ParameterRange.of(0.93333334F, 1.0F));
    }

    private void writeMountainousBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange weirdness) {
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            ParameterRange biome = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                ParameterRange biome2 = this.HUMIDITY_PARAMETERS[j];
                Biome biome3 = this.getRegularBiome(i, j, weirdness);
                Biome biome4 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                Biome biome5 = this.getMountainStartBiome(i, j, weirdness);
                Biome biome6 = this.getNearMountainBiome(i, j, weirdness);
                Biome biome7 = this.getHillBiome(i, j, weirdness);
                Biome biome8 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, biome7);
                Biome biome9 = this.getPeakBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[0], weirdness, 0.0F, biome9);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0F, biome5);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0F, biome9);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[2], weirdness, 0.0F, biome6);
                this.writeBiomeParameters(parameters, biome, biome2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, biome4);
                this.writeBiomeParameters(parameters, biome, biome2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, biome6);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome8);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome7);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome3);
            }
        }
    }

    private void writePlainBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange weirdness) {
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            ParameterRange biome = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                ParameterRange biome2 = this.HUMIDITY_PARAMETERS[j];
                Biome biome3 = this.getRegularBiome(i, j, weirdness);
                Biome biome4 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                Biome biome5 = this.getMountainStartBiome(i, j, weirdness);
                Biome biome6 = this.getNearMountainBiome(i, j, weirdness);
                Biome biome7 = this.getHillBiome(i, j, weirdness);
                Biome biome8 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, biome3);
                Biome biome9 = this.getMountainSlopeBiome(i, j, weirdness);
                Biome biome10 = this.getPeakBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, biome9);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[0], weirdness, 0.0F, biome10);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, biome5);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0F, biome9);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[2], weirdness, 0.0F, biome6);
                this.writeBiomeParameters(parameters, biome, biome2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, biome4);
                this.writeBiomeParameters(parameters, biome, biome2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, biome6);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome8);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome7);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome3);
            }
        }
    }

    private void writeMixedBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange weirdness) {
        this.writeBiomeParameters(parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]), weirdness, 0.0F, Biome.STONY_SHORE);
        this.writeBiomeParameters(parameters, ParameterRange.combine(this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[2]), this.DEFAULT_PARAMETER, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.SWAMP);
        this.writeBiomeParameters(parameters, ParameterRange.combine(this.TEMPERATURE_PARAMETERS[3], this.TEMPERATURE_PARAMETERS[4]), this.DEFAULT_PARAMETER, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.MANGROVE_SWAMP);

        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            ParameterRange biome = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                ParameterRange biome2 = this.HUMIDITY_PARAMETERS[j];
                Biome biome3 = this.getRegularBiome(i, j, weirdness);
                Biome biome4 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                Biome biome5 = this.getMountainStartBiome(i, j, weirdness);
                Biome biome6 = this.getHillBiome(i, j, weirdness);
                Biome biome7 = this.getNearMountainBiome(i, j, weirdness);
                Biome biome8 = this.getShoreBiome(i);
                Biome biome9 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, biome3);
                Biome biome10 = this.getFlatShoreBiome(i, j, weirdness);
                Biome biome11 = this.getMountainSlopeBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[0], weirdness, 0.0F, biome11);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.MID_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0F, biome5);
                this.writeBiomeParameters(parameters, biome, biome2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, i == 0 ? biome11 : biome7);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, biome4);
                this.writeBiomeParameters(parameters, biome, biome2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, biome7);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[3], weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[3], weirdness, 0.0F, biome4);
                if (weirdness.max() < 0L) {
                    this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, biome8);
                    this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0F, biome3);
                } else {
                    this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0F, biome3);
                }
                this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome10);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome9);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome6);
                if (weirdness.max() < 0L) {
                    this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome8);
                } else {
                    this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome3);
                }
                if (i == 0) {
                    this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome3);
                }
            }
        }
    }

    private void writeBiomesNearRivers(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange weirdness) {
        this.writeBiomeParameters(parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]), weirdness, 0.0F, Biome.STONY_SHORE);
        this.writeBiomeParameters(parameters, ParameterRange.combine(this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[2]), this.DEFAULT_PARAMETER, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.SWAMP);
        this.writeBiomeParameters(parameters, ParameterRange.combine(this.TEMPERATURE_PARAMETERS[3], this.TEMPERATURE_PARAMETERS[4]), this.DEFAULT_PARAMETER, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.MANGROVE_SWAMP);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            ParameterRange biome = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                ParameterRange biome2 = this.HUMIDITY_PARAMETERS[j];
                Biome biome3 = this.getRegularBiome(i, j, weirdness);
                Biome biome4 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                Biome biome5 = this.getMountainStartBiome(i, j, weirdness);
                Biome biome6 = this.getShoreBiome(i);
                Biome biome7 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, biome3);
                Biome biome8 = this.getFlatShoreBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, biome4);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, biome5);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0F, biome4);
                this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[3], this.EROSION_PARAMETERS[4]), weirdness, 0.0F, biome6);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome8);
                this.writeBiomeParameters(parameters, biome, biome2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome7);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0F, biome3);
                this.writeBiomeParameters(parameters, biome, biome2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome6);
                if (i == 0) {
                    this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, biome3);
                }
            }
        }
    }

    private void writeRiverBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange weirdness) {
        this.writeBiomeParameters(parameters, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, weirdness.max() < 0L ? Biome.STONY_SHORE : Biome.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, weirdness.max() < 0L ? Biome.STONY_SHORE : Biome.RIVER);
        this.writeBiomeParameters(parameters, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.NEAR_INLAND_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, Biome.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, this.NEAR_INLAND_CONTINENTALNESS, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0F, Biome.RIVER);
        this.writeBiomeParameters(parameters, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]), weirdness, 0.0F, Biome.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, ParameterRange.combine(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]), weirdness, 0.0F, Biome.RIVER);
        this.writeBiomeParameters(parameters, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.RIVER);
        this.writeBiomeParameters(parameters, ParameterRange.combine(this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[2]), this.DEFAULT_PARAMETER, ParameterRange.combine(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.SWAMP);
        this.writeBiomeParameters(parameters, ParameterRange.combine(this.TEMPERATURE_PARAMETERS[3], this.TEMPERATURE_PARAMETERS[4]), this.DEFAULT_PARAMETER, ParameterRange.combine(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.MANGROVE_SWAMP);
        this.writeBiomeParameters(parameters, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, ParameterRange.combine(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, Biome.FROZEN_RIVER);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            ParameterRange biome = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                ParameterRange biome2 = this.HUMIDITY_PARAMETERS[j];
                Biome biome3 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, biome, biome2, ParameterRange.combine(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0f, biome3);
            }
        }
    }

    private void writeCaveBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters) {
        this.writeCaveBiomeParameters(parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, ParameterRange.of(0.8F, 1.0F), this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0F, Biome.DRIPSTONE_CAVES);
        this.writeCaveBiomeParameters(parameters, this.DEFAULT_PARAMETER, ParameterRange.of(0.7F, 1.0F), this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0F, Biome.LUSH_CAVES);
        this.writeBottomBiomeParameters(parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, ParameterRange.combine(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), this.DEFAULT_PARAMETER, 0.0F, Biome.DEEP_DARK);
    }

    private Biome getRegularBiome(int temperature, int humidity, ParameterRange weirdness) {
        if (weirdness.max() < 0L) {
            return this.COMMON_BIOMES[temperature][humidity];
        }
        Biome biome = this.UNCOMMON_BIOMES[temperature][humidity];
        return biome == null ? this.COMMON_BIOMES[temperature][humidity] : biome;
    }

    private Biome getBadlandsOrRegularBiome(int temperature, int humidity, ParameterRange weirdness) {
        return temperature == 4 ? this.getBadlandsBiome(humidity, weirdness) : this.getRegularBiome(temperature, humidity, weirdness);
    }

    private Biome getMountainStartBiome(int temperature, int humidity, ParameterRange weirdness) {
        return temperature == 0 ? this.getMountainSlopeBiome(temperature, humidity, weirdness) : this.getBadlandsOrRegularBiome(temperature, humidity, weirdness);
    }

    private Biome getBiomeOrWindsweptSavanna(int temperature, int humidity, ParameterRange weirdness, Biome biome) {
        if (temperature > 1 && humidity < 4 && weirdness.max() >= 0L) {
            return Biome.WINDSWEPT_SAVANNA;
        }
        return biome;
    }

    private Biome getFlatShoreBiome(int temperature, int humidity, ParameterRange weirdness) {
        Biome biome = weirdness.max() >= 0L ? this.getRegularBiome(temperature, humidity, weirdness) : this.getShoreBiome(temperature);
        return this.getBiomeOrWindsweptSavanna(temperature, humidity, weirdness, biome);
    }

    private Biome getShoreBiome(int temperature) {
        if (temperature == 0) {
            return Biome.SNOWY_BEACH;
        }
        if (temperature == 4) {
            return Biome.DESERT;
        }
        return Biome.BEACH;
    }

    private Biome getBadlandsBiome(int humidity, ParameterRange weirdness) {
        if (humidity < 2) {
            return weirdness.max() < 0L ? Biome.BADLANDS : Biome.ERODED_BADLANDS;
        }
        if (humidity < 3) {
            return Biome.BADLANDS;
        }
        return Biome.WOODED_BADLANDS;
    }

    private Biome getNearMountainBiome(int temperature, int humidity, ParameterRange weirdness) {
        if (weirdness.max() >= 0L) {
            Biome biome = this.SPECIAL_NEAR_MOUNTAIN_BIOMES[temperature][humidity];
            return biome != null ? biome : this.NEAR_MOUNTAIN_BIOMES[temperature][humidity];
        } else {
            return this.NEAR_MOUNTAIN_BIOMES[temperature][humidity];
        }
    }

    private Biome getPeakBiome(int temperature, int humidity, ParameterRange weirdness) {
        if (temperature <= 2) {
            return weirdness.max() < 0L ? Biome.JAGGED_PEAKS : Biome.FROZEN_PEAKS;
        }
        if (temperature == 3) {
            return Biome.STONY_PEAKS;
        }
        return this.getBadlandsBiome(humidity, weirdness);
    }

    private Biome getMountainSlopeBiome(int temperature, int humidity, ParameterRange weirdness) {
        if (temperature >= 3) {
            return this.getNearMountainBiome(temperature, humidity, weirdness);
        }
        if (humidity <= 1) {
            return Biome.SNOWY_SLOPES;
        }
        return Biome.GROVE;
    }

    private Biome getHillBiome(int temperature, int humidity, ParameterRange weirdness) {
        Biome biome = this.HILL_BIOMES[temperature][humidity];
        return biome == null ? this.getRegularBiome(temperature, humidity, weirdness) : biome;
    }

    private void writeBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange weirdness, float offset, Biome biome) {
        parameters.accept(new Pair<>(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, ParameterRange.of(0.0F), weirdness, offset), biome));
        parameters.accept(new Pair<>(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, ParameterRange.of(1.0F), weirdness, offset), biome));
    }

    private void writeCaveBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange weirdness, float offset, Biome biome) {
        parameters.accept(new Pair<>(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, ParameterRange.of(0.2F, 0.9F), weirdness, offset), biome));
    }

    private void writeBottomBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> parameters, ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange weirdness, float offset, Biome biome) {
        parameters.accept(new Pair<>(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, ParameterRange.of(1.1F), weirdness, offset), biome));
    }
}
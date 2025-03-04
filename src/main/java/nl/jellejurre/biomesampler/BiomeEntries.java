package nl.jellejurre.biomesampler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nl.jellejurre.biomesampler.minecraft.Biome;
import nl.jellejurre.biomesampler.minecraft.MultiNoiseUtil;
import nl.jellejurre.biomesampler.minecraft.VanillaBiomeParameters;
import nl.kallestruik.noisesampler.minecraft.Dimension;
import nl.kallestruik.noisesampler.minecraft.util.Pair;

@SuppressWarnings("ALL")
public class BiomeEntries {
    HashMap<Dimension, MultiNoiseUtil.Entries<Biome>> lists = new HashMap<>();
    public BiomeEntries() {
        ArrayList<Pair<MultiNoiseUtil.NoiseHypercube, Biome>> list = new ArrayList<>();
        new VanillaBiomeParameters().writeVanillaBiomeParameters(list::add);
        lists.put(Dimension.OVERWORLD, new MultiNoiseUtil.Entries(list));
        lists.put(Dimension.NETHER, new MultiNoiseUtil.Entries(List.of(
                new Pair(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                        Biome.NETHER_WASTES),
                new Pair(MultiNoiseUtil.createNoiseHypercube(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                        Biome.SOUL_SAND_VALLEY),
                new Pair(MultiNoiseUtil.createNoiseHypercube(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                        Biome.CRIMSON_FOREST),
                new Pair(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F),
                        Biome.WARPED_FOREST),
                new Pair(MultiNoiseUtil.createNoiseHypercube(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F),
                        Biome.BASALT_DELTAS))));
    }
}
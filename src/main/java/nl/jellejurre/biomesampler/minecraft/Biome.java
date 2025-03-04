package nl.jellejurre.biomesampler.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Biome {
    PLAINS("plains", Category.PLAINS),
    SUNFLOWER_PLAINS("sunflower_plains", Category.PLAINS),
    SNOWY_PLAINS("snowy_plains", Category.ICY),
    ICE_SPIKES("ice_spikes", Category.ICY),
    DESERT("desert", Category.DESERT),
    SWAMP("swamp", Category.SWAMP),
    // "mangrove_swamp" was added in 1.19
    MANGROVE_SWAMP("mangrove_swamp", Category.SWAMP),
    FOREST("forest", Category.FOREST),
    FLOWER_FOREST("flower_forest", Category.FOREST),
    BIRCH_FOREST("birch_forest", Category.FOREST),
    DARK_FOREST("dark_forest", Category.FOREST),
    OLD_GROWTH_BIRCH_FOREST("old_growth_birch_forest", Category.FOREST),
    OLD_GROWTH_PINE_TAIGA("old_growth_pine_taiga", Category.TAIGA),
    OLD_GROWTH_SPRUCE_TAIGA("old_growth_spruce_taiga", Category.TAIGA),
    TAIGA("taiga", Category.TAIGA),
    SNOWY_TAIGA("snowy_taiga", Category.TAIGA),
    SAVANNA("savanna", Category.SAVANNA),
    SAVANNA_PLATEAU("savanna_plateau", Category.SAVANNA),
    WINDSWEPT_HILLS("windswept_hills", Category.EXTREME_HILLS),
    WINDSWEPT_GRAVELLY_HILLS("windswept_gravelly_hills", Category.EXTREME_HILLS),
    WINDSWEPT_FOREST("windswept_forest", Category.EXTREME_HILLS),
    WINDSWEPT_SAVANNA("windswept_savanna", Category.SAVANNA),
    JUNGLE("jungle", Category.JUNGLE),
    SPARSE_JUNGLE("sparse_jungle", Category.JUNGLE),
    BAMBOO_JUNGLE("bamboo_jungle", Category.JUNGLE),
    BADLANDS("badlands", Category.BADLANDS),
    ERODED_BADLANDS("eroded_badlands", Category.BADLANDS),
    WOODED_BADLANDS("wooded_badlands", Category.BADLANDS),
    MEADOW("meadow", Category.MOUNTAIN),
    // "cherry_grove" was added in 1.20
    CHERRY_GROVE("cherry_grove", Category.MOUNTAIN),
    GROVE("grove", Category.FOREST),
    SNOWY_SLOPES("snowy_slopes", Category.MOUNTAIN),
    FROZEN_PEAKS("frozen_peaks", Category.MOUNTAIN),
    JAGGED_PEAKS("jagged_peaks", Category.MOUNTAIN),
    STONY_PEAKS("stony_peaks", Category.MOUNTAIN),
    RIVER("river", Category.RIVER),
    FROZEN_RIVER("frozen_river", Category.RIVER),
    BEACH("beach", Category.BEACH),
    SNOWY_BEACH("snowy_beach", Category.BEACH),
    STONY_SHORE("stony_shore", Category.BEACH),
    WARM_OCEAN("warm_ocean", Category.OCEAN),
    LUKEWARM_OCEAN("lukewarm_ocean", Category.OCEAN),
    DEEP_LUKEWARM_OCEAN("deep_lukewarm_ocean", Category.OCEAN),
    OCEAN("ocean", Category.OCEAN),
    DEEP_OCEAN("deep_ocean", Category.OCEAN),
    COLD_OCEAN("cold_ocean", Category.OCEAN),
    DEEP_COLD_OCEAN("deep_cold_ocean", Category.OCEAN),
    FROZEN_OCEAN("frozen_ocean", Category.OCEAN),
    DEEP_FROZEN_OCEAN("deep_frozen_ocean", Category.OCEAN),
    MUSHROOM_FIELDS("mushroom_fields", Category.MUSHROOM),
    DRIPSTONE_CAVES("dripstone_caves", Category.UNDERGROUND),
    LUSH_CAVES("lush_caves", Category.UNDERGROUND),
    //"deep_dark" was added in 1.19
    DEEP_DARK("deep_dark", Category.UNDERGROUND),
    NETHER_WASTES("nether_wastes", Category.NETHER),
    WARPED_FOREST("warped_forest", Category.NETHER),
    CRIMSON_FOREST("crimson_forest", Category.NETHER),
    SOUL_SAND_VALLEY("soul_sand_valley", Category.NETHER),
    BASALT_DELTAS("basalt_deltas", Category.NETHER),
    THE_END("the_end", Category.THEEND),
    END_HIGHLANDS("end_highlands", Category.THEEND),
    END_MIDLANDS("end_midlands", Category.THEEND),
    SMALL_END_ISLANDS("small_end_islands", Category.THEEND),
    END_BARRENS("end_barrens", Category.THEEND);

    private final String name;
    private final Category category;

    Biome(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public final String getName() {
        return name;
    }

    public final Category getCategory() {
        return this.category;
    }

    public enum Category {
        TAIGA("taiga"),
        EXTREME_HILLS("extreme_hills"),
        JUNGLE("jungle"),
        BADLANDS("badlands"),
        PLAINS("plains"),
        SAVANNA("savanna"),
        ICY("icy"),
        THEEND("the_end"),
        BEACH("beach"),
        FOREST("forest"),
        OCEAN("ocean"),
        DESERT("desert"),
        RIVER("river"),
        SWAMP("swamp"),
        MUSHROOM("mushroom"),
        NETHER("nether"),
        UNDERGROUND("underground"),
        MOUNTAIN("mountain");

        private static final Map<String, Category> BY_NAME;
        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Category byName(String name) {
            return BY_NAME.get(name);
        }

        static {
            BY_NAME = Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getName, category -> category));
        }
    }
}
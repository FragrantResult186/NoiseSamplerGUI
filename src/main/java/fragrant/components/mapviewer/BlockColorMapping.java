package fragrant.components.mapviewer;

import java.util.HashMap;
import java.util.Map;

public class BlockColorMapping {
    public static final Map<String, String> BLOCK_COLORS = new HashMap<>();
    public static final Map<String, Map<String, String>> BIOME_COLOR_VARIATIONS = new HashMap<>();

    static {
        // STONE VARIANTS
        BLOCK_COLORS.put("stone", "#a9a9a9");
        BLOCK_COLORS.put("cobblestone", "#a9a9a9");
        BLOCK_COLORS.put("granite", "#d29c84");
        BLOCK_COLORS.put("polished_granite", "#b47f61");
        BLOCK_COLORS.put("diorite", "#d7d7d7");
        BLOCK_COLORS.put("andesite", "#8e8e8e");
        BLOCK_COLORS.put("polished_andesite", "#7e7e7e");
        BLOCK_COLORS.put("tuff", "#5c5c5c");
        BLOCK_COLORS.put("calcite", "#ede8df");
        BLOCK_COLORS.put("chiseled_stone_bricks", "#7b7b7b");

        // DEEPSLATE VARIANTS
        BLOCK_COLORS.put("deepslate", "#4d4d4d");
        BLOCK_COLORS.put("cobbled_deepslate", "#6b6b6b");
        BLOCK_COLORS.put("polished_deepslate", "#525252");

        // ORES
        BLOCK_COLORS.put("coal_ore", "#3f3f3f");
        BLOCK_COLORS.put("deepslate_coal_ore", "#2b2b2b");
        BLOCK_COLORS.put("iron_ore", "#d8af93");
        BLOCK_COLORS.put("deepslate_iron_ore", "#a68977");
        BLOCK_COLORS.put("copper_ore", "#ad6f3b");
        BLOCK_COLORS.put("deepslate_copper_ore", "#8d5631");
        BLOCK_COLORS.put("gold_ore", "#e3ca2d");
        BLOCK_COLORS.put("deepslate_gold_ore", "#bba621");
        BLOCK_COLORS.put("redstone_ore", "#ff0000");
        BLOCK_COLORS.put("deepslate_redstone_ore", "#cc0000");
        BLOCK_COLORS.put("emerald_ore", "#17dd62");
        BLOCK_COLORS.put("deepslate_emerald_ore", "#15b556");
        BLOCK_COLORS.put("lapis_ore", "#2546ad");
        BLOCK_COLORS.put("deepslate_lapis_ore", "#1c377f");
        BLOCK_COLORS.put("diamond_ore", "#2ee1e4");
        BLOCK_COLORS.put("deepslate_diamond_ore", "#25b6b9");

        // GRASS: 127, 178, 56 -> #7FB238
        BLOCK_COLORS.put("grass_block", "#7FB238");
        BLOCK_COLORS.put("slime_block", "#7FB238");

        // SAND: 247, 233, 163 -> #F7E9A3
        BLOCK_COLORS.put("sand", "#F7E9A3");
        BLOCK_COLORS.put("birch_planks", "#F7E9A3");
        BLOCK_COLORS.put("birch_log", "#F7E9A3");
        BLOCK_COLORS.put("stripped_birch_log", "#F7E9A3");
        BLOCK_COLORS.put("birch_wood", "#F7E9A3");
        BLOCK_COLORS.put("stripped_birch_wood", "#F7E9A3");
        BLOCK_COLORS.put("birch_sign", "#F7E9A3");
        BLOCK_COLORS.put("birch_pressure_plate", "#F7E9A3");
        BLOCK_COLORS.put("birch_trapdoor", "#F7E9A3");
        BLOCK_COLORS.put("birch_stairs", "#F7E9A3");
        BLOCK_COLORS.put("birch_slab", "#F7E9A3");
        BLOCK_COLORS.put("birch_fence_gate", "#F7E9A3");
        BLOCK_COLORS.put("birch_fence", "#F7E9A3");
        BLOCK_COLORS.put("birch_door", "#F7E9A3");
        BLOCK_COLORS.put("sandstone", "#F7E9A3");
        BLOCK_COLORS.put("chiseled_sandstone", "#F7E9A3");
        BLOCK_COLORS.put("cut_sandstone", "#F7E9A3");
        BLOCK_COLORS.put("smooth_sandstone", "#F7E9A3");
        BLOCK_COLORS.put("sandstone_stairs", "#F7E9A3");
        BLOCK_COLORS.put("sandstone_slab", "#F7E9A3");
        BLOCK_COLORS.put("sandstone_wall", "#F7E9A3");
        BLOCK_COLORS.put("end_stone", "#F7E9A3");
        BLOCK_COLORS.put("end_stone_bricks", "#F7E9A3");
        BLOCK_COLORS.put("end_stone_brick_stairs", "#F7E9A3");
        BLOCK_COLORS.put("end_stone_brick_slab", "#F7E9A3");
        BLOCK_COLORS.put("end_stone_brick_wall", "#F7E9A3");
        BLOCK_COLORS.put("end_portal_flame", "#F7E9A3");
        BLOCK_COLORS.put("glowstone", "#F7E9A3");
        BLOCK_COLORS.put("bone_block", "#F7E9A3");
        BLOCK_COLORS.put("turtle_egg", "#F7E9A3");
        BLOCK_COLORS.put("scaffolding", "#F7E9A3");
        BLOCK_COLORS.put("candle", "#F7E9A3");
        BLOCK_COLORS.put("ochre_froglight", "#F7E9A3");

        // WOOL: 199, 199, 199 -> #C7C7C7
        BLOCK_COLORS.put("cobweb", "#C7C7C7");
        BLOCK_COLORS.put("mushroom_stem", "#C7C7C7");
        BLOCK_COLORS.put("bed_head", "#C7C7C7"); // Assuming bed (head) is white
        BLOCK_COLORS.put("white_candle", "#C7C7C7");

        // FIRE: 255, 0, 0 -> #FF0000
        BLOCK_COLORS.put("lava", "#FF0000");
        BLOCK_COLORS.put("tnt", "#FF0000");
        //BLOCK_COLORS.put("fire", "#FF0000");
        BLOCK_COLORS.put("redstone_block", "#FF0000");

        // ICE: 160, 160, 255 -> #A0A0FF
        BLOCK_COLORS.put("ice", "#A0A0FF");
        BLOCK_COLORS.put("frosted_ice", "#A0A0FF");
        BLOCK_COLORS.put("packed_ice", "#A0A0FF");
        BLOCK_COLORS.put("blue_ice", "#A0A0FF");

        // METAL: 167, 167, 167 -> #A7A7A7
        BLOCK_COLORS.put("block_of_iron", "#A7A7A7");
        BLOCK_COLORS.put("iron_door", "#A7A7A7");
        BLOCK_COLORS.put("brewing_stand", "#A7A7A7");
        //BLOCK_COLORS.put("heavy_weighted_pressure_plate", "#A7A7A7");
        BLOCK_COLORS.put("iron_trapdoor", "#A7A7A7");
        BLOCK_COLORS.put("lantern", "#A7A7A7");
        BLOCK_COLORS.put("anvil", "#A7A7A7"); // Assuming all damage levels are same color
        BLOCK_COLORS.put("grindstone", "#A7A7A7");
        //BLOCK_COLORS.put("soul_fire_lantern", "#A7A7A7");
        BLOCK_COLORS.put("lodestone", "#A7A7A7");

        // PLANT: 0, 124, 0 -> #007C00
        //BLOCK_COLORS.put("sapling", "#007C00");
        //BLOCK_COLORS.put("flowers", "#007C00"); // A general color for flowers
        //BLOCK_COLORS.put("wheat", "#007C00");
        BLOCK_COLORS.put("sugar_cane", "#007C00");
        BLOCK_COLORS.put("pumpkin_stem", "#007C00");
        BLOCK_COLORS.put("melon_stem", "#007C00");
        BLOCK_COLORS.put("lily_pad", "#007C00");
        BLOCK_COLORS.put("cocoa", "#007C00");
        //BLOCK_COLORS.put("carrots", "#007C00");
        //BLOCK_COLORS.put("potatoes", "#007C00");
        //BLOCK_COLORS.put("beetroots", "#007C00");
        BLOCK_COLORS.put("sweet_berry_bush", "#007C00");
        BLOCK_COLORS.put("fern", "#007C00");
        //BLOCK_COLORS.put("vine", "#007C00");
        BLOCK_COLORS.put("leaves", "#007C00"); // A general color for all leaf types
        BLOCK_COLORS.put("cactus", "#007C00");
        BLOCK_COLORS.put("bamboo", "#007C00");
        //BLOCK_COLORS.put("cave_vines", "#007C00");
        BLOCK_COLORS.put("spore_blossom", "#007C00");
        BLOCK_COLORS.put("azalea", "#007C00");
        BLOCK_COLORS.put("flowering_azalea", "#007C00");
        BLOCK_COLORS.put("big_dripleaf", "#007C00");
        BLOCK_COLORS.put("small_dripleaf", "#007C00");
        BLOCK_COLORS.put("seagrass", "#007C00");

        // SNOW: 255, 255, 255 -> #FFFFFF
        //BLOCK_COLORS.put("snow", "#FFFFFF");
        BLOCK_COLORS.put("snow_block", "#FFFFFF");
        BLOCK_COLORS.put("white_bed_foot", "#FFFFFF");
        BLOCK_COLORS.put("white_wool", "#FFFFFF");
        BLOCK_COLORS.put("white_stained_glass", "#FFFFFF");
        //BLOCK_COLORS.put("white_carpet", "#FFFFFF");
        BLOCK_COLORS.put("white_shulker_box", "#FFFFFF");
        BLOCK_COLORS.put("white_glazed_terracotta", "#FFFFFF");
        BLOCK_COLORS.put("white_concrete", "#FFFFFF");
        BLOCK_COLORS.put("white_concrete_powder", "#FFFFFF");
        BLOCK_COLORS.put("powder_snow", "#FFFFFF");

        // CLAY: 164, 168, 184 -> #A4A8B8
        BLOCK_COLORS.put("clay", "#A4A8B8");
        BLOCK_COLORS.put("infested_block", "#A4A8B8"); // Excludes infested deepslate

        // DIRT: 151, 109, 77 -> #976D4D
        BLOCK_COLORS.put("dirt", "#976D4D");
        BLOCK_COLORS.put("coarse_dirt", "#976D4D");
        BLOCK_COLORS.put("farmland", "#976D4D");
        BLOCK_COLORS.put("dirt_path", "#976D4D");
        BLOCK_COLORS.put("granite_slab", "#976D4D");
        BLOCK_COLORS.put("granite_stairs", "#976D4D");
        BLOCK_COLORS.put("granite_wall", "#976D4D");
        BLOCK_COLORS.put("polished_granite_slab", "#976D4D");
        BLOCK_COLORS.put("polished_granite_stairs", "#976D4D");
        BLOCK_COLORS.put("jungle_planks", "#976D4D");
        BLOCK_COLORS.put("jungle_log", "#976D4D");
        BLOCK_COLORS.put("stripped_jungle_log", "#976D4D");
        BLOCK_COLORS.put("jungle_wood", "#976D4D");
        BLOCK_COLORS.put("stripped_jungle_wood", "#976D4D");
        BLOCK_COLORS.put("jungle_sign", "#976D4D");
        BLOCK_COLORS.put("jungle_pressure_plate", "#976D4D");
        BLOCK_COLORS.put("jungle_trapdoor", "#976D4D");
        BLOCK_COLORS.put("jungle_stairs", "#976D4D");
        BLOCK_COLORS.put("jungle_slab", "#976D4D");
        BLOCK_COLORS.put("jungle_fence_gate", "#976D4D");
        BLOCK_COLORS.put("jungle_fence", "#976D4D");
        BLOCK_COLORS.put("jungle_door", "#976D4D");
        BLOCK_COLORS.put("jukebox", "#976D4D");
        BLOCK_COLORS.put("brown_mushroom_block", "#976D4D");
        BLOCK_COLORS.put("rooted_dirt", "#976D4D");
        BLOCK_COLORS.put("hanging_roots", "#976D4D");
        BLOCK_COLORS.put("packed_mud", "#976D4D");

        // STONE: 112, 112, 112 -> #707070
        BLOCK_COLORS.put("stone_slab", "#707070");
        BLOCK_COLORS.put("stone_stairs", "#707070");
        BLOCK_COLORS.put("andesite_slab", "#707070");
        BLOCK_COLORS.put("andesite_stairs", "#707070");
        BLOCK_COLORS.put("andesite_wall", "#707070");
        BLOCK_COLORS.put("polished_andesite_slab", "#707070");
        BLOCK_COLORS.put("polished_andesite_stairs", "#707070");
        BLOCK_COLORS.put("cobblestone_slab", "#707070");
        BLOCK_COLORS.put("cobblestone_stairs", "#707070");
        BLOCK_COLORS.put("cobblestone_wall", "#707070");
        BLOCK_COLORS.put("gold_ore", "#707070");
        BLOCK_COLORS.put("iron_ore", "#707070");
        BLOCK_COLORS.put("coal_ore", "#707070");
        BLOCK_COLORS.put("lapis_lazuli_ore", "#707070");
        BLOCK_COLORS.put("dispenser", "#707070");
        BLOCK_COLORS.put("mossy_cobblestone_slab", "#707070");
        BLOCK_COLORS.put("mossy_cobblestone_stairs", "#707070");
        BLOCK_COLORS.put("mossy_cobblestone_wall", "#707070");
        BLOCK_COLORS.put("spawner", "#707070");
        BLOCK_COLORS.put("diamond_ore", "#707070");
        BLOCK_COLORS.put("furnace", "#707070");
        BLOCK_COLORS.put("stone_pressure_plate", "#707070");
        BLOCK_COLORS.put("redstone_ore", "#707070");
        BLOCK_COLORS.put("stone_bricks", "#707070");
        BLOCK_COLORS.put("stone_brick_slab", "#707070");
        BLOCK_COLORS.put("stone_brick_stairs", "#707070");
        BLOCK_COLORS.put("stone_brick_wall", "#707070");
        BLOCK_COLORS.put("emerald_ore", "#707070");
        BLOCK_COLORS.put("dropper", "#707070");
        BLOCK_COLORS.put("smooth_stone", "#707070");
        BLOCK_COLORS.put("smooth_stone_slab", "#707070");
        BLOCK_COLORS.put("observer", "#707070");
        BLOCK_COLORS.put("smoker", "#707070");
        BLOCK_COLORS.put("blast_furnace", "#707070");
        BLOCK_COLORS.put("stonecutter", "#707070");
        BLOCK_COLORS.put("sticky_piston", "#707070");
        BLOCK_COLORS.put("piston", "#707070");
        BLOCK_COLORS.put("piston_head", "#707070");
        BLOCK_COLORS.put("gravel", "#707070");
        BLOCK_COLORS.put("acacia_log_side", "#707070");
        BLOCK_COLORS.put("cauldron", "#707070");
        BLOCK_COLORS.put("cauldron_with_water", "#707070");
        BLOCK_COLORS.put("cauldron_with_lava", "#707070");
        BLOCK_COLORS.put("cauldron_with_powder_snow", "#707070");
        BLOCK_COLORS.put("hopper", "#707070");
        BLOCK_COLORS.put("copper_ore", "#707070");

        // WATER: 64, 64, 255 -> #4040FF
        BLOCK_COLORS.put("kelp", "#4040FF");
        BLOCK_COLORS.put("water", "#4040FF");
        BLOCK_COLORS.put("bubble_column", "#4040FF");
        BLOCK_COLORS.put("waterlogged_leaves", "#4040FF");

        // WOOD: 143, 119, 72 -> #8F7748
        BLOCK_COLORS.put("oak_planks", "#8F7748");
        BLOCK_COLORS.put("oak_log", "#8F7748");
        BLOCK_COLORS.put("stripped_oak_log", "#8F7748");
        BLOCK_COLORS.put("oak_wood", "#8F7748");
        BLOCK_COLORS.put("stripped_oak_wood", "#8F7748");
        BLOCK_COLORS.put("oak_sign", "#8F7748");
        BLOCK_COLORS.put("oak_door", "#8F7748");
        BLOCK_COLORS.put("oak_pressure_plate", "#8F7748");
        BLOCK_COLORS.put("oak_fence", "#8F7748");
        BLOCK_COLORS.put("oak_trapdoor", "#8F7748");
        BLOCK_COLORS.put("oak_fence_gate", "#8F7748");
        BLOCK_COLORS.put("oak_slab", "#8F7748");
        BLOCK_COLORS.put("oak_stairs", "#8F7748");
        BLOCK_COLORS.put("note_block", "#8F7748");
        BLOCK_COLORS.put("bookshelf", "#8F7748");
        BLOCK_COLORS.put("chest", "#917436");
        BLOCK_COLORS.put("crafting_table", "#8F7748");
        BLOCK_COLORS.put("trapped_chest", "#8F7748");
        BLOCK_COLORS.put("daylight_detector", "#8F7748");
        BLOCK_COLORS.put("loom", "#8F7748");
        BLOCK_COLORS.put("barrel", "#8F7748");
        BLOCK_COLORS.put("cartography_table", "#8F7748");
        BLOCK_COLORS.put("fletching_table", "#8F7748");
        BLOCK_COLORS.put("lectern", "#8F7748");
        BLOCK_COLORS.put("smithing_table", "#8F7748");
        BLOCK_COLORS.put("composter", "#8F7748");
        BLOCK_COLORS.put("bamboo_sapling", "#8F7748");
        BLOCK_COLORS.put("dead_bush", "#8F7748");
        BLOCK_COLORS.put("petrified_oak_slab", "#8F7748");
        BLOCK_COLORS.put("beehive", "#8F7748");
        BLOCK_COLORS.put("banners", "#8F7748"); // General color for all banners when not as markers

        // QUARTZ: 255, 252, 245 -> #FFFCD5
        BLOCK_COLORS.put("diorite", "#FFFCD5");
        BLOCK_COLORS.put("diorite_stairs", "#FFFCD5");
        BLOCK_COLORS.put("diorite_slab", "#FFFCD5");
        BLOCK_COLORS.put("diorite_wall", "#FFFCD5");
        BLOCK_COLORS.put("polished_diorite", "#FFFCD5");
        BLOCK_COLORS.put("polished_diorite_stairs", "#FFFCD5");
        BLOCK_COLORS.put("polished_diorite_slab", "#FFFCD5");
        BLOCK_COLORS.put("birch_log_side", "#FFFCD5");
        BLOCK_COLORS.put("quartz_block", "#FFFCD5");
        BLOCK_COLORS.put("quartz_slab", "#FFFCD5");
        BLOCK_COLORS.put("quartz_stairs", "#FFFCD5");
        BLOCK_COLORS.put("sea_lantern", "#FFFCD5");
        BLOCK_COLORS.put("target", "#FFFCD5");

        // COLOR_ORANGE: 216, 127, 51 -> #D87F33
        BLOCK_COLORS.put("acacia_planks", "#D87F33");
        BLOCK_COLORS.put("acacia_log", "#D87F33");
        BLOCK_COLORS.put("stripped_acacia_log", "#D87F33");
        BLOCK_COLORS.put("acacia_wood", "#D87F33");
        BLOCK_COLORS.put("stripped_acacia_wood", "#D87F33");
        BLOCK_COLORS.put("acacia_sign", "#D87F33");
        BLOCK_COLORS.put("acacia_trapdoor", "#D87F33");
        BLOCK_COLORS.put("acacia_slab", "#D87F33");
        BLOCK_COLORS.put("acacia_stairs", "#D87F33");
        BLOCK_COLORS.put("acacia_pressure_plate", "#D87F33");
        BLOCK_COLORS.put("acacia_fence_gate", "#D87F33");
        BLOCK_COLORS.put("acacia_fence", "#D87F33");
        BLOCK_COLORS.put("acacia_door", "#D87F33");
        BLOCK_COLORS.put("red_sand", "#D87F33");
        BLOCK_COLORS.put("orange_wool", "#D87F33");
        //BLOCK_COLORS.put("orange_carpet", "#D87F33");
        BLOCK_COLORS.put("orange_shulker_box", "#D87F33");
        BLOCK_COLORS.put("orange_bed", "#D87F33");
        BLOCK_COLORS.put("orange_stained_glass", "#D87F33");
        BLOCK_COLORS.put("orange_glazed_terracotta", "#D87F33");
        BLOCK_COLORS.put("orange_concrete", "#D87F33");
        BLOCK_COLORS.put("orange_concrete_powder", "#D87F33");
        BLOCK_COLORS.put("orange_candle", "#D87F33");
        BLOCK_COLORS.put("pumpkin", "#D87F33");
        BLOCK_COLORS.put("carved_pumpkin", "#D87F33");
        BLOCK_COLORS.put("jack_o_lantern", "#D87F33");
        BLOCK_COLORS.put("terracotta", "#D87F33");
        BLOCK_COLORS.put("red_sandstone", "#D87F33");
        BLOCK_COLORS.put("honey_block", "#D87F33");
        BLOCK_COLORS.put("honeycomb_block", "#D87F33");
        BLOCK_COLORS.put("copper_block", "#D87F33");
        BLOCK_COLORS.put("lightning_rod", "#D87F33");
        BLOCK_COLORS.put("block_of_raw_copper", "#D87F33");

        // COLOR_MAGENTA: 178, 76, 216 -> #B24CD8
        BLOCK_COLORS.put("magenta_wool", "#B24CD8");
        //BLOCK_COLORS.put("magenta_carpet", "#B24CD8");
        BLOCK_COLORS.put("magenta_shulker_box", "#B24CD8");
        BLOCK_COLORS.put("magenta_bed", "#B24CD8");
        BLOCK_COLORS.put("magenta_stained_glass", "#B24CD8");
        BLOCK_COLORS.put("magenta_glazed_terracotta", "#B24CD8");
        BLOCK_COLORS.put("magenta_concrete", "#B24CD8");
        BLOCK_COLORS.put("magenta_concrete_powder", "#B24CD8");
        BLOCK_COLORS.put("magenta_candle", "#B24CD8");
        BLOCK_COLORS.put("purpur_block", "#B24CD8");
        BLOCK_COLORS.put("purpur_slab", "#B24CD8");
        BLOCK_COLORS.put("purpur_stairs", "#B24CD8");
        BLOCK_COLORS.put("ender_chest", "#B24CD8");

        // COLOR_GRAY: 76, 76, 76 -> #4C4C4C
        BLOCK_COLORS.put("acacia_wood", "#4C4C4C");
        BLOCK_COLORS.put("gray_wool", "#4C4C4C");
        //BLOCK_COLORS.put("gray_carpet", "#4C4C4C");
        BLOCK_COLORS.put("gray_shulker_box", "#4C4C4C");
        BLOCK_COLORS.put("gray_bed", "#4C4C4C");
        BLOCK_COLORS.put("gray_stained_glass", "#4C4C4C");
        BLOCK_COLORS.put("gray_glazed_terracotta", "#4C4C4C");
        BLOCK_COLORS.put("gray_concrete", "#4C4C4C");
        BLOCK_COLORS.put("gray_concrete_powder", "#4C4C4C");
        BLOCK_COLORS.put("gray_candle", "#4C4C4C");
        BLOCK_COLORS.put("dead_coral_block", "#4C4C4C");
        BLOCK_COLORS.put("dead_coral", "#4C4C4C");
        BLOCK_COLORS.put("dead_coral_fan", "#4C4C4C");
        BLOCK_COLORS.put("tinted_glass", "#4C4C4C");
        BLOCK_COLORS.put("smooth_basalt", "#4C4C4C");

        // COLOR_LIGHT_GRAY: 153, 153, 153 -> #999999
        BLOCK_COLORS.put("light_gray_wool", "#999999");
        //BLOCK_COLORS.put("light_gray_carpet", "#999999");
        BLOCK_COLORS.put("light_gray_shulker_box", "#999999");
        BLOCK_COLORS.put("light_gray_bed", "#999999");
        BLOCK_COLORS.put("light_gray_stained_glass", "#999999");
        BLOCK_COLORS.put("light_gray_glazed_terracotta", "#999999");
        BLOCK_COLORS.put("light_gray_concrete", "#999999");
        BLOCK_COLORS.put("light_gray_concrete_powder", "#999999");
        BLOCK_COLORS.put("light_gray_candle", "#999999");
        BLOCK_COLORS.put("structure_block", "#999999");
        BLOCK_COLORS.put("jigsaw_block", "#999999");
        BLOCK_COLORS.put("chain", "#999999");
        BLOCK_COLORS.put("rail", "#999999");

        // COLOR_CYAN: 76, 127, 153 -> #4C7F99
        BLOCK_COLORS.put("cyan_wool", "#4C7F99");
        //BLOCK_COLORS.put("cyan_carpet", "#4C7F99");
        BLOCK_COLORS.put("cyan_shulker_box", "#4C7F99");
        BLOCK_COLORS.put("cyan_bed", "#4C7F99");
        BLOCK_COLORS.put("cyan_stained_glass", "#4C7F99");
        BLOCK_COLORS.put("cyan_glazed_terracotta", "#4C7F99");
        BLOCK_COLORS.put("cyan_concrete", "#4C7F99");
        BLOCK_COLORS.put("cyan_concrete_powder", "#4C7F99");
        BLOCK_COLORS.put("cyan_candle", "#4C7F99");
        BLOCK_COLORS.put("prismarine", "#4C7F99");
        BLOCK_COLORS.put("warped_roots", "#4C7F99");
        BLOCK_COLORS.put("warped_fungus", "#4C7F99");
        //BLOCK_COLORS.put("twisting_vines", "#4C7F99");
        BLOCK_COLORS.put("sculk_sensor", "#4C7F99");

        // COLOR_PURPLE: 127, 63, 178 -> #7F3FB2
        BLOCK_COLORS.put("purple_wool", "#7F3FB2");
        //BLOCK_COLORS.put("purple_carpet", "#7F3FB2");
        BLOCK_COLORS.put("purple_shulker_box", "#7F3FB2");
        BLOCK_COLORS.put("purple_bed", "#7F3FB2");
        BLOCK_COLORS.put("purple_stained_glass", "#7F3FB2");
        BLOCK_COLORS.put("purple_glazed_terracotta", "#7F3FB2");
        BLOCK_COLORS.put("purple_concrete", "#7F3FB2");
        BLOCK_COLORS.put("purple_concrete_powder", "#7F3FB2");
        BLOCK_COLORS.put("purple_candle", "#7F3FB2");
        BLOCK_COLORS.put("mycelium", "#7F3FB2");
        BLOCK_COLORS.put("chorus_plant", "#7F3FB2");
        BLOCK_COLORS.put("chorus_flower", "#7F3FB2");
        BLOCK_COLORS.put("repeating_command_block", "#7F3FB2");
        BLOCK_COLORS.put("bubble_coral_block", "#7F3FB2");
        BLOCK_COLORS.put("bubble_coral", "#7F3FB2");
        BLOCK_COLORS.put("bubble_coral_fan", "#7F3FB2");
        BLOCK_COLORS.put("amethyst_block", "#7F3FB2");
        BLOCK_COLORS.put("budding_amethyst", "#7F3FB2");
        BLOCK_COLORS.put("amethyst_cluster", "#7F3FB2");
        BLOCK_COLORS.put("amethyst_bud", "#7F3FB2");

        // COLOR_BLUE: 51, 76, 178 -> #334CB2
        BLOCK_COLORS.put("blue_wool", "#334CB2");
        //BLOCK_COLORS.put("blue_carpet", "#334CB2");
        BLOCK_COLORS.put("blue_shulker_box", "#334CB2");
        BLOCK_COLORS.put("blue_bed", "#334CB2");
        BLOCK_COLORS.put("blue_stained_glass", "#334CB2");
        BLOCK_COLORS.put("blue_glazed_terracotta", "#334CB2");
        BLOCK_COLORS.put("blue_concrete", "#334CB2");
        BLOCK_COLORS.put("blue_concrete_powder", "#334CB2");
        BLOCK_COLORS.put("blue_candle", "#334CB2");
        BLOCK_COLORS.put("tube_coral_block", "#334CB2");
        BLOCK_COLORS.put("tube_coral", "#334CB2");
        BLOCK_COLORS.put("tube_coral_fan", "#334CB2");

        // COLOR_BROWN: 102, 76, 51 -> #664C33
        BLOCK_COLORS.put("dark_oak_planks", "#664C33");
        BLOCK_COLORS.put("dark_oak_log", "#664C33");
        BLOCK_COLORS.put("stripped_dark_oak_log", "#664C33");
        BLOCK_COLORS.put("dark_oak_wood", "#664C33");
        BLOCK_COLORS.put("stripped_dark_oak_wood", "#664C33");
        BLOCK_COLORS.put("dark_oak_sign", "#664C33");
        BLOCK_COLORS.put("dark_oak_pressure_plate", "#664C33");
        BLOCK_COLORS.put("dark_oak_trapdoor", "#664C33");
        BLOCK_COLORS.put("dark_oak_stairs", "#664C33");
        BLOCK_COLORS.put("dark_oak_slab", "#664C33");
        BLOCK_COLORS.put("dark_oak_fence_gate", "#664C33");
        BLOCK_COLORS.put("dark_oak_fence", "#664C33");
        BLOCK_COLORS.put("dark_oak_door", "#664C33");
        BLOCK_COLORS.put("spruce_log_side", "#664C33");
        BLOCK_COLORS.put("brown_wool", "#664C33");
        //BLOCK_COLORS.put("brown_carpet", "#664C33");
        BLOCK_COLORS.put("brown_shulker_box", "#664C33");
        BLOCK_COLORS.put("brown_bed", "#664C33");
        BLOCK_COLORS.put("brown_stained_glass", "#664C33");
        BLOCK_COLORS.put("brown_glazed_terracotta", "#664C33");
        BLOCK_COLORS.put("brown_concrete", "#664C33");
        BLOCK_COLORS.put("brown_concrete_powder", "#664C33");
        BLOCK_COLORS.put("brown_candle", "#664C33");
        BLOCK_COLORS.put("soul_sand", "#664C33");
        BLOCK_COLORS.put("command_block", "#664C33");
        BLOCK_COLORS.put("brown_mushroom", "#664C33");
        BLOCK_COLORS.put("soul_soil", "#664C33");
        BLOCK_COLORS.put("mud_brick_slab", "#664C33");

        // COLOR_BLACK: 25, 25, 25 -> #191919
        BLOCK_COLORS.put("black_wool", "#191919");
        //BLOCK_COLORS.put("black_carpet", "#191919");
        BLOCK_COLORS.put("black_shulker_box", "#191919");
        BLOCK_COLORS.put("black_bed", "#191919");
        BLOCK_COLORS.put("black_stained_glass", "#191919");
        BLOCK_COLORS.put("black_glazed_terracotta", "#191919");
        BLOCK_COLORS.put("black_concrete", "#191919");
        BLOCK_COLORS.put("black_concrete_powder", "#191919");
        BLOCK_COLORS.put("black_candle", "#191919");
        BLOCK_COLORS.put("obsidian", "#191919");
        BLOCK_COLORS.put("end_portal", "#191919");
        BLOCK_COLORS.put("dragon_egg", "#191919");
        BLOCK_COLORS.put("coal_block", "#191919");
        BLOCK_COLORS.put("end_gateway", "#191919");
        BLOCK_COLORS.put("basalt", "#191919");
        BLOCK_COLORS.put("polished_basalt", "#191919");
        BLOCK_COLORS.put("smooth_basalt", "#191919");
        BLOCK_COLORS.put("netherite_block", "#191919");
        BLOCK_COLORS.put("ancient_debris", "#191919");
        BLOCK_COLORS.put("crying_obsidian", "#191919");
        BLOCK_COLORS.put("respawn_anchor", "#191919");
        BLOCK_COLORS.put("blackstone", "#191919");
        BLOCK_COLORS.put("gilded_blackstone", "#191919");
        BLOCK_COLORS.put("sculk", "#191919");
        BLOCK_COLORS.put("sculk_vein", "#191919");
        BLOCK_COLORS.put("sculk_catalyst", "#191919");
        BLOCK_COLORS.put("sculk_shrieker", "#191919");

        // GOLD: 250, 238, 77 -> #FAEE4D
        BLOCK_COLORS.put("gold_block", "#FAEE4D");
        BLOCK_COLORS.put("light_weighted_pressure_plate", "#FAEE4D");
        BLOCK_COLORS.put("bell", "#FAEE4D");
        BLOCK_COLORS.put("raw_gold_block", "#FAEE4D");

        // DIAMOND: 92, 219, 213 -> #5CD9D5
        BLOCK_COLORS.put("diamond_block", "#5CD9D5");
        BLOCK_COLORS.put("beacon", "#5CD9D5");
        BLOCK_COLORS.put("prismarine_bricks", "#5CD9D5");
        BLOCK_COLORS.put("dark_prismarine", "#5CD9D5");
        BLOCK_COLORS.put("conduit", "#5CD9D5");

        // LAPIS: 74, 128, 255 -> #4A80FF
        BLOCK_COLORS.put("lapis_block", "#4A80FF");

        // EMERALD: 0, 217, 58 -> #00D93A
        BLOCK_COLORS.put("emerald_block", "#00D93A");

        // PODZOL: 129, 86, 49 -> #815631
        BLOCK_COLORS.put("podzol", "#815631");
        BLOCK_COLORS.put("spruce_planks", "#815631");
        BLOCK_COLORS.put("spruce_log", "#815631");
        BLOCK_COLORS.put("stripped_spruce_log", "#815631");
        BLOCK_COLORS.put("spruce_wood", "#815631");
        BLOCK_COLORS.put("stripped_spruce_wood", "#815631");
        BLOCK_COLORS.put("spruce_sign", "#815631");
        BLOCK_COLORS.put("spruce_pressure_plate", "#815631");
        BLOCK_COLORS.put("spruce_trapdoor", "#815631");
        BLOCK_COLORS.put("spruce_stairs", "#815631");
        BLOCK_COLORS.put("spruce_slab", "#815631");
        BLOCK_COLORS.put("spruce_fence_gate", "#815631");
        BLOCK_COLORS.put("spruce_fence", "#815631");
        BLOCK_COLORS.put("spruce_door", "#815631");
        BLOCK_COLORS.put("oak_log_side", "#815631");
        BLOCK_COLORS.put("jungle_log_side", "#815631");
        BLOCK_COLORS.put("campfire", "#815631");
        //BLOCK_COLORS.put("soul_campfire", "#815631");
        BLOCK_COLORS.put("mangrove_log_side", "#815631");
        BLOCK_COLORS.put("mangrove_roots", "#815631");
        BLOCK_COLORS.put("muddy_mangrove_roots", "#815631");

        // GLOW_LICHEN: 127, 167, 150 -> #7FA796
        BLOCK_COLORS.put("glow_lichen", "#7FA796");
        BLOCK_COLORS.put("verdant_froglight", "#7FA796");

        // FLOWERS
        //BLOCK_COLORS.put("dandelion", "#FFD700");
        //BLOCK_COLORS.put("poppy", "#FF0000");
        //BLOCK_COLORS.put("blue_orchid", "#00C4CC");
        //BLOCK_COLORS.put("allium", "#AB5CC6");
        //BLOCK_COLORS.put("azure_bluet", "#C3C3D1");
        //BLOCK_COLORS.put("tulip", "#E77D5E");
        //BLOCK_COLORS.put("oxeye_daisy", "#E9E9E9");
        //BLOCK_COLORS.put("cornflower", "#6495ED");
        //BLOCK_COLORS.put("lily_of_the_valley", "#F5F5F5");
        //BLOCK_COLORS.put("torchflower", "#FFA500");

        // ADD_BLOCKS
        BLOCK_COLORS.put("netherrack", "#ac2020");
        BLOCK_COLORS.put("nether_sprouts", "#4C7F99");
        BLOCK_COLORS.put("nether_gold_ore", "#4C7F99");
        BLOCK_COLORS.put("magma_block", "#ff7f00");
        BLOCK_COLORS.put("gold_block", "#ffff00");
        BLOCK_COLORS.put("cracked_stone_bricks", "#757d76");
        BLOCK_COLORS.put("mossy_stone_bricks", "#596b59");
        BLOCK_COLORS.put("gold_block", "#e8e10c");

        // MISC
        BLOCK_COLORS.put("bedrock", "#3c3c3c");
        //BLOCK_COLORS.put("grass", "#5fc979");
        //BLOCK_COLORS.put("tall_grass", "#5fc979");
        //BLOCK_COLORS.put("air", "#ffffff"); // Representing air as white (transparent in visualization)
        BLOCK_COLORS.put("default", "#ffffff"); // Default color for unknown blocks
    }

    public static String getColor(String blockType) {
        if (BLOCK_COLORS.containsKey(blockType)) {
            return BLOCK_COLORS.get(blockType);
        }
        for (Map.Entry<String, String> entry : BLOCK_COLORS.entrySet()) {
            if (blockType.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return BLOCK_COLORS.get("default");
    }
}

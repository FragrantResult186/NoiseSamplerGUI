/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package nl.kallestruik.noisesampler.minecraft;

public enum NoiseParameterKey {
    TEMPERATURE("temperature"),
    VEGETATION("vegetation"),
    CONTINENTALNESS("continentalness"),
    EROSION("erosion"),
    TEMPERATURE_LARGE("temperature_large"),
    VEGETATION_LARGE("vegetation_large"),
    CONTINENTALNESS_LARGE("continentalness_large"),
    EROSION_LARGE("erosion_large"),
    RIDGE("ridge"),
    OFFSET("offset"),
    AQUIFER_BARRIER("aquifer_barrier"),
    AQUIFER_FLUID_LEVEL_FLOODEDNESS("aquifer_fluid_level_floodedness"),
    AQUIFER_LAVA("aquifer_lava"),
    AQUIFER_FLUID_LEVEL_SPREAD("aquifer_fluid_level_spread"),
    PILLAR("pillar"),
    PILLAR_RARENESS("pillar_rareness"),
    PILLAR_THICKNESS("pillar_thickness"),
    SPAGHETTI_2D("spaghetti_2d"),
    SPAGHETTI_2D_ELEVATION("spaghetti_2d_elevation"),
    SPAGHETTI_2D_MODULATOR("spaghetti_2d_modulator"),
    SPAGHETTI_2D_THICKNESS("spaghetti_2d_thickness"),
    SPAGHETTI_3D_1("spaghetti_3d_1"),
    SPAGHETTI_3D_2("spaghetti_3d_2"),
    SPAGHETTI_3D_RARITY("spaghetti_3d_rarity"),
    SPAGHETTI_3D_THICKNESS("spaghetti_3d_thickness"),
    SPAGHETTI_ROUGHNESS("spaghetti_roughness"),
    SPAGHETTI_ROUGHNESS_MODULATOR("spaghetti_roughness_modulator"),
    CAVE_ENTRANCE("cave_entrance"),
    CAVE_LAYER("cave_layer"),
    CAVE_CHEESE("cave_cheese"),
    ORE_VEININESS("ore_veininess"),
    ORE_VEIN_A("ore_vein_a"),
    ORE_VEIN_B("ore_vein_b"),
    ORE_GAP("ore_gap"),
    NOODLE("noodle"),
    NOODLE_THICKNESS("noodle_thickness"),
    NOODLE_RIDGE_A("noodle_ridge_a"),
    NOODLE_RIDGE_B("noodle_ridge_b"),
    JAGGED("jagged"),
    SURFACE("surface"),
    SURFACE_SECONDARY("surface_secondary"),
    CLAY_BANDS_OFFSET("clay_bands_offset"),
    BADLANDS_PILLAR("badlands_pillar"),
    BADLANDS_PILLAR_ROOF("badlands_pillar_roof"),
    BADLANDS_SURFACE("badlands_surface"),
    ICEBERG_PILLAR("iceberg_pillar"),
    ICEBERG_PILLAR_ROOF("iceberg_pillar_roof"),
    ICEBERG_SURFACE("iceberg_surface"),
    SURFACE_SWAMP("surface_swamp"),
    CALCITE("calcite"),
    GRAVEL("gravel"),
    POWDER_SNOW("powder_snow"),
    PACKED_ICE("packed_ice"),
    ICE("ice"),
    SOUL_SAND_LAYER("soul_sand_layer"),
    GRAVEL_LAYER("gravel_layer"),
    PATCH("patch"),
    NETHERRACK("netherrack"),
    NETHER_WART("nether_wart"),
    NETHER_STATE_SELECTOR("nether_state_selector");

    public String value;

    NoiseParameterKey(String value) {
        this.value = value;
    }
}


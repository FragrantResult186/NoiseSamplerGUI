package nl.jellejurre.seedchecker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.*;
import nl.jellejurre.seedchecker.ReflectionUtils;
import nl.jellejurre.seedchecker.SeedCheckerDimension;
import nl.jellejurre.seedchecker.SeedCheckerSettings;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.datafixer.Schemas;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import nl.jellejurre.seedchecker.serverMocks.FakeLevelStorage;
import nl.jellejurre.seedchecker.serverMocks.FakeLightingProvider;
import nl.jellejurre.seedchecker.serverMocks.FakeSaveProperties;
import nl.jellejurre.seedchecker.serverMocks.FakeServerWorld;
import nl.jellejurre.seedchecker.serverMocks.FakeThreadedAnvilChunkStorage;

public class SeedChunkGenerator {
    private final ConcurrentHashMap<ChunkPos, ProtoChunk> chunkMap = new ConcurrentHashMap<>();
    private BiomeSource biomeSource;
    private SeedCheckerDimension dimension;
    public ChunkGenerator chunkGenerator;
    private FakeLevelStorage levelStorage;
    private StructureManager structureManager;
    public FakeServerWorld fakeServerWorld;
    private LightingProvider fakeLightingProvider;
    private DynamicRegistryManager.Impl registryManager;
    private ResourcePackManager resourcePackManager;
    private ServerResourceManager serverResourceManager;
    private ResourceManager resourceManager;
    private FakeLevelStorage.FakeSession session;
    private FakeSaveProperties saveProperties;

    private long seed;
    private int targetLevel;
    private boolean createLight;

    public SeedChunkGenerator(long seed, int targetLevel, SeedCheckerDimension dimension, boolean createLight) {
        this.seed = seed;
        this.dimension = dimension;
        this.targetLevel = targetLevel;
        this.createLight = createLight;
        registryManager = SeedCheckerSettings.registryManager;
        resourcePackManager = SeedCheckerSettings.resourcePackManager;
        serverResourceManager = SeedCheckerSettings.serverResourceManager;
        resourceManager = SeedCheckerSettings.resourceManager;
        saveProperties = new FakeSaveProperties(registryManager, seed);
        levelStorage = FakeLevelStorage.create(Path.of(""));
        try {
            session = levelStorage.createSession();
            structureManager = new StructureManager(resourceManager,
                    session,
                    Schemas.getFixer());
        } catch (IOException e) {
            System.out.println("Couldn't instantiate Structuremanager");
            e.printStackTrace();
        }
        switch (dimension) {
            case OVERWORLD -> initOverworld();
            case NETHER -> initNether();
            case END -> initEnd();
        }
        biomeSource = chunkGenerator.getBiomeSource();
        if (createLight) {
            FakeThreadedAnvilChunkStorage c = new FakeThreadedAnvilChunkStorage(fakeServerWorld, session,
                    Schemas.getFixer(),
                    fakeServerWorld.getChunkManager());
            fakeLightingProvider = c.getLightingProvider();
        } else {
            fakeLightingProvider = new FakeLightingProvider(fakeServerWorld.getChunkManager(), false, false);
        }
    }

    private void initOverworld() {
        GeneratorOptions options = saveProperties.getGeneratorOptions();
        DimensionOptions dimensionOptions = options.getDimensions().get(DimensionOptions.OVERWORLD);
        chunkGenerator = dimensionOptions.getChunkGenerator();
        fakeServerWorld = FakeServerWorld.create(registryManager, World.OVERWORLD,
                registryManager.get(Registry.DIMENSION_TYPE_KEY)
                        .getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY),
                seed,
                resourcePackManager, saveProperties, chunkGenerator, serverResourceManager, this, session);
    }

    private void initNether() {
        GeneratorOptions options = saveProperties.getGeneratorOptions();
        DimensionOptions dimensionOptions = options.getDimensions().get(DimensionOptions.NETHER);
        chunkGenerator = dimensionOptions.getChunkGenerator();

        fakeServerWorld = FakeServerWorld.create(registryManager, World.NETHER,
                registryManager.get(Registry.DIMENSION_TYPE_KEY)
                        .getOrThrow(DimensionType.THE_NETHER_REGISTRY_KEY),
                seed,
                resourcePackManager, saveProperties, chunkGenerator, serverResourceManager, this, session);
    }

    private void initEnd() {
        GeneratorOptions options = saveProperties.getGeneratorOptions();
        DimensionOptions dimensionOptions = options.getDimensions().get(DimensionOptions.END);
        chunkGenerator = dimensionOptions.getChunkGenerator();

        DimensionType endDimension = registryManager.get(Registry.DIMENSION_TYPE_KEY)
                .getOrThrow(DimensionType.THE_END_REGISTRY_KEY);
        nl.jellejurre.seedchecker.ReflectionUtils.setValueOfField(endDimension, "field_24764", "hasEnderDragonFight",
                false);
        fakeServerWorld = FakeServerWorld.create(registryManager, World.END,
                endDimension, seed,
                resourcePackManager, saveProperties, chunkGenerator, serverResourceManager, this, session);
    }

    public BlockPos getSpawnPos() {
        BlockPos finalSpawnPoint;
        ChunkGenerator lv = fakeServerWorld.getChunkManager().getChunkGenerator();
        ChunkPos lv2 = new ChunkPos(lv.getMultiNoiseSampler().findBestSpawnPosition());
        int i = lv.getSpawnHeight(fakeServerWorld);
        if (i < fakeServerWorld.getBottomY()) {
            BlockPos lv3 = lv2.getStartPos();
            i = fakeServerWorld.getTopY(Heightmap.Type.WORLD_SURFACE, lv3.getX() + 8, lv3.getZ() + 8);
        }
        finalSpawnPoint = lv2.getStartPos().add(8, i, 8); // SPAWN POS
        int lv3 = 0;
        int j = 0;
        int k = 0;
        int l = -1;
        int m = 5;
        for (int n = 0; n < MathHelper.square(11); ++n) {
            BlockPos lv4;
            if (lv3 >= -5 && lv3 <= 5 && j >= -5 && j <= 5 && (lv4 = SpawnLocating.findServerSpawnPoint(fakeServerWorld,
                    new ChunkPos(lv2.x + lv3, lv2.z + j))) != null) {
                finalSpawnPoint = lv4; // SPAWN POS
                break;
            }
            if (lv3 == j || lv3 < 0 && lv3 == -j || lv3 > 0 && lv3 == 1 - j) {
                int lv42 = k;
                k = -l;
                l = lv42;
            }
            lv3 += k;
            j += l;
        }
        BlockPos returnPos = finalSpawnPoint.add(-8, 8, -8);

        return returnPos;
    }

    public Block getBlock(int x, int y, int z) {
        return getBlockState(x, y, z).getBlock();
    }

    public Block getBlock(BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState getBlockState(int x, int y, int z) {
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        ProtoChunk chunk = chunkMap.get(pos);
        if (chunk == null) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld,
                    fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), null);
        }
        if (chunk.getStatus().getIndex() < targetLevel) {
            chunk = getOrBuildChunk(pos.x, pos.z);
        }
        chunkMap.put(pos, chunk);
        return chunk.getBlockState(
                new BlockPos(x, y, z));
    }

    public BlockState getBlockState(BlockPos pos) {
        return getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockEntity getBlockEntity(int x, int y, int z) {
        if (targetLevel < 8) {
            throw new IllegalStateException(
                    "Tried to generate blockentities without structure generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 8 or higher.");
        }
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        ProtoChunk chunk = chunkMap.get(pos);
        if (chunk == null) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld,
                    fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), null);
        }
        if (chunk.getStatus().getIndex() < targetLevel) {
            chunk = getOrBuildChunk(pos.x, pos.z);
        }
        chunkMap.put(pos, chunk);
        return chunk.getBlockEntity(
                new BlockPos(x, y, z));
    }

    public BlockEntity getBlockEntity(BlockPos pos) {
        return getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    public FluidState getFluidState(int x, int y, int z) {
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        ProtoChunk chunk = chunkMap.get(pos);
        if (chunk == null) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld,
                    fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), null);
        }
        if (chunk.getStatus().getIndex() < targetLevel) {
            chunk = getOrBuildChunk(pos.x, pos.z);
        }
        chunkMap.put(pos, chunk);
        return chunk.getFluidState(
                new BlockPos(x, y, z));
    }

    public FluidState getFluidState(BlockPos pos) {
        return getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    public List<ItemStack> generateChestLoot(int x, int y, int z) {
        ChestBlockEntity chest = (ChestBlockEntity) getBlockEntity(x, y, z);
        if (chest == null) {
            return new ArrayList<>();
        }
        Identifier lootTableId = (Identifier) nl.jellejurre.seedchecker.ReflectionUtils.getValueFromField(chest,
                "field_12037", "lootTableId");
        long lootTableSeed = (long) ReflectionUtils.getValueFromField(chest, "field_12036", "lootTableSeed");
        LootTable lootTable = serverResourceManager.getLootManager().getTable(lootTableId);
        LootContext.Builder lootContextBuilder = new LootContext.Builder(fakeServerWorld).parameter(
                LootContextParameters.ORIGIN, Vec3d.ofCenter(chest.getPos())).random(lootTableSeed);
        return lootTable.generateLoot(lootContextBuilder.build(LootContextTypes.CHEST));
    }

    public List<ItemStack> generateChestLoot(BlockPos pos) {
        return generateChestLoot(pos.getX(), pos.getY(), pos.getZ());
    }

    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        ProtoChunk chunk;

        if (!chunkMap.containsKey(pos)) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld,
                    fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), null);
            chunkMap.put(pos, chunk);
        } else {
            chunk = chunkMap.get(pos);
        }

        generateChunkUntil(chunk, targetLevel, true);

        return chunk;
    }

    public ProtoChunk getOrBuildChunk(ChunkPos pos) {
        return getOrBuildChunk(pos.x, pos.z);
    }

    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ, int targetLevel) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        ProtoChunk chunk;

        if (!chunkMap.containsKey(pos)) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld,
                    fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), null);
            chunkMap.put(pos, chunk);
        } else {
            chunk = chunkMap.get(pos);
        }

        generateChunkUntil(chunk, targetLevel, true);

        return chunk;
    }

    public ProtoChunk getOrBuildChunk(ChunkPos pos, int targetLevel) {
        return getOrBuildChunk(pos.x, pos.z, targetLevel);
    }

    private void generateChunkUntil(ProtoChunk chunk, int target, boolean generateOthers) {
        if (target >= 1)
            STRUCTURE_STARTS(chunk);
        if (target >= 2) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 2);
            STRUCTURE_REFERENCES(chunk, chunks);
        }
        if (target >= 3) {
            BIOMES(chunk);
        }
        if (target >= 4) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 4);
            NOISE(chunk, chunks);
        }
        if (target >= 5) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 5);
            SURFACE(chunk, chunks);
        }
        if (target >= 6) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 6);
            CARVERS(chunk, chunks);
        }
        if (target >= 7)
            LIQUID_CARVERS(chunk);
        if (target >= 8) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 8);
            FEATURES(chunk, chunks, generateOthers);
        }
        if (target >= 9 && dimension == SeedCheckerDimension.OVERWORLD) {
            LIGHT(chunk);
        }
        if (target >= 10) {
            SPAWN(chunk);
        }
    }

    private List<Chunk> getRegion(ChunkPos center, int radius, int centerTargetLevel) {
        centerTargetLevel -= 1;

        List<Chunk> chunks = new ArrayList<>();
        int centerX = center.x;
        int centerZ = center.z;

        for (int z = -radius; z <= radius; z++) {
            for (int x = -radius; x <= radius; x++) {
                int distanceFromCenter = Math.max(Math.abs(z), Math.abs(x));
                int chunkTargetLevel = Math.max(0, centerTargetLevel - distanceFromCenter);

                ChunkPos pos = new ChunkPos(centerX + x, centerZ + z);
                ProtoChunk chunk;

                if (!chunkMap.containsKey(pos)) {
                    chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld,
                            fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), null);
                    STRUCTURE_STARTS(chunk);
                    chunkMap.put(pos, chunk);
                } else {
                    chunk = chunkMap.get(pos);
                }

                chunks.add(chunk);

                if (!pos.equals(center) && chunkTargetLevel != 0 &&
                        chunkTargetLevel > chunk.getStatus().getIndex())
                    generateChunkUntil(chunk, chunkTargetLevel, true);
            }
        }

        return chunks;
    }

    private void STRUCTURE_STARTS(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.STRUCTURE_STARTS))
            return;
        chunkGenerator.setStructureStarts(fakeServerWorld.getRegistryManager(),
                fakeServerWorld.getStructureAccessor(), chunk, structureManager, seed);
        chunk.setStatus(ChunkStatus.STRUCTURE_STARTS);
    }

    private void STRUCTURE_REFERENCES(ProtoChunk chunk, List<Chunk> chunks) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.STRUCTURE_REFERENCES))
            return;
        ChunkRegion region = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.STRUCTURE_REFERENCES, -1);
        chunkGenerator.addStructureReferences(region,
                fakeServerWorld.getStructureAccessor().forRegion(region), chunk);
        chunk.setStatus(ChunkStatus.STRUCTURE_REFERENCES);
    }

    private void BIOMES(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.BIOMES))
            return;

        chunk.populateBiomes(chunkGenerator.getBiomeSource()::getBiome, chunkGenerator.getMultiNoiseSampler());

        chunk.setStatus(ChunkStatus.BIOMES);
    }

    private void NOISE(ProtoChunk chunk, List<Chunk> chunks) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.NOISE))
            return;

        ChunkRegion region = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.NOISE, 0);
        try {
            chunkGenerator
                    .populateNoise(Util.getMainWorkerExecutor(), Blender.getNoBlending(),
                            fakeServerWorld.getStructureAccessor().forRegion(region), chunk)
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error while generating chunk " + chunk.getPos() + " with seed " + seed);
            e.printStackTrace();
        }
        chunk.setStatus(ChunkStatus.NOISE);
    }

    private void SURFACE(ProtoChunk chunk, List<Chunk> chunks) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.SURFACE))
            return;
        try {
            ChunkRegion region = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.NOISE, 0);
            chunkGenerator.buildSurface(region, fakeServerWorld.getStructureAccessor().forRegion(region), chunk);
        } catch (NullPointerException ignored) {
        }
        chunk.setStatus(ChunkStatus.SURFACE);
    }

    private void CARVERS(ProtoChunk chunk, List<Chunk> chunks) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.CARVERS))
            return;
        ChunkRegion region = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.CARVERS, 0);
        chunkGenerator
                .carve(region, seed, fakeServerWorld.getBiomeAccess(),
                        fakeServerWorld.getStructureAccessor().forRegion(region), chunk, GenerationStep.Carver.AIR);
        chunk.setStatus(ChunkStatus.CARVERS);
    }

    private void LIQUID_CARVERS(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.LIQUID_CARVERS))
            return;
        // Mojang seems to have removed this one. Keeping it for backwards compatibility
        // just like them.
        chunk.setStatus(ChunkStatus.LIQUID_CARVERS);
    }

    private void FEATURES(ProtoChunk chunk, List<Chunk> chunks, boolean generateOthers) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.FEATURES))
            return;

        chunk.setLightingProvider(fakeLightingProvider);
        Heightmap.populateHeightmaps(chunk, EnumSet
                .of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
        ChunkRegion region = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.FEATURES, 1);
        chunkGenerator.generateFeatures(region, chunk,
                fakeServerWorld.getStructureAccessor().forRegion(region));
        chunk.setStatus(ChunkStatus.FEATURES);

        for (LongSet chunkPosLongs : chunk.getStructureReferences().values()) {
            for (Long chunkPosLong : chunkPosLongs) {
                ChunkPos pos = new ChunkPos(chunkPosLong);
                if (pos.equals(chunk.getPos()))
                    continue;

                generateChunkUntil(chunkMap.get(pos), 8, false);
            }
        }

        if (generateOthers) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    generateChunkUntil(
                            chunkMap.get(new ChunkPos(chunk.getPos().x + x, chunk.getPos().z + z)), 8,
                            false);
                }
            }
        }
    }

    private void LIGHT(ProtoChunk chunk) {
        if (!createLight) {
            throw new IllegalStateException(
                    "Tried to generate light without light generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 9 or higher.");
        }
        if (chunk.getStatus().isAtLeast(ChunkStatus.LIGHT))
            return;
        boolean bl = chunk.getStatus().isAtLeast(ChunkStatus.LIGHT) && chunk.isLightOn();
        if (!chunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            chunk.setStatus(ChunkStatus.LIGHT);
        }
        ((ServerLightingProvider) fakeLightingProvider).light(chunk, bl).thenApply(Either::left);
        chunk.setStatus(ChunkStatus.LIGHT);
    }

    private void SPAWN(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.SPAWN))
            return;
        chunkGenerator
                .populateEntities(new ChunkRegion(fakeServerWorld, ImmutableList.of(chunk), ChunkStatus.SPAWN, -1));
        chunk.setStatus(ChunkStatus.FULL);
    }

    public List<NbtCompound> getEntitiesInBox(String name, Box box, Predicate<NbtCompound> predicate) {
        if (targetLevel < 10) {
            throw new IllegalStateException(
                    "Tried to generate entities without entity generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 10 or higher.");
        }
        int chunkCountx = (int) Math.ceil(box.getXLength() / 16);
        int chunkCountz = (int) Math.ceil(box.getZLength() / 16);
        List<NbtCompound> list = Lists.newArrayList();
        for (int x = (int) box.minX >> 4; x < ((int) box.minX >> 4) + chunkCountx; x++) {
            for (int z = (int) box.minZ >> 4; z < ((int) box.minZ >> 4) + chunkCountz; z++) {
                ProtoChunk chunk = getOrBuildChunk(x, z);
                for (NbtCompound nbtCompound : chunk.getEntities()) {
                    if (nbtCompound.get("id").asString().toLowerCase().contains(name.toLowerCase())) {
                        NbtList s = (NbtList) nbtCompound.get("Pos");
                        double mobx = s.getDouble(0);
                        double moby = s.getDouble(1);
                        double mobz = s.getDouble(2);
                        if (box.contains(new Vec3d(mobx, moby, mobz)) && predicate.test(nbtCompound)) {
                            list.add(nbtCompound);
                        }
                    }
                }
            }
        }
        return list;
    }

    public List<NbtCompound> getEntitiesInBox(String name, Box box) {
        return this.getEntitiesInBox(name, box, compound -> true);
    }

    public List<NbtCompound> getEntitiesInBox(Box box) {
        return getEntitiesInBox("", box);
    }

    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box,
            Predicate<BlockEntity> predicate) {
        if (targetLevel < 8) {
            throw new IllegalStateException(
                    "Tried to generate blockentities without structure generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 8 or higher.");
        }
        // Make both inclusive
        Box checkBox = new Box(box.minX, box.minY, box.minZ, box.maxX + 1, box.maxY + 1, box.maxZ + 1);
        Map<BlockPos, BlockEntity> map = new HashMap<>();
        int chunkCountx = (int) Math.ceil(box.getXLength() / 16);
        int chunkCountz = (int) Math.ceil(box.getZLength() / 16);
        for (int x = (int) box.minX >> 4; x < ((int) box.minX >> 4) + chunkCountx; x++) {
            for (int z = (int) box.minZ >> 4; z < ((int) box.minZ >> 4) + chunkCountz; z++) {
                ProtoChunk chunk = getOrBuildChunk(x, z);
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (type == null || blockEntity.getType() == type) {
                        BlockPos pos = blockEntity.getPos();
                        if (checkBox.contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()))
                                && predicate.test(blockEntity)) {
                            map.put(blockEntity.getPos(), blockEntity);
                        }
                    }
                }
            }
        }
        return map;
    }

    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box) {
        return getBlockEntitiesInBox(type, box, blockEntity -> true);
    }

    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(Box box) {
        return getBlockEntitiesInBox(null, box);
    }

    public int getBlockCountInBox(Block block, Box box) {
        if (targetLevel < 7) {
            throw new IllegalStateException(
                    "Tried to generate blocks without block generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 7 or higher.");
        }
        int count = 0;
        int chunkCountx = (int) Math.ceil(box.getXLength() / 16);
        int chunkCountz = (int) Math.ceil(box.getZLength() / 16);
        for (int x = (int) box.minX >> 4; x < ((int) box.minX >> 4) + chunkCountx; x++) {
            for (int z = (int) box.minZ >> 4; z < ((int) box.minZ >> 4) + chunkCountz; z++) {
                ProtoChunk c = getOrBuildChunk(x, z);
                for (int x2 = 0; x2 < 16; x2++) {
                    for (int z2 = 0; z2 < 16; z2++) {
                        for (int y = (int) box.minY; y < (int) box.maxY; y++) {
                            BlockPos pos = new BlockPos((x << 4) + x2, y, (z << 4) + z2);
                            if (box.contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()))) {
                                if (c.getBlockState(pos).getBlock() == block) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public void printArea(Box box) {
        Direction.Axis xa = Direction.Axis.X;
        Direction.Axis ya = Direction.Axis.Y;
        Direction.Axis za = Direction.Axis.Z;
        for (int y = (int) box.getMin(ya); y <= box.getMax(ya); y++) {
            for (int x = (int) box.getMin(xa); x <= box.getMax(xa); x++) {
                for (int z = (int) box.getMax(za); z >= box.getMin(za); z--) {
                    System.out.print("[" + StringUtils.leftPad(Integer.toString(x), 3, "0") + ","
                            + StringUtils.leftPad(Integer.toString(y), 3, "0") + ","
                            + StringUtils.leftPad(Integer.toString(z), 3, "0") + "] "
                            + String.format("%1$-20s", getBlockState(
                                    x, y, z).getBlock().getTranslationKey().substring(16)));
                }
                System.out.println();
            }
        }
    }

    public void clearMemory() {
        for (ChunkPos pos : chunkMap.keySet()) {
            ProtoChunk chunk = chunkMap.get(pos);
            chunk = null;
            chunkMap.remove(pos);
        }
        Runtime.getRuntime().gc();
    }
}
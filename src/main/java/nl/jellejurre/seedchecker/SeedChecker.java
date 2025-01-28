package nl.jellejurre.seedchecker;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ProtoChunk;
import nl.jellejurre.seedchecker.SeedCheckerDimension;
import nl.jellejurre.seedchecker.SeedCheckerSettings;
import nl.jellejurre.seedchecker.SeedChunkGenerator;
import nl.jellejurre.seedchecker.TargetState;

public class SeedChecker {
    public SeedChunkGenerator seedChunkGenerator;
    private long seed;
    private int targetLevel;
    private boolean createLight;

    public SeedChecker(long seed, int targetLevel, SeedCheckerDimension dimension) {
        if(Runtime.getRuntime().freeMemory()<50000){
            try {
                Thread.sleep(5000);
            }catch (Exception e){

            }
        }
        SeedCheckerSettings.initialise();
        this.seed = seed;
        this.targetLevel = targetLevel;
        this.createLight = targetLevel >= 9;
        this.seedChunkGenerator = new SeedChunkGenerator(seed, targetLevel, dimension, createLight);
    }
    public SeedChecker(long seed, TargetState state, SeedCheckerDimension dimension) {
        this(seed, state.getLevel(), dimension);
    }
    public SeedChecker(long seed, int state){
        this(seed, state, SeedCheckerDimension.OVERWORLD);
    }
    public SeedChecker(long seed, TargetState state){
        this(seed, state.getLevel());
    }
    public SeedChecker(long seed, SeedCheckerDimension dimension) {
        this(seed, 12, dimension);
    }
    public SeedChecker(long seed) {
        this(seed, 12, SeedCheckerDimension.OVERWORLD);
    }
    public BlockPos getSpawnPos(){
        return this.seedChunkGenerator.getSpawnPos();
    }
    public Block getBlock(int x, int y, int z){
        return seedChunkGenerator.getBlock(x, y, z);
    }
    public Block getBlock(BlockPos pos){
        return seedChunkGenerator.getBlock(pos);
    }
    public BlockState getBlockState(int x, int y, int z){
        return seedChunkGenerator.getBlockState(x, y, z);
    }
    public BlockState getBlockState(BlockPos pos){
        return seedChunkGenerator.getBlockState(pos);
    }
    public BlockEntity getBlockEntity(int x, int y, int z){
        return seedChunkGenerator.getBlockEntity(x, y, z);
    }
    public BlockEntity getBlockEntity(BlockPos pos){
        return seedChunkGenerator.getBlockEntity(pos);
    }
    public FluidState getFluidState(int x, int y, int z){
        return seedChunkGenerator.getFluidState(x, y, z);
    }
    public FluidState getFluidState(BlockPos pos){
        return seedChunkGenerator.getFluidState(pos);
    }
    public List<ItemStack> generateChestLoot(int x, int y, int z){
        return seedChunkGenerator.generateChestLoot(x, y, z);
    }
    public List<ItemStack> generateChestLoot(BlockPos pos){
        return seedChunkGenerator.generateChestLoot(pos);
    }
    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ);
    }
    public ProtoChunk getOrBuildChunk(ChunkPos pos){
        return seedChunkGenerator.getOrBuildChunk(pos.x, pos.z);
    }
    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ, targetLevel);
    }
    public ProtoChunk getOrBuildChunk(ChunkPos pos, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(pos, targetLevel);
    }
    public List<NbtCompound> getEntitiesInBox(String name, Box box, Predicate<NbtCompound> predicate) {
        return seedChunkGenerator.getEntitiesInBox(name, box ,predicate);
    }
    public List<NbtCompound> getEntitiesInBox(String name, Box box) {
        return seedChunkGenerator.getEntitiesInBox(name, box);
    }
    public List<NbtCompound> getEntitiesInBox(Box box) {
        return seedChunkGenerator.getEntitiesInBox(box);
    }
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(Box box) {
        return seedChunkGenerator.getBlockEntitiesInBox(box);
    }
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box){
        return seedChunkGenerator.getBlockEntitiesInBox(type, box);
    }
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box, Predicate<BlockEntity> predicate) {
        return seedChunkGenerator.getBlockEntitiesInBox(type, box, predicate);
    }
    public int getBlockCountInBox(Block block, Box box){
        return seedChunkGenerator.getBlockCountInBox(block, box);
    }
    public void printArea(Box box) {
        seedChunkGenerator.printArea(box);
    }
    public ProtoChunk getChunk(int chunkX, int chunkZ){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ);
    }
    public ProtoChunk getChunk(ChunkPos pos){
        return seedChunkGenerator.getOrBuildChunk(pos);
    }
    public ProtoChunk getChunk(int chunkX, int chunkZ, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ, targetLevel);
    }
    public ProtoChunk getChunk(ChunkPos pos, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(pos, targetLevel);
    }
    public ProtoChunk getChunk(BlockPos pos) {
        return getChunk(pos.getX() % 16, pos.getZ() % 16);
    }
    public void clearMemory(){
        seedChunkGenerator.clearMemory();
    }
    public long getSeed() {
        return seed;
    }
    public long getTargetLevel() {
        return targetLevel;
    }
}

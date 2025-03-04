package fragrant.components.mapviewer.model;

import net.minecraft.block.Block;

public class ChunkData {
    private final Block[][][] blocks;
    private final long timestamp;
    private static final long EXPIRATION_TIME = 30000;

    public ChunkData(Block[][][] blocks) {
        this.blocks = blocks;
        this.timestamp = System.currentTimeMillis();
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > EXPIRATION_TIME;
    }
}

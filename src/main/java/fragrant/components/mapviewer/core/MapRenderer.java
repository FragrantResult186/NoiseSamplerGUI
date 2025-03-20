package fragrant.components.mapviewer.core;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import fragrant.components.mapviewer.model.BlockInfo;
import fragrant.components.mapviewer.model.ChunkData;
import fragrant.components.mapviewer.model.ChunkKey;
import nl.jellejurre.seedchecker.SeedChecker;
import nl.jellejurre.seedchecker.SeedCheckerDimension;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MapRenderer {
    private final SeedChecker seedChecker;
    private int blockSize;
    private int depthRange;
    private final Map<ChunkKey, ChunkData> chunkCache;
    private final Object blockAccessLock;
    private BlockPos spawnPos;
    private boolean showNoiseOverlay;
    private nl.kallestruik.noisesampler.NoiseType selectedNoiseType;
    private nl.kallestruik.noisesampler.NoiseSampler noiseSampler;
    private static final int HIGHLIGHT_PADDING = 2;
    protected SeedCheckerDimension currentDimension = SeedCheckerDimension.OVERWORLD;
    private final JPanel mapPanel;

    public MapRenderer(SeedChecker seedChecker, int blockSize, int depthRange, Map<ChunkKey, ChunkData> chunkCache, Object blockAccessLock, JPanel mapPanel) {
        this.seedChecker = seedChecker;
        this.blockSize = blockSize;
        this.depthRange = depthRange;
        this.chunkCache = chunkCache;
        this.blockAccessLock = blockAccessLock;
        this.mapPanel = mapPanel;
    }

    public void setNoiseSampler(nl.kallestruik.noisesampler.NoiseSampler noiseSampler) {
        this.noiseSampler = noiseSampler;
    }

    public void setSpawnPos(BlockPos spawnPos) {
        this.spawnPos = spawnPos;
    }

    public void setNoiseSettings(boolean showNoiseOverlay, nl.kallestruik.noisesampler.NoiseType selectedNoiseType) {
        this.showNoiseOverlay = showNoiseOverlay;
        this.selectedNoiseType = selectedNoiseType;
    }

    public void drawMap(Graphics2D g2d, int mapWidth, int mapHeight, int centerX, int centerY, int centerZ) {
        synchronized (blockAccessLock) {
            if (seedChecker == null) {
                g2d.drawString("No seed loaded", 10, 20);
                return;
            }

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int viewWidth = Math.max(1, mapWidth / blockSize);
            int viewHeight = mapHeight / blockSize;

            int startChunkX = (centerX - viewWidth / 2) >> 4;
            int endChunkX = (centerX + viewWidth / 2) >> 4;
            int startChunkY = (centerY - viewHeight / 2) >> 4;
            int endChunkY = (centerY + viewHeight / 2) >> 4;
            int startChunkZ = (centerZ - depthRange) >> 4;
            int endChunkZ = centerZ >> 4;

            BlockInfo[][] visibilityMap = new BlockInfo[viewWidth][viewHeight];

            synchronized (blockAccessLock) {
                for (int chunkZ = endChunkZ; chunkZ >= startChunkZ; chunkZ--) {
                    for (int chunkX = startChunkX; chunkX <= endChunkX; chunkX++) {
                        for (int chunkY = startChunkY; chunkY <= endChunkY; chunkY++) {
                            Block[][][] chunkBlocks = getChunkData(chunkX, chunkY, chunkZ);
                            drawChunk(g2d, chunkX, chunkY, chunkZ, chunkBlocks, visibilityMap, viewWidth, viewHeight, centerX, centerY, centerZ);
                        }
                    }
                }
            }

            drawSpawnPoint(g2d, viewWidth, viewHeight, visibilityMap, centerX, centerY, centerZ, mapHeight);
        }
    }

    private void drawSpawnPoint(Graphics2D g2d, int viewWidth, int viewHeight, BlockInfo[][] visibilityMap, int centerX, int centerY, int centerZ, int mapPanel) {
        if (spawnPos == null || currentDimension == SeedCheckerDimension.END) return;

        int spawnX = spawnPos.getX();
        int spawnY = spawnPos.getY();
        int spawnZ = spawnPos.getZ();

        if (spawnX < centerX - viewWidth / 2 || spawnX > centerX + viewWidth / 2 || spawnY < centerY - viewHeight / 2 || spawnY > centerY + viewHeight / 2 || spawnZ < centerZ - depthRange || spawnZ > centerZ) {
            return;
        }

        int spawnScreenX = ((spawnX - centerX) + viewWidth / 2) * blockSize;
        int spawnScreenY = mapPanel - ((spawnY - centerY) + viewHeight / 2) * blockSize;

        int depth = centerZ - spawnZ;
        double normalizedDepth = (double) depth / depthRange;
        float opacity = (float) (1.0 - (normalizedDepth * 0.8));
        opacity = Math.max(0.2f, opacity);

        int mapX = spawnX - centerX + viewWidth / 2;
        int mapY = spawnY - centerY + viewHeight / 2;

        if (mapX >= 0 && mapX < visibilityMap.length && mapY >= 0 && mapY < visibilityMap[0].length) {
            BlockInfo blockInfo = visibilityMap[mapX][mapY];
            if (blockInfo == null || blockInfo.depth() > depth) {

                drawSpawnMarker(g2d, spawnScreenX, spawnScreenY, opacity);

                drawSpawnLabel(g2d, spawnScreenX, spawnScreenY - blockSize * 2, opacity);
            }
        }
    }

    private void drawSpawnMarker(Graphics2D g2d, int x, int y, float opacity) {
        int markerSize = Math.max(blockSize * 2, 16);

        int glowSize = markerSize + (blockSize / 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity * 0.7f));

        RadialGradientPaint glow = new RadialGradientPaint(x, y, glowSize, new float[]{0.0f, 0.7f, 1.0f}, new Color[]{new Color(255, 128, 128, (int) (60 * opacity)), new Color(255, 128, 128, (int) (30 * opacity)), new Color(255, 128, 128, 0)});
        g2d.setPaint(glow);
        g2d.fillOval(x - glowSize / 2, y - glowSize / 2, glowSize, glowSize);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.setColor(new Color(255, 128, 128));
        g2d.setStroke(new BasicStroke(Math.max(1.5f, blockSize * 0.15f)));

        int innerCircleSize = markerSize / 2;
        g2d.drawOval(x - innerCircleSize / 2, y - innerCircleSize / 2, innerCircleSize, innerCircleSize);
        g2d.drawOval(x - markerSize / 2, y - markerSize / 2, markerSize, markerSize);
    }

    private void drawSpawnLabel(Graphics2D g2d, int x, int y, float opacity) {
        String text = "SPAWN";

        float fontSize = Math.max(10f, Math.min(blockSize * 1.5f, 24f));
        Font labelFont = new Font("SansSerif", Font.BOLD, (int) fontSize);
        g2d.setFont(labelFont);

        FontMetrics metrics = g2d.getFontMetrics(labelFont);
        int textWidth = metrics.stringWidth(text);
        int padding = (int) (fontSize * 0.5);

        int rectX = x - textWidth / 2 - padding;
        int rectY = y - metrics.getHeight() + metrics.getDescent() - (blockSize);
        int rectWidth = textWidth + padding * 2;
        int rectHeight = metrics.getHeight() + padding;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity * 0.85f));
        g2d.setColor(new Color(32, 32, 40));
        g2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, padding / 2, padding / 2);

        g2d.setColor(new Color(255, 128, 128));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.drawString(text, x - textWidth / 2, y - blockSize);
    }

    private Block[][][] getChunkData(int chunkX, int chunkY, int chunkZ) {
        synchronized (blockAccessLock) {
            ChunkKey key = new ChunkKey(chunkX, chunkY, chunkZ);
            ChunkData data = chunkCache.get(key);

            if (data != null && !data.isExpired()) {
                return data.getBlocks();
            }

            Block[][][] blocks = new Block[16][16][16];
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        blocks[x][y][z] = seedChecker.getBlock(chunkX * 16 + x, chunkY * 16 + y, chunkZ * 16 + z);
                    }
                }
            }

            chunkCache.put(key, new ChunkData(blocks));
            return blocks;
        }
    }

    private void drawChunk(Graphics2D g2d, int chunkX, int chunkY, int chunkZ, Block[][][] chunkBlocks, BlockInfo[][] visibilityMap, int viewWidth, int viewHeight, int centerX, int centerY, int centerZ) {
        for (int z = 15; z >= 0; z--) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    int worldX = chunkX * 16 + x;
                    int worldY = chunkY * 16 + y;
                    int worldZ = chunkZ * 16 + z;

                    if (worldX < centerX - viewWidth / 2 || worldX > centerX + viewWidth / 2 || worldY < centerY - viewHeight / 2 || worldY > centerY + viewHeight / 2 || worldZ < centerZ - depthRange || worldZ > centerZ) {
                        continue;
                    }

                    Block block = chunkBlocks[x][y][z];
                    drawBlock(g2d, worldX, worldY, worldZ, block, visibilityMap, viewWidth, viewHeight, centerX, centerY, centerZ);
                }
            }
        }
    }

    private void drawBlock(Graphics2D g2d, int worldX, int worldY, int worldZ, Block block, BlockInfo[][] visibilityMap, int viewWidth, int viewHeight, int centerX, int centerY, int centerZ) {
        String blockId = net.minecraft.util.registry.Registry.BLOCK.getId(block).getPath();

        if (BlockInfo.isTransparentBlock(blockId)) return;
        if (worldZ < (centerZ - depthRange) || worldZ > centerZ) return;

        int depth = centerZ - worldZ;
        int screenX = (worldX - centerX + viewWidth / 2) * blockSize;
        int screenY = mapPanel.getHeight() - ((worldY - centerY + viewHeight / 2) * blockSize);
        int mapX = worldX - centerX + viewWidth / 2;
        int mapY = worldY - centerY + viewHeight / 2;

        if (mapX < 0 || mapX >= visibilityMap.length || mapY < 0 || mapY >= visibilityMap[0].length) return;

        BlockInfo existingBlock = visibilityMap[mapX][mapY];
        if (existingBlock != null && existingBlock.isVisible()) return;

        Color baseColor;
        if (showNoiseOverlay && selectedNoiseType != null && noiseSampler != null) {
            double noiseValue = noiseSampler.queryNoiseFromBlockPos(worldX, worldY, worldZ, selectedNoiseType).get(selectedNoiseType);

            float normalizedValue = (float) Math.max(0, Math.min(1, (noiseValue + 1.0) / 2.0));
            baseColor = new Color(normalizedValue, normalizedValue, normalizedValue);
        } else {
            baseColor = BlockInfo.getBlockColor(blockId);
        }

        Color depthAdjustedColor = applyDepthFade(baseColor, depth);

        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(depthAdjustedColor);
        g2d.fillRect(screenX, screenY - blockSize, blockSize, blockSize);

        if (blockId.contains("chest")) {
            Composite originalComposite = g2d.getComposite();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            g2d.fillRect(screenX - HIGHLIGHT_PADDING, screenY - blockSize - HIGHLIGHT_PADDING, blockSize + (HIGHLIGHT_PADDING * 2), blockSize + (HIGHLIGHT_PADDING * 2));

            g2d.setComposite(originalComposite);
        }

        if (blockSize >= 4) {
            g2d.setColor(new Color(0, 0, 0, 32));
            g2d.drawRect(screenX, screenY - blockSize, blockSize, blockSize);
        }

        visibilityMap[mapX][mapY] = new BlockInfo(true, depth);
    }

    private Color applyDepthFade(Color baseColor, int depth) {
        double normalizedDepth = (double) depth / depthRange;
        float fadeFactor = (float) (1.0 - (normalizedDepth * 0.8));

        fadeFactor = Math.max(0.2f, fadeFactor);

        return new Color(Math.max(20, (int) (baseColor.getRed() * fadeFactor)), Math.max(20, (int) (baseColor.getGreen() * fadeFactor)), Math.max(20, (int) (baseColor.getBlue() * fadeFactor)), baseColor.getAlpha());
    }

    public void setDepthRange(int depthRange) {
        this.depthRange = depthRange;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
}
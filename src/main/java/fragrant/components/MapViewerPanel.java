package fragrant.components;

import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;

import nl.jellejurre.seedchecker.SeedCheckerSettings;
import nl.jellejurre.seedchecker.SeedChecker;

public class MapViewerPanel extends JPanel {
    private int centerX = 0;
    private int centerY = 95;
    private int centerZ = 0;
    private int blockSize = 8;
    private final Map<String, Color> blockColors = new HashMap<>();
    private SeedChecker seedChecker;
    private JTextField seedInput;
    private JButton loadSeedButton;
    private JPanel mapPanel;
    private Point highlightedBlock = null;
    private int highlightedBlockDepth = 0;
    private static final int DEPTH_RANGE = 128;
    private static final float DEPTH_FADE_START = 0.95f;
    private static final float DEPTH_FADE_FACTOR = 0.98f;
    private BlockPos spawnPos;
    
    private BufferedImage mapBuffer;
    private final ExecutorService executorService;
    private final Map<ChunkKey, ChunkData> chunkCache;
    private final AtomicBoolean isRendering;
    private long lastRenderTime = 0;
    private static final int RENDER_DELAY = 50;
    private Timer renderTimer;

    private static class BlockInfo {
        final boolean isVisible;
        final int depth;

        BlockInfo(Block block, boolean isVisible, int depth) {
            this.isVisible = isVisible;
            this.depth = depth;
        }
    }
    
    private static class ChunkKey {
        final int x, y, z;
        
        ChunkKey(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkKey)) return false;
            ChunkKey key = (ChunkKey) o;
            return x == key.x && y == key.y && z == key.z;
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }

    private static class ChunkData {
        final Block[][][] blocks;
        final long timestamp;
        
        ChunkData(Block[][][] blocks) {
            this.blocks = blocks;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 30000;
        }
    }

    public MapViewerPanel() {
        setLayout(new BorderLayout());
        executorService = Executors.newFixedThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
        );
        chunkCache = new ConcurrentHashMap<>();
        isRendering = new AtomicBoolean(false);
        
        initializeComponents();
        initializeBlockColors();
        setupRenderTimer();
    }
    
    private void setupRenderTimer() {
        renderTimer = new Timer(RENDER_DELAY, e -> {
            if (System.currentTimeMillis() - lastRenderTime >= RENDER_DELAY) {
                requestMapUpdate();
            }
        });
        renderTimer.setRepeats(false);
    }

    private void initializeComponents() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        seedInput = new JTextField(20);
        seedInput.addActionListener(e -> loadSeed());
        
        loadSeedButton = new JButton("Load Seed");
        loadSeedButton.addActionListener(e -> loadSeed());
        
        JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(95, -64, 320, 1));
        JSpinner zSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        
        setupSpinnerListeners(xSpinner, ySpinner, zSpinner);
        
        JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 32, 1));
        zoomSpinner.addChangeListener(e -> {
            blockSize = (Integer) zoomSpinner.getValue();
            resetMapBuffer();
            requestMapUpdate();
        });
        
        setupControlPanel(controlPanel, seedInput, loadSeedButton, 
                         xSpinner, ySpinner, zSpinner, zoomSpinner);
        
        mapPanel = createMapPanel();
        setupKeyBindings(mapPanel);
        setupMouseListeners(mapPanel);
        
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(() -> mapPanel.requestFocusInWindow());
    }
    
    private void setupSpinnerListeners(JSpinner xSpinner, JSpinner ySpinner, JSpinner zSpinner) {
        xSpinner.addChangeListener(e -> {
            centerX = (Integer) xSpinner.getValue();
            requestMapUpdate();
        });
        ySpinner.addChangeListener(e -> {
            centerY = (Integer) ySpinner.getValue();
            requestMapUpdate();
        });
        zSpinner.addChangeListener(e -> {
            centerZ = (Integer) zSpinner.getValue();
            requestMapUpdate();
        });
    }
    
    private JPanel createMapPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mapBuffer != null) {
                    g.drawImage(mapBuffer, 0, 0, null);
                }
                drawUIOverlay((Graphics2D) g);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        };
    }
    
    private void setupMouseListeners(JPanel panel) {
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point oldHighlight = highlightedBlock;
                highlightedBlock = e.getPoint();
                if (!highlightedBlock.equals(oldHighlight)) {
                    mapPanel.repaint();
                }
            }
        });
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                highlightedBlock = null;
                mapPanel.repaint();
            }
        });
    }
    
    private void requestMapUpdate() {
        if (!isRendering.get()) {
            lastRenderTime = System.currentTimeMillis();
            executorService.submit(this::updateMapBuffer);
        } else {
            renderTimer.restart();
        }
    }
    
    private void resetMapBuffer() {
        if (mapPanel.getWidth() <= 0 || mapPanel.getHeight() <= 0) return;
        
        mapBuffer = new BufferedImage(
            mapPanel.getWidth(),
            mapPanel.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
    }
    
    private void updateMapBuffer() {
        if (!isRendering.compareAndSet(false, true)) return;
        
        try {
            if (mapBuffer == null || 
                mapBuffer.getWidth() != mapPanel.getWidth() || 
                mapBuffer.getHeight() != mapPanel.getHeight()) {
                resetMapBuffer();
            }
            
            if (mapBuffer == null) return;
            
            Graphics2D g2d = mapBuffer.createGraphics();
            try {
                g2d.setBackground(new Color(0, 0, 0, 0));
                g2d.clearRect(0, 0, mapBuffer.getWidth(), mapBuffer.getHeight());
                drawMap(g2d);
            } finally {
                g2d.dispose();
            }
            
            SwingUtilities.invokeLater(() -> mapPanel.repaint());
            
        } finally {
            isRendering.set(false);
        }
    }
    
    private Block[][][] getChunkData(int chunkX, int chunkY, int chunkZ) {
        ChunkKey key = new ChunkKey(chunkX, chunkY, chunkZ);
        ChunkData data = chunkCache.get(key);
        
        if (data != null && !data.isExpired()) {
            return data.blocks;
        }
        
        Block[][][] blocks = new Block[16][16][16];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    blocks[x][y][z] = seedChecker.getBlock(
                        chunkX * 16 + x,
                        chunkY * 16 + y,
                        chunkZ * 16 + z
                    );
                }
            }
        }
        
        chunkCache.put(key, new ChunkData(blocks));
        return blocks;
    }
    
    private void drawChunk(Graphics2D g2d, int chunkX, int chunkY, int chunkZ,
                          Block[][][] chunkBlocks, BlockInfo[][] visibilityMap,
                          int viewWidth, int viewHeight) {
        for (int z = 15; z >= 0; z--) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    int worldX = chunkX * 16 + x;
                    int worldY = chunkY * 16 + y;
                    int worldZ = chunkZ * 16 + z;
                    
                    if (worldX < centerX - viewWidth/2 || worldX > centerX + viewWidth/2 ||
                        worldY < centerY - viewHeight/2 || worldY > centerY + viewHeight/2 ||
                        worldZ < centerZ - DEPTH_RANGE || worldZ > centerZ) {
                        continue;
                    }
                    
                    Block block = chunkBlocks[x][y][z];
                    drawBlock(g2d, worldX, worldY, worldZ, block, visibilityMap, viewWidth, viewHeight);
                }
            }
        }
    }

    private void drawUIOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.drawLine(0, mapPanel.getHeight()/2, mapPanel.getWidth(), mapPanel.getHeight()/2);
        g2d.drawLine(mapPanel.getWidth()/2, 0, mapPanel.getWidth()/2, mapPanel.getHeight());
        
        String coords;
        if (spawnPos != null) {
            coords = String.format("X: %d, Y: %d, Z: %d (Spawn at X: %d, Y: %d, Z: %d)", 
                centerX, centerY, centerZ,
                spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        } else {
            coords = String.format("X: %d, Y: %d, Z: %d (No spawn point available)", 
                centerX, centerY, centerZ);
        }
        
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillRect(5, 5, g2d.getFontMetrics().stringWidth(coords) + 10, 25);
        g2d.setColor(Color.BLACK);
        g2d.drawString(coords, 10, 20);
        
        if (highlightedBlock != null) {
            int mouseX = highlightedBlock.x / blockSize - mapPanel.getWidth() / (2 * blockSize) + centerX;
            int mouseY = (mapPanel.getHeight() - highlightedBlock.y) / blockSize - mapPanel.getHeight() / (2 * blockSize) + centerY;
            int mouseZ = centerZ - highlightedBlockDepth;
            
            Block block = seedChecker.getBlock(mouseX, mouseY, mouseZ);
            if (block != null) {
                String blockInfo = String.format("Block: %s (x:%d, y:%d, z:%d, depth:%d)", 
                    getBlockId(block), mouseX, mouseY, mouseZ, highlightedBlockDepth);
                
                g2d.setColor(new Color(255, 255, 255, 220));
                int padding = 5;
                int textWidth = g2d.getFontMetrics().stringWidth(blockInfo);
                int textHeight = g2d.getFontMetrics().getHeight();
                g2d.fillRect(5, 30, textWidth + padding * 2, textHeight + padding);
                
                g2d.setColor(Color.BLACK);
                g2d.drawString(blockInfo, padding + 5, 45);
            }
        }
    }

    public void updateSeed(long seed) {
        SeedCheckerSettings.initialise();
        this.seedChecker = new SeedChecker(seed);
        this.spawnPos = seedChecker.getSpawnPos();
        seedInput.setText(String.valueOf(seed));
        chunkCache.clear();
        requestMapUpdate();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        executorService.shutdown();
        renderTimer.stop();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void initializeBlockColors() {
        BlockColorMapping.BLOCK_COLORS.forEach((blockId, hexColor) -> {
            String hex = hexColor.replace("#", "");
            int rgb = Integer.parseInt(hex, 16);
            Color color = new Color(
                (rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF
            );
            blockColors.put(blockId, color);
        });
        
        blockColors.put("air", new Color(200, 200, 255, 128));
        blockColors.put("water", new Color(64, 128, 255, 128));
    }

    private void loadSeed() {
        try {
            long seed = Long.parseLong(seedInput.getText().trim());
            updateSeed(seed);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid seed number format", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupControlPanel(JPanel controlPanel, JTextField seedInput, 
                                 JButton loadSeedButton, JSpinner xSpinner, 
                                 JSpinner ySpinner, JSpinner zSpinner, 
                                 JSpinner zoomSpinner) {
        controlPanel.add(new JLabel("Seed:"));
        controlPanel.add(seedInput);
        controlPanel.add(loadSeedButton);
        controlPanel.add(new JLabel("X:"));
        controlPanel.add(xSpinner);
        controlPanel.add(new JLabel("Y:"));
        controlPanel.add(ySpinner);
        controlPanel.add(new JLabel("Z:"));
        controlPanel.add(zSpinner);
        controlPanel.add(new JLabel("Zoom:"));
        controlPanel.add(zoomSpinner);
    }

    private void setupKeyBindings(JPanel panel) {
        bindKey(panel, "W", KeyEvent.VK_W, () -> { centerZ--; requestMapUpdate(); });
        bindKey(panel, "S", KeyEvent.VK_S, () -> { centerZ++; requestMapUpdate(); });
        bindKey(panel, "A", KeyEvent.VK_A, () -> { centerX--; requestMapUpdate(); });
        bindKey(panel, "D", KeyEvent.VK_D, () -> { centerX++; requestMapUpdate(); });
        bindKey(panel, "Q", KeyEvent.VK_Q, () -> { centerY++; requestMapUpdate(); });
        bindKey(panel, "Z", KeyEvent.VK_Z, () -> { centerY--; requestMapUpdate(); });
    }

    private void bindKey(JPanel panel, String name, int keyCode, Runnable action) {
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                panel.requestFocusInWindow();
            }
        });
    }

    private String getBlockId(Block block) {
        return Registry.BLOCK.getId(block).getPath();
    }

    private boolean isAirBlock(String blockId) {
        return blockId.contains("air");
    }

    private Color hexToColor(String hexColor) {
        if (hexColor == null) return Color.MAGENTA;
        String hex = hexColor.replace("#", "");
        try {
            int rgb = Integer.parseInt(hex, 16);
            return new Color(
                (rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF
            );
        } catch (NumberFormatException e) {
            return Color.MAGENTA;
        }
    }

    private void drawSpawnPoint(Graphics2D g2d, int viewWidth, int viewHeight, BlockInfo[][] visibilityMap) {
        if (spawnPos == null) return;
        
        int spawnX = spawnPos.getX();
        int spawnY = spawnPos.getY();
        int spawnZ = spawnPos.getZ();
        
        if (spawnX < centerX - viewWidth/2 || spawnX > centerX + viewWidth/2 ||
            spawnY < centerY - viewHeight/2 || spawnY > centerY + viewHeight/2 ||
            spawnZ < centerZ - DEPTH_RANGE || spawnZ > centerZ) {
            return;
        }
        
        int spawnScreenX = ((spawnX - centerX) + viewWidth/2) * blockSize;
        int spawnScreenY = mapPanel.getHeight() - ((spawnY - centerY) + viewHeight/2) * blockSize;
        
        int depth = centerZ - spawnZ;
        float opacity = (float)Math.pow(DEPTH_FADE_FACTOR, depth);
        opacity = Math.max(0.2f, opacity);
        
        int mapX = spawnX - centerX + viewWidth/2;
        int mapY = spawnY - centerY + viewHeight/2;
        if (mapX >= 0 && mapX < visibilityMap.length &&
            mapY >= 0 && mapY < visibilityMap[0].length) {
            
            BlockInfo blockInfo = visibilityMap[mapX][mapY];
            if (blockInfo == null || blockInfo.depth > depth) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                int markerSize = Math.max(blockSize, 8);
                
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(255, 0, 0, (int)(200 * opacity)));
                
                g2d.drawLine(
                    spawnScreenX,
                    spawnScreenY - markerSize,
                    spawnScreenX,
                    spawnScreenY + markerSize
                );
                g2d.drawLine(
                    spawnScreenX - markerSize,
                    spawnScreenY,
                    spawnScreenX + markerSize,
                    spawnScreenY
                );
                g2d.drawOval(
                    spawnScreenX - markerSize/2,
                    spawnScreenY - markerSize/2,
                    markerSize,
                    markerSize
                );
            }
        }
    }

    private void drawBlock(Graphics2D g2d, int worldX, int worldY, int worldZ,
                          Block block, BlockInfo[][] visibilityMap,
                          int viewWidth, int viewHeight) {
        String blockId = getBlockId(block);
        if (isAirBlock(blockId)) return;
        
        int depth = centerZ - worldZ;
        int screenX = (worldX - centerX + viewWidth/2) * blockSize;
        int screenY = mapPanel.getHeight() - ((worldY - centerY + viewHeight/2) * blockSize);
        int mapX = worldX - centerX + viewWidth/2;
        int mapY = worldY - centerY + viewHeight/2;
        
        if (mapX < 0 || mapX >= visibilityMap.length ||
            mapY < 0 || mapY >= visibilityMap[0].length) {
            return;
        }
        
        BlockInfo existingBlock = visibilityMap[mapX][mapY];
        if (existingBlock != null && existingBlock.isVisible) {
            return;
        }
        
        Color baseColor = blockColors.getOrDefault(blockId,
            hexToColor(BlockColorMapping.getColor(blockId)));
        Color depthAdjustedColor = applyDepthFade(baseColor, depth);
        
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(depthAdjustedColor);
        g2d.fillRect(screenX, screenY - blockSize, blockSize, blockSize);
        
        if (blockSize >= 4) {
            g2d.setColor(new Color(0, 0, 0, 32));
            g2d.drawRect(screenX, screenY - blockSize, blockSize, blockSize);
        }
        
        visibilityMap[mapX][mapY] = new BlockInfo(block, true, depth);
    }

    private Color applyDepthFade(Color baseColor, int depth) {
        float fadeFactor = (float)Math.pow(DEPTH_FADE_START, depth * 0.5f) * 
                          (float)Math.pow(DEPTH_FADE_FACTOR, depth);
        
        return new Color(
            Math.max(20, (int)(baseColor.getRed() * fadeFactor)),
            Math.max(20, (int)(baseColor.getGreen() * fadeFactor)),
            Math.max(20, (int)(baseColor.getBlue() * fadeFactor)),
            baseColor.getAlpha()
        );
    }

    private void drawMap(Graphics2D g2d) {
        if (seedChecker == null) {
            g2d.drawString("No seed loaded", 10, 20);
            return;
        }
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int viewWidth = mapPanel.getWidth() / blockSize;
        int viewHeight = mapPanel.getHeight() / blockSize;
        
        int startChunkX = (centerX - viewWidth/2) >> 4;
        int endChunkX = (centerX + viewWidth/2) >> 4;
        int startChunkY = (centerY - viewHeight/2) >> 4;
        int endChunkY = (centerY + viewHeight/2) >> 4;
        int startChunkZ = (centerZ - DEPTH_RANGE) >> 4;
        int endChunkZ = centerZ >> 4;
        
        BlockInfo[][] visibilityMap = new BlockInfo[viewWidth][viewHeight];
        
        for (int chunkZ = endChunkZ; chunkZ >= startChunkZ; chunkZ--) {
            for (int chunkX = startChunkX; chunkX <= endChunkX; chunkX++) {
                for (int chunkY = startChunkY; chunkY <= endChunkY; chunkY++) {
                    Block[][][] chunkBlocks = getChunkData(chunkX, chunkY, chunkZ);
                    drawChunk(g2d, chunkX, chunkY, chunkZ, chunkBlocks, visibilityMap, viewWidth, viewHeight);
                }
            }
        }
        
        drawSpawnPoint(g2d, viewWidth, viewHeight, visibilityMap);
    }
}
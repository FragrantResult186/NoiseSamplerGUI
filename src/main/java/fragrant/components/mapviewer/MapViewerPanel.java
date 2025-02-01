package fragrant.components.mapviewer;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
import java.util.List;
import java.util.Map;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;

import nl.jellejurre.seedchecker.SeedCheckerDimension;
import nl.jellejurre.seedchecker.SeedCheckerSettings;
import nl.jellejurre.seedchecker.SeedChecker;

public class MapViewerPanel extends JPanel {
    private static final int RENDER_DELAY = 50;
    private static final int HIGHLIGHT_PADDING = 2;
    private static final Color CHEST_HIGHLIGHT_COLOR = new Color(255, 215, 0, 100);

    private int centerX = 0;
    private int centerY = 95;
    private int centerZ = 0;
    private int blockSize = 8;
    private int depthRange = 128;
    private int highlightedBlockDepth = 0;
    private long lastRenderTime = 0;
    private SeedCheckerDimension currentDimension = SeedCheckerDimension.OVERWORLD;

    private JTextField seedInput;
    private JButton loadSeedButton;
    private JPanel mapPanel;
    private JComboBox<SeedCheckerDimension> dimensionSelector;
    private JDialog chestContentsDialog;
    private JDialog helpDialog;

    private BufferedImage mapBuffer;
    private Timer renderTimer;
    private Point highlightedBlock = null;

    private SeedChecker seedChecker;
    private BlockPos spawnPos;
    private final Map<ChunkKey, ChunkData> chunkCache;

    private final ExecutorService executorService;
    private final AtomicBoolean isRendering;

    private static final int MAX_RENDER_ATTEMPTS = 3;
    private static final long RENDER_TIMEOUT = 5000;
    private AtomicInteger renderAttempts = new AtomicInteger(0);
    private volatile boolean renderRequested = false;
    private Timer retryTimer;

    private static class ChunkKey {
        final int x, y, z;

        ChunkKey(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ChunkKey))
                return false;
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
                Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
        chunkCache = new ConcurrentHashMap<>();
        isRendering = new AtomicBoolean(false);

        initializeComponents();
        BlockInfo.initializeBlockColors();
        setupRenderTimer();
        createHelpDialog();
        createChestContentsDialog();
    }

    private void initializeComponents() {
        JPanel mainControlPanel = new JPanel(new BorderLayout(5, 0));
        mainControlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setupSeedControls(leftPanel);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setupCoordinateControls(centerPanel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        setupZoomControl(rightPanel);
        
        mainControlPanel.add(leftPanel, BorderLayout.WEST);
        mainControlPanel.add(centerPanel, BorderLayout.CENTER);
        mainControlPanel.add(rightPanel, BorderLayout.EAST);
        
        JButton helpButton = new JButton("Controls");
        helpButton.addActionListener(e -> {
            if (helpDialog == null) {
                createHelpDialog();
            }
            helpDialog.setVisible(true);
        });
        rightPanel.add(helpButton);

        mapPanel = createMapPanel();
        setupKeyBindings(mapPanel);
        setupMouseListeners(mapPanel);
        
        add(mainControlPanel, BorderLayout.NORTH);
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(() -> mapPanel.requestFocusInWindow());
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
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (seedChecker == null) return;
                
                int mouseX = e.getX() / blockSize - mapPanel.getWidth() / (2 * blockSize) + centerX;
                int mouseY = (mapPanel.getHeight() - e.getY()) / blockSize - mapPanel.getHeight() / (2 * blockSize) + centerY;
                int mouseZ = centerZ - highlightedBlockDepth;
                
                Block block = seedChecker.getBlock(mouseX, mouseY, mouseZ);
                if (block != null) {
                    String blockId = getBlockId(block);
                    if (blockId.contains("chest")) {
                        showChestContents(mouseX, mouseY, mouseZ);
                    }
                }
            }
        });
    }

    private void showChestContents(int x, int y, int z) {
        if (seedChecker == null) return;
        
        List<ItemStack> contents = seedChecker.generateChestLoot(x, y, z);
        if (contents == null || contents.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No chest contents found at this location", 
                "Empty Chest", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Item", "Count"}, 0
        );
        
        for (ItemStack item : contents) {
            model.addRow(new Object[]{
                item.getName().getString(),
                item.getCount()
            });
        }
        
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        chestContentsDialog.getContentPane().removeAll();
        chestContentsDialog.getContentPane().add(new JScrollPane(table));
        chestContentsDialog.setTitle(String.format("Chest Contents at X:%d Y:%d Z:%d", x, y, z));
        chestContentsDialog.setVisible(true);
    }

    private String getBlockId(Block block) {
        return Registry.BLOCK.getId(block).getPath();
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

    private void setupZoomControl(JPanel panel) {
        JPanel controlGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        controlGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("View Controls"),
            new EmptyBorder(2, 2, 2, 2)
        ));
        
        JLabel zoomLabel = new JLabel("Zoom:");
        JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 32, 1));
        Dimension zoomSize = new Dimension(60, zoomSpinner.getPreferredSize().height);
        zoomSpinner.setPreferredSize(zoomSize);
        zoomSpinner.addChangeListener(e -> {
            blockSize = (Integer) zoomSpinner.getValue();
            resetMapBuffer();
            requestMapUpdate();
        });
        
        JLabel depthLabel = new JLabel("Depth Range:");
        JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(128, 1, 256, 1));
        Dimension depthSize = new Dimension(70, depthSpinner.getPreferredSize().height);
        depthSpinner.setPreferredSize(depthSize);
        depthSpinner.addChangeListener(e -> {
            depthRange = (Integer) depthSpinner.getValue();
            requestMapUpdate();
        });
        
        controlGroup.add(zoomLabel);
        controlGroup.add(zoomSpinner);
        controlGroup.add(Box.createHorizontalStrut(10));
        controlGroup.add(depthLabel);
        controlGroup.add(depthSpinner);
        
        panel.add(controlGroup);
    }

    private void resetMapBuffer() {
        if (mapPanel.getWidth() <= 0 || mapPanel.getHeight() <= 0) return;
        
        mapBuffer = new BufferedImage(
            mapPanel.getWidth(),
            mapPanel.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
    }

    private void setupCoordinateControls(JPanel panel) {
        JPanel coordGroup = new JPanel(new BorderLayout(5, 0));
        coordGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Coordinates"),
            new EmptyBorder(2, 2, 2, 2)
        ));
    
        JPanel currentCoordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JLabel currentCoordLabel = new JLabel(String.format("Current: X:%d Y:%d Z:%d", centerX, centerY, centerZ));
        currentCoordPanel.add(currentCoordLabel);
        coordGroup.add(currentCoordPanel, BorderLayout.NORTH);
    
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JTextField xField = new JTextField(6);
        JTextField yField = new JTextField(6);
        JTextField zField = new JTextField(6);
    
        xField.setText(String.valueOf(centerX));
        yField.setText(String.valueOf(centerY));
        zField.setText(String.valueOf(centerZ));
    
        inputPanel.add(new JLabel("X:"));
        inputPanel.add(xField);
        inputPanel.add(new JLabel("Y:"));
        inputPanel.add(yField);
        inputPanel.add(new JLabel("Z:"));
        inputPanel.add(zField);
    
        JButton goButton = new JButton("Go To");
        goButton.addActionListener(e -> {
            try {
                int newX = Integer.parseInt(xField.getText().trim());
                int newY = Integer.parseInt(yField.getText().trim());
                int newZ = Integer.parseInt(zField.getText().trim());
                
                animateMoveTo(newX, newY, newZ, currentCoordLabel);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid coordinate format",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    
        inputPanel.add(goButton);
        coordGroup.add(inputPanel, BorderLayout.CENTER);
    
        JPanel quickNavPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        
        JButton goToSpawnButton = new JButton("Go To Spawn");
        goToSpawnButton.addActionListener(e -> {
            if (spawnPos != null && currentDimension != SeedCheckerDimension.END) {
                animateMoveTo(
                    spawnPos.getX(),
                    spawnPos.getY(),
                    spawnPos.getZ(),
                    currentCoordLabel
                );
            } else {
                JOptionPane.showMessageDialog(this,
                    "Spawn point not available",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton resetPosButton = new JButton("Reset Position");
        resetPosButton.addActionListener(e -> {
            animateMoveTo(0, 95, 0, currentCoordLabel);
        });
    
        quickNavPanel.add(goToSpawnButton);
        quickNavPanel.add(resetPosButton);
        coordGroup.add(quickNavPanel, BorderLayout.SOUTH);
    
        panel.add(coordGroup);
    }

    private void animateMoveTo(int targetX, int targetY, int targetZ, JLabel coordLabel) {
        if (centerX == targetX && centerY == targetY && centerZ == targetZ) {
            return;
        }
    
        final int STEPS = 20;
        final int DELAY = 50;
    
        double dx = (targetX - centerX) / (double)STEPS;
        double dy = (targetY - centerY) / (double)STEPS;
        double dz = (targetZ - centerZ) / (double)STEPS;
    
        final double[] currentPos = {centerX, centerY, centerZ};
        int[] step = {0};
    
        Timer animationTimer = new Timer(DELAY, null);
        animationTimer.addActionListener(e -> {
            step[0]++;
            
            if (step[0] >= STEPS) {
                centerX = targetX;
                centerY = targetY;
                centerZ = targetZ;
                animationTimer.stop();
            } else {
                currentPos[0] += dx;
                currentPos[1] += dy;
                currentPos[2] += dz;
                
                centerX = (int)Math.round(currentPos[0]);
                centerY = (int)Math.round(currentPos[1]);
                centerZ = (int)Math.round(currentPos[2]);
            }
    
            coordLabel.setText(String.format("Current: X:%d Y:%d Z:%d", centerX, centerY, centerZ));
            requestMapUpdate();
        });
    
        animationTimer.start();
    }

    private void setupSeedControls(JPanel panel) {
        JPanel seedGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        seedGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Seed"),
            new EmptyBorder(2, 2, 2, 2)
        ));
        
        seedInput = new JTextField(15);
        seedInput.addActionListener(e -> loadSeed());
        
        loadSeedButton = new JButton("Load");
        loadSeedButton.addActionListener(e -> loadSeed());
        
        seedGroup.add(seedInput);
        seedGroup.add(loadSeedButton);
        
        JPanel dimensionGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        dimensionGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Dimension"),
            new EmptyBorder(2, 2, 2, 2)
        ));
        
        dimensionSelector = new JComboBox<>(SeedCheckerDimension.values());
        dimensionSelector.setSelectedItem(currentDimension);
        dimensionSelector.addActionListener(e -> {
            currentDimension = (SeedCheckerDimension) dimensionSelector.getSelectedItem();
            switch (currentDimension) {
                case NETHER:
                    centerY = 64;
                    break;
                case END:
                    centerY = 64;
                    break;
                case OVERWORLD:
                default:
                    centerY = 95;
                    break;
            }
            updateSeedWithCurrentDimension();
        });
        
        dimensionGroup.add(dimensionSelector);
        
        panel.add(seedGroup);
        panel.add(dimensionGroup);
    }

    private void updateSeedWithCurrentDimension() {
        if (seedChecker != null) {
            long currentSeed = seedChecker.getSeed();
            updateSeed(currentSeed);
        }
    }

    public void updateSeed(long seed) {
        SeedCheckerSettings.initialise();
        this.seedChecker = new SeedChecker(seed, currentDimension);
        this.spawnPos = seedChecker.getSpawnPos();
        seedInput.setText(String.valueOf(seed));
        chunkCache.clear();
        requestMapUpdate();
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

    private void createHelpDialog() {
        helpDialog = new JDialog((Frame) null, "How to Use", ModalityType.MODELESS);
        helpDialog.setSize(400, 500);
    
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setText(
            "Map Viewer Instructions:\n\n" +
            "Basic Controls:\n" +
            "- W: Move north\n" +
            "- S: Move south\n" +
            "- A: Move west\n" +
            "- D: Move east\n" +
            "- Q: Move up\n" +
            "- Z: Move down\n\n" +
            "Mouse Controls:\n" +
            "- Mouse hover: Display block information\n" +
            "- Click on chest: Show chest contents\n\n" +
            "View Settings:\n" +
            "- Zoom: Adjust display size\n" +
            "- Depth range: Adjust the visible depth range\n\n" +
            "Seed Operations:\n" +
            "1. Enter seed value\n" +
            "2. Click the Load button\n" +
            "3. Select dimension (world)\n\n" +
            "Other:\n" +
            "- Crosshair in the center: Current position\n" +
            "- Red cross mark: Spawn point\n" +
            "- Coordinate information: Displayed at the top left of the screen"
        );
    
        helpDialog.add(new JScrollPane(helpText));
        helpDialog.setLocationRelativeTo(this);
    }

    private void createChestContentsDialog() {
        chestContentsDialog = new JDialog((Frame)null, "Chest Contents", ModalityType.MODELESS);
        chestContentsDialog.setSize(400, 300);
        chestContentsDialog.setLocationRelativeTo(this);
    }

    private void drawBlock(Graphics2D g2d, int worldX, int worldY, int worldZ,
            Block block, BlockInfo[][] visibilityMap,
            int viewWidth, int viewHeight) {
        String blockId = Registry.BLOCK.getId(block).getPath();

        if (BlockInfo.isTransparentBlock(blockId)) {
            return;
        }

        if (worldZ < centerZ - depthRange || worldZ > centerZ)
            return;

        int depth = centerZ - worldZ;
        int screenX = (worldX - centerX + viewWidth / 2) * blockSize;
        int screenY = mapPanel.getHeight() - ((worldY - centerY + viewHeight / 2) * blockSize);
        int mapX = worldX - centerX + viewWidth / 2;
        int mapY = worldY - centerY + viewHeight / 2;

        if (mapX < 0 || mapX >= visibilityMap.length ||
                mapY < 0 || mapY >= visibilityMap[0].length) {
            return;
        }

        BlockInfo existingBlock = visibilityMap[mapX][mapY];
        if (existingBlock != null && existingBlock.isVisible) {
            return;
        }

        Color baseColor = BlockInfo.getBlockColor(blockId);
        Color depthAdjustedColor = applyDepthFade(baseColor, depth);

        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(depthAdjustedColor);
        g2d.fillRect(screenX, screenY - blockSize, blockSize, blockSize);

        if (blockId.contains("chest")) {
            Composite originalComposite = g2d.getComposite();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            g2d.setColor(CHEST_HIGHLIGHT_COLOR);
            g2d.fillRect(
                    screenX - HIGHLIGHT_PADDING,
                    screenY - blockSize - HIGHLIGHT_PADDING,
                    blockSize + (HIGHLIGHT_PADDING * 2),
                    blockSize + (HIGHLIGHT_PADDING * 2));

            g2d.setComposite(originalComposite);
        }

        if (blockSize >= 4) {
            g2d.setColor(new Color(0, 0, 0, 32));
            g2d.drawRect(screenX, screenY - blockSize, blockSize, blockSize);
        }

        visibilityMap[mapX][mapY] = new BlockInfo(block, true, depth);
    }

    private void requestMapUpdate() {
        if (!isRendering.get()) {
            renderRequested = true;
            lastRenderTime = System.currentTimeMillis();
            scheduleRender();
        } else {
            renderTimer.restart();
        }
    }

    private void scheduleRender() {
        if (!renderRequested)
            return;

        executorService.submit(() -> {
            try {
                if (updateMapBuffer()) {
                    renderRequested = false;
                    renderAttempts.set(0);
                } else {
                    handleFailedRender();
                }
            } catch (Exception e) {
                handleFailedRender();
            }
        });
    }

    private void handleFailedRender() {
        int attempts = renderAttempts.incrementAndGet();
        if (attempts < MAX_RENDER_ATTEMPTS) {
            long delay = Math.min(1000 * (1L << attempts), RENDER_TIMEOUT);

            if (retryTimer != null && retryTimer.isRunning()) {
                retryTimer.stop();
            }

            retryTimer = new Timer((int) delay, e -> scheduleRender());
            retryTimer.setRepeats(false);
            retryTimer.start();

        } else {
            renderRequested = false;
            renderAttempts.set(0);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        MapViewerPanel.this,
                        "Map rendering failed. Please try again.",
                        "Rendering Error",
                        JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private boolean updateMapBuffer() {
        if (!isRendering.compareAndSet(false, true))
            return false;

        try {
            long startTime = System.currentTimeMillis();

            if (mapBuffer == null ||
                    mapBuffer.getWidth() != mapPanel.getWidth() ||
                    mapBuffer.getHeight() != mapPanel.getHeight()) {
                resetMapBuffer();
            }

            if (mapBuffer == null)
                return false;

            Graphics2D g2d = mapBuffer.createGraphics();
            try {
                g2d.setBackground(new Color(0, 0, 0, 0));
                g2d.clearRect(0, 0, mapBuffer.getWidth(), mapBuffer.getHeight());
                drawMap(g2d);

                if (System.currentTimeMillis() - startTime > RENDER_TIMEOUT) {
                    return false;
                }
            } finally {
                g2d.dispose();
            }

            SwingUtilities.invokeLater(() -> mapPanel.repaint());
            return true;

        } catch (Exception e) {
            return false;
        } finally {
            isRendering.set(false);
        }
    }

    private void setupRenderTimer() {
        renderTimer = new Timer(RENDER_DELAY, e -> {
            if (System.currentTimeMillis() - lastRenderTime >= RENDER_DELAY) {
                requestMapUpdate();
            }
        });
        renderTimer.setRepeats(false);
    }

    private Block[][][] getChunkData(int chunkX, int chunkY, int chunkZ) {
        ChunkKey key = new ChunkKey(chunkX, chunkY, chunkZ);
        ChunkData data = chunkCache.get(key);

        if (data != null && !data.isExpired()) {
            return data.blocks;
        }

        long startTime = System.currentTimeMillis();
        Block[][][] blocks = new Block[16][16][16];

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (System.currentTimeMillis() - startTime > RENDER_TIMEOUT) {
                        throw new RuntimeException("Chunk loading timeout");
                    }

                    blocks[x][y][z] = seedChecker.getBlock(
                            chunkX * 16 + x,
                            chunkY * 16 + y,
                            chunkZ * 16 + z);
                }
            }
        }

        chunkCache.put(key, new ChunkData(blocks));
        return blocks;
    }

    private void drawMap(Graphics2D g2d) {
        if (seedChecker == null) {
            g2d.drawString("No seed loaded", 10, 20);
            return;
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int viewWidth = mapPanel.getWidth() / blockSize;
        int viewHeight = mapPanel.getHeight() / blockSize;

        int startChunkX = (centerX - viewWidth / 2) >> 4;
        int endChunkX = (centerX + viewWidth / 2) >> 4;
        int startChunkY = (centerY - viewHeight / 2) >> 4;
        int endChunkY = (centerY + viewHeight / 2) >> 4;
        int startChunkZ = (centerZ - depthRange) >> 4;
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

    private void drawSpawnPoint(Graphics2D g2d, int viewWidth, int viewHeight, BlockInfo[][] visibilityMap) {
        if (spawnPos == null || currentDimension == SeedCheckerDimension.END) return;
        
        int spawnX = spawnPos.getX();
        int spawnY = spawnPos.getY();
        int spawnZ = spawnPos.getZ();
        
        if (spawnX < centerX - viewWidth/2 || spawnX > centerX + viewWidth/2 ||
            spawnY < centerY - viewHeight/2 || spawnY > centerY + viewHeight/2 ||
            spawnZ < centerZ - depthRange || spawnZ > centerZ) {
            return;
        }
        
        int spawnScreenX = ((spawnX - centerX) + viewWidth/2) * blockSize;
        int spawnScreenY = mapPanel.getHeight() - ((spawnY - centerY) + viewHeight/2) * blockSize;
        
        int depth = centerZ - spawnZ;
        
        double normalizedDepth = (double) depth / depthRange;
        float opacity = (float) (1.0 - (normalizedDepth * 0.8));
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
                        worldZ < centerZ - depthRange || worldZ > centerZ) {
                        continue;
                    }
                    
                    Block block = chunkBlocks[x][y][z];
                    drawBlock(g2d, worldX, worldY, worldZ, block, visibilityMap, viewWidth, viewHeight);
                }
            }
        }
    }

    private Color applyDepthFade(Color baseColor, int depth) {
        double normalizedDepth = (double) depth / depthRange;
        float fadeFactor = (float) (1.0 - (normalizedDepth * 0.8));

        fadeFactor = Math.max(0.2f, fadeFactor);

        return new Color(
                Math.max(20, (int) (baseColor.getRed() * fadeFactor)),
                Math.max(20, (int) (baseColor.getGreen() * fadeFactor)),
                Math.max(20, (int) (baseColor.getBlue() * fadeFactor)),
                baseColor.getAlpha());
    }
}

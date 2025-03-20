package fragrant.components.mapviewer.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;
import java.util.Objects;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

import fragrant.components.mapviewer.ui.dialogs.Dialog;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;

import fragrant.components.mapviewer.ui.components.LoadingSpinner;
import fragrant.components.mapviewer.model.BlockInfo;
import fragrant.components.mapviewer.model.ChunkData;
import fragrant.components.mapviewer.model.ChunkKey;

import nl.jellejurre.seedchecker.SeedCheckerDimension;
import nl.jellejurre.seedchecker.SeedCheckerSettings;
import nl.jellejurre.seedchecker.SeedChecker;

import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.NoiseType;
import org.jetbrains.annotations.NotNull;

public class MapViewerPanel extends JPanel {
    private static final int RENDER_DELAY = 60;
    private static final int MAX_RENDER_ATTEMPTS = 3;
    private static final long RENDER_TIMEOUT = 90000;
    private static final long LOADING_DISPLAY_THRESHOLD = 200;

    protected int centerX = 0;
    protected int centerY = 60;
    protected int centerZ = 0;
    protected int blockSize = 7;
    private int depthRange = 64;
    private int targetLevel = 10;
    private long lastRenderTime = 0;
    private volatile boolean renderRequested = false;
    private volatile boolean isUpdatingMap = false;
    private volatile boolean isLoading = false;
    private final boolean isClosed = false;

    private JTextField seedInput;
    private JButton loadSeedButton;
    private JPanel mapPanel;
    private JPanel loadingOverlay;
    private JComboBox<SeedCheckerDimension> dimensionSelector;
    private JComboBox<NoiseType> noiseTypeSelector;

    private final Dialog dialogManager;
    private final Map<ChunkKey, ChunkData> chunkCache;
    private final ExecutorService executorService;
    private final AtomicBoolean isRendering;
    private final Object blockAccessLock = new Object();

    private BufferedImage mapBuffer;
    private Timer renderTimer;
    private Timer retryTimer;
    private Timer loadingAnimationTimer;
    private Timer loadingDisplayTimer;
    private MapRenderer mapRenderer;

    private SeedChecker seedChecker;
    private BlockPos spawnPos;
    private SeedCheckerDimension currentDimension = SeedCheckerDimension.OVERWORLD;
    private NoiseSampler noiseSampler;
    private NoiseType selectedNoiseType = null;
    private boolean showNoiseOverlay = false;
    private Point highlightedBlock = null;

    private final AtomicInteger renderAttempts = new AtomicInteger(0);

    public MapViewerPanel() {
        setLayout(new BorderLayout());
        executorService = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
        chunkCache = new ConcurrentHashMap<>();
        isRendering = new AtomicBoolean(false);

        createLoadingOverlay();
        loadingOverlay.setVisible(false);
        add(loadingOverlay);

        initializeComponents();
        BlockInfo.initializeBlockColors();
        setupRenderTimer();
        dialogManager = new Dialog();
        dialogManager.createHelpDialog(this);
        dialogManager.createChestContentsDialog(this);
    }

    private JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                new EmptyBorder(2, 2, 2, 2)
        ));
        return panel;
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel mainControlPanel = new JPanel(new BorderLayout(5, 0));
        mainControlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setupSeedControls(leftPanel);
        setupNoiseControls(leftPanel);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setupCoordinateControls(centerPanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        setupZoomControl(rightPanel);

        JButton helpButton = new JButton("Controls");
        helpButton.addActionListener(e -> dialogManager.showHelpDialog());
        rightPanel.add(helpButton);

        mainControlPanel.add(leftPanel, BorderLayout.WEST);
        mainControlPanel.add(centerPanel, BorderLayout.CENTER);
        mainControlPanel.add(rightPanel, BorderLayout.EAST);

        JScrollPane controlScrollPane = new JScrollPane(mainControlPanel);
        controlScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controlScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        controlScrollPane.setBorder(BorderFactory.createEmptyBorder());

        mapPanel = createMapPanel();
        setupKeyBindings(mapPanel);
        setupMouseListeners(mapPanel);

        JPanel layeredContainer = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        layeredContainer.setLayout(new OverlayLayout(layeredContainer));

        JScrollPane mapScrollPane = new JScrollPane(mapPanel);
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        layeredContainer.add(loadingOverlay);
        layeredContainer.add(mapScrollPane);

        add(controlScrollPane, BorderLayout.NORTH);
        add(layeredContainer, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            mapPanel.requestFocusInWindow();
            mapPanel.setFocusable(true);
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

    private void setupKeyBindings(JPanel panel) {
        bindKey(panel, "W", KeyEvent.VK_W, () -> {
            centerZ--;
            requestMapUpdate();
        });
        bindKey(panel, "S", KeyEvent.VK_S, () -> {
            centerZ++;
            requestMapUpdate();
        });
        bindKey(panel, "A", KeyEvent.VK_A, () -> {
            centerX--;
            requestMapUpdate();
        });
        bindKey(panel, "D", KeyEvent.VK_D, () -> {
            centerX++;
            requestMapUpdate();
        });
        bindKey(panel, "Q", KeyEvent.VK_Q, () -> {
            centerY++;
            requestMapUpdate();
        });
        bindKey(panel, "Z", KeyEvent.VK_Z, () -> {
            centerY--;
            requestMapUpdate();
        });
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

    private void setupMouseListeners(JPanel panel) {
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                highlightedBlock = e.getPoint();
                mapPanel.repaint();
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
                if (seedChecker == null || isClosed) return;

                Point blockPos = calculateBlockPosition(e.getPoint());
                Block block = seedChecker.getBlock(blockPos.x, blockPos.y, centerZ);

                if (block != null && getBlockId(block).contains("chest")) {
                    showChestContents(blockPos.x, blockPos.y, centerZ);
                }
            }
        });
    }

    private void setupNoiseControls(JPanel panel) {
        JPanel noiseGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        noiseGroup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Noise Visualization"),
                new EmptyBorder(2, 2, 2, 2)));

        NoiseType[] noiseTypesWithNone = new NoiseType[NoiseType.values().length + 1];
        noiseTypesWithNone[0] = null;
        System.arraycopy(NoiseType.values(), 0, noiseTypesWithNone, 1, NoiseType.values().length);

        noiseTypeSelector = new JComboBox<>(noiseTypesWithNone);
        noiseTypeSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String display = (value == null) ? "None" : value.toString();
                return super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
            }
        });
        noiseTypeSelector.setSelectedItem(null);
        noiseTypeSelector.addActionListener(e -> {
            selectedNoiseType = (NoiseType) noiseTypeSelector.getSelectedItem();
            showNoiseOverlay = selectedNoiseType != null;
            if (mapRenderer != null) {
                mapRenderer.setNoiseSettings(showNoiseOverlay, selectedNoiseType);
            }
            requestMapUpdate();
        });

        noiseGroup.add(noiseTypeSelector);
        panel.add(noiseGroup);
    }

    private void setupZoomControl(JPanel panel) {
        JPanel controlGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        controlGroup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("View Controls"),
                new EmptyBorder(2, 2, 2, 2)));

        JLabel zoomLabel = new JLabel("Zoom:");
        JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 32, 1));
        Dimension zoomSize = new Dimension(60, zoomSpinner.getPreferredSize().height);
        zoomSpinner.setPreferredSize(zoomSize);
        zoomSpinner.addChangeListener(e -> {
            blockSize = (Integer) zoomSpinner.getValue();
            if (mapRenderer != null) {
                mapRenderer.setBlockSize(blockSize);
            }
            resetMapBuffer();
            requestMapUpdate();
        });

        JLabel depthLabel = new JLabel("Depth Range:");
        JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(128, 1, 256, 1));
        Dimension depthSize = new Dimension(70, depthSpinner.getPreferredSize().height);
        depthSpinner.setPreferredSize(depthSize);
        depthSpinner.addChangeListener(e -> {
            depthRange = (Integer) depthSpinner.getValue();
            if (mapRenderer != null) {
                mapRenderer.setDepthRange(depthRange);
            }
            requestMapUpdate();
        });

        controlGroup.add(zoomLabel);
        controlGroup.add(zoomSpinner);
        controlGroup.add(Box.createHorizontalStrut(10));
        controlGroup.add(depthLabel);
        controlGroup.add(depthSpinner);

        panel.add(controlGroup);
    }

    private void setupCoordinateControls(JPanel panel) {
        JPanel coordGroup = new JPanel(new BorderLayout(5, 0));
        coordGroup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Coordinates"),
                new EmptyBorder(2, 2, 2, 2)));

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
                JOptionPane.showMessageDialog(this, "Invalid coordinate format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(goButton);
        coordGroup.add(inputPanel, BorderLayout.CENTER);

        JPanel quickNavPanel = getJPanel(currentCoordLabel);
        coordGroup.add(quickNavPanel, BorderLayout.SOUTH);

        panel.add(coordGroup);
    }

    private void setupSeedControls(JPanel panel) {
        JPanel seedGroup = createTitledPanel("Seed", new FlowLayout(FlowLayout.LEFT, 2, 0));

        seedInput = new JTextField(15);
        seedInput.addActionListener(e -> loadSeed());

        loadSeedButton = new JButton("Load");
        loadSeedButton.addActionListener(e -> loadSeed());

        seedGroup.add(seedInput);
        seedGroup.add(loadSeedButton);

        JPanel targetLevelGroup = createTitledPanel("Target Level", new FlowLayout(FlowLayout.LEFT, 2, 0));
        SpinnerNumberModel targetLevelModel = new SpinnerNumberModel(10, 5, 10, 1);
        JSpinner targetLevelSpinner = new JSpinner(targetLevelModel);
        targetLevelSpinner.setPreferredSize(new Dimension(60, targetLevelSpinner.getPreferredSize().height));

        targetLevelSpinner.addChangeListener(e -> {
            targetLevel = (Integer) targetLevelSpinner.getValue();
            if (seedChecker != null) updateSeedWithCurrentSettings();
        });

        targetLevelGroup.add(targetLevelSpinner);

        JPanel dimensionGroup = createTitledPanel("Dimension", new FlowLayout(FlowLayout.LEFT, 2, 0));
        dimensionSelector = new JComboBox<>(SeedCheckerDimension.values());
        dimensionSelector.setSelectedItem(currentDimension);
        dimensionSelector.addActionListener(e -> handleDimensionChange());

        dimensionGroup.add(dimensionSelector);

        panel.add(seedGroup);
        panel.add(targetLevelGroup);
        panel.add(dimensionGroup);
    }

    private void drawUIOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.drawLine(0, mapPanel.getHeight() / 2, mapPanel.getWidth(), mapPanel.getHeight() / 2);
        g2d.drawLine(mapPanel.getWidth() / 2, 0, mapPanel.getWidth() / 2, mapPanel.getHeight());

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

        if (highlightedBlock != null && seedChecker != null && !isUpdatingMap) {
            synchronized (blockAccessLock) {
                int mouseX = highlightedBlock.x / blockSize - mapPanel.getWidth() / (2 * blockSize) + centerX;
                int mouseY = (mapPanel.getHeight() - highlightedBlock.y) / blockSize
                        - mapPanel.getHeight() / (2 * blockSize) + centerY;
                int highlightedBlockDepth = 0;
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
    }

    private void showLoadingOverlay() {
        System.currentTimeMillis();

        if (loadingDisplayTimer != null && loadingDisplayTimer.isRunning()) {
            loadingDisplayTimer.stop();
        }

        loadingDisplayTimer = new Timer((int) LOADING_DISPLAY_THRESHOLD, e -> {
            if (isLoading) {
                loadingOverlay.setVisible(true);
                disableControls();
                startLoadingAnimation();
            }
        });
        loadingDisplayTimer.setRepeats(false);
        loadingDisplayTimer.start();

        isLoading = true;
    }

    private void hideLoadingOverlay() {
        if (loadingDisplayTimer != null) {
            loadingDisplayTimer.stop();
        }

        if (isLoading) {
            if (loadingOverlay.isVisible()) {
                loadingOverlay.setVisible(false);
                enableControls();
                stopLoadingAnimation();
            }

            isLoading = false;
        }
    }

    private void startLoadingAnimation() {
        if (loadingAnimationTimer != null && loadingAnimationTimer.isRunning()) {
            loadingAnimationTimer.stop();
        }

        loadingAnimationTimer = new Timer(50, e -> repaint());
        loadingAnimationTimer.start();
    }

    private void stopLoadingAnimation() {
        if (loadingAnimationTimer != null) {
            loadingAnimationTimer.stop();
        }
    }

    private void disableControls() {
        loadSeedButton.setEnabled(false);
        seedInput.setEnabled(false);
        dimensionSelector.setEnabled(false);
    }

    private void enableControls() {
        loadSeedButton.setEnabled(true);
        seedInput.setEnabled(true);
        dimensionSelector.setEnabled(true);
    }

    public void requestMapUpdate() {
        if (!isRendering.get()) {
            showLoadingOverlay();
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
                    SwingUtilities.invokeLater(this::hideLoadingOverlay);
                } else {
                    handleFailedRender();
                }
            } catch (Exception e) {
                handleFailedRender();
            }
        });
    }

    private boolean updateMapBuffer() {
        if (!isRendering.compareAndSet(false, true)) return false;

        try {
            isUpdatingMap = true;
            synchronized (blockAccessLock) {
                long startTime = System.currentTimeMillis();

                if (mapBuffer == null ||
                        mapBuffer.getWidth() != mapPanel.getWidth() ||
                        mapBuffer.getHeight() != mapPanel.getHeight()) {
                    resetMapBuffer();
                }

                if (mapBuffer == null) return false;

                Graphics2D g2d = null;
                try {
                    g2d = mapBuffer.createGraphics();
                    g2d.setBackground(new Color(0, 0, 0, 0));
                    g2d.clearRect(0, 0, mapBuffer.getWidth(), mapBuffer.getHeight());
                    drawMap(g2d);

                    if (System.currentTimeMillis() - startTime > RENDER_TIMEOUT) return false;
                } finally {
                    if (g2d != null) g2d.dispose();
                }

                SwingUtilities.invokeLater(() -> mapPanel.repaint());
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            isUpdatingMap = false;
            isRendering.set(false);
        }
    }

    private void drawMap(Graphics2D g2d) {
        if (mapRenderer == null) {
            g2d.drawString("No seed loaded", 10, 20);
            return;
        }
        mapRenderer.drawMap(
                g2d,
                mapPanel.getWidth(),
                mapPanel.getHeight(),
                centerX,
                centerY,
                centerZ
        );
    }

    public void updateSeed(long seed) {
        showLoadingOverlay();
        CompletableFuture.runAsync(() -> {
            try {
                synchronized (blockAccessLock) {
                    SeedCheckerSettings.initialise();
                    this.seedChecker = new SeedChecker(seed, targetLevel, currentDimension);

                    nl.kallestruik.noisesampler.minecraft.Dimension noiseDimension = switch (currentDimension) {
                        case OVERWORLD -> nl.kallestruik.noisesampler.minecraft.Dimension.OVERWORLD;
                        case NETHER -> nl.kallestruik.noisesampler.minecraft.Dimension.NETHER;
                        case END -> nl.kallestruik.noisesampler.minecraft.Dimension.THEEND;
                    };
                    this.noiseSampler = new NoiseSampler(seed, noiseDimension);

                    BlockPos originalSpawn = seedChecker.getSpawnPos();
                    if (currentDimension == SeedCheckerDimension.OVERWORLD && originalSpawn != null) {
                        this.spawnPos = detectSpawnHeight(originalSpawn);
                    } else {
                        this.spawnPos = originalSpawn;
                    }

                    mapRenderer = new MapRenderer(
                            seedChecker,
                            blockSize,
                            depthRange,
                            chunkCache,
                            blockAccessLock,
                            mapPanel
                    );
                    mapRenderer.setNoiseSampler(noiseSampler);
                    mapRenderer.setSpawnPos(spawnPos);
                    mapRenderer.setNoiseSettings(showNoiseOverlay, selectedNoiseType);
                    mapRenderer.setBlockSize(blockSize);
                    mapRenderer.setDepthRange(depthRange);

                    SwingUtilities.invokeLater(() -> {
                        seedInput.setText(String.valueOf(seed));
                        chunkCache.clear();
                        requestMapUpdate();
                    });
                }
            } finally {
                SwingUtilities.invokeLater(this::hideLoadingOverlay);
            }
        }, executorService);
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

    private void updateSeedWithCurrentSettings() {
        if (seedChecker != null) {
            long currentSeed = seedChecker.getSeed();
            updateSeed(currentSeed);
        }
    }

    private void showChestContents(int x, int y, int z) {
        if (seedChecker == null) return;
        synchronized (blockAccessLock) {
            List<ItemStack> contents = seedChecker.generateChestLoot(x, y, z);
            dialogManager.showChestDialog(x, y, z, contents, this);
        }
    }

    private String getBlockId(Block block) {
        return Registry.BLOCK.getId(block).getPath();
    }

    private boolean isAirBlock(Block block) {
        if (block == null)
            return false;
        String blockId = Registry.BLOCK.getId(block).getPath();
        return blockId.equals("air") || blockId.equals("cave_air") || blockId.equals("void_air");
    }

    private void resetMapBuffer() {
        if (mapPanel.getWidth() <= 0 || mapPanel.getHeight() <= 0)
            return;

        mapBuffer = new BufferedImage(
                mapPanel.getWidth(),
                mapPanel.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
    }

    private @NotNull JPanel getJPanel(JLabel currentCoordLabel) {
        JPanel quickNavPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JButton goToSpawnButton = new JButton("Go To Spawn");
        goToSpawnButton.addActionListener(e -> {
            if (spawnPos != null && currentDimension != SeedCheckerDimension.END) {
                animateMoveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), currentCoordLabel);
            } else {
                JOptionPane.showMessageDialog(this, "Spawn point not available", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton resetPosButton = new JButton("Reset Position");
        resetPosButton.addActionListener(e -> animateMoveTo(0, 95, 0, currentCoordLabel));

        quickNavPanel.add(goToSpawnButton);
        quickNavPanel.add(resetPosButton);
        return quickNavPanel;
    }

    private void animateMoveTo(int targetX, int targetY, int targetZ, JLabel coordLabel) {
        if (centerX == targetX && centerY == targetY && centerZ == targetZ) {
            return;
        }

        final int STEPS = 20;
        final int DELAY = 50;

        double dx = (targetX - centerX) / (double) STEPS;
        double dy = (targetY - centerY) / (double) STEPS;
        double dz = (targetZ - centerZ) / (double) STEPS;

        final double[] currentPos = { centerX, centerY, centerZ };
        int[] step = { 0 };

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

                centerX = (int) Math.round(currentPos[0]);
                centerY = (int) Math.round(currentPos[1]);
                centerZ = (int) Math.round(currentPos[2]);
            }

            coordLabel.setText(String.format("Current: X:%d Y:%d Z:%d", centerX, centerY, centerZ));
            requestMapUpdate();
        });

        animationTimer.start();
    }

    private void handleDimensionChange() {
        currentDimension = (SeedCheckerDimension) dimensionSelector.getSelectedItem();
        switch (Objects.requireNonNull(currentDimension)) {
            case NETHER:
            case END:
                centerY = 64;
                break;
            default:
                centerY = 95;
        }
        updateSeedWithCurrentSettings();
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
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    MapViewerPanel.this,
                    "Map rendering failed. Please try again.",
                    "Rendering Error",
                    JOptionPane.ERROR_MESSAGE));
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

    private BlockPos detectSpawnHeight(BlockPos originalSpawn) {
        if (originalSpawn == null || seedChecker == null) {
            return null;
        }

        int spawnX = originalSpawn.getX();
        int spawnZ = originalSpawn.getZ();

        for (int y = 256; y >= -64; y--) {
            Block block = seedChecker.getBlock(spawnX, y, spawnZ);
            Block blockAbove = seedChecker.getBlock(spawnX, y + 1, spawnZ);
            Block blockBelow = seedChecker.getBlock(spawnX, y - 1, spawnZ);

            if (isAirBlock(block) &&
                    isAirBlock(blockAbove) &&
                    !isAirBlock(blockBelow)) {
                return new BlockPos(spawnX, y, spawnZ);
            }
        }

        return originalSpawn;
    }

    private void createLoadingOverlay() {
        loadingOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        loadingOverlay.setOpaque(false);
        loadingOverlay.setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        JLabel loadingLabel = new JLabel("Generating Map...");
        loadingLabel.setBackground(Color.WHITE);
        loadingLabel.setOpaque(true);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLabel.setFont(loadingLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel spinnerPanel = new LoadingSpinner();
        spinnerPanel.setBackground(Color.WHITE);
        spinnerPanel.setOpaque(true);
        spinnerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinnerPanel.setPreferredSize(new Dimension(40, 40));

        contentPanel.add(loadingLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(spinnerPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        loadingOverlay.add(contentPanel, gbc);

        loadingOverlay.setVisible(false);
    }

    private Point calculateBlockPosition(Point mousePoint) {
        int blockX = mousePoint.x / blockSize
                - mapPanel.getWidth() / (2 * blockSize)
                + centerX;

        int blockY = (mapPanel.getHeight() - mousePoint.y) / blockSize
                - mapPanel.getHeight() / (2 * blockSize)
                + centerY;

        return new Point(blockX, blockY);
    }
}
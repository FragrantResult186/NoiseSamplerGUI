package fragrant.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import nl.jellejurre.seedchecker.SeedChecker;
import nl.jellejurre.seedchecker.SeedCheckerSettings;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

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
    private static final float DEPTH_FADE_FACTOR = 0.97f;
    private BlockPos spawnPos;

    public MapViewerPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        initializeBlockColors();
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
        
        xSpinner.addChangeListener(e -> {
            centerX = (Integer) xSpinner.getValue();
            mapPanel.repaint();
        });
        ySpinner.addChangeListener(e -> {
            centerY = (Integer) ySpinner.getValue();
            mapPanel.repaint();
        });
        zSpinner.addChangeListener(e -> {
            centerZ = (Integer) zSpinner.getValue();
            mapPanel.repaint();
        });
        
        JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 32, 1));
        zoomSpinner.addChangeListener(e -> {
            blockSize = (Integer) zoomSpinner.getValue();
            mapPanel.repaint();
        });
        
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
        
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(800, 600));
        setupKeyBindings(mapPanel);
        mapPanel.setFocusable(true);
        
        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                highlightedBlock = e.getPoint();
                mapPanel.repaint();
            }
        });
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                highlightedBlock = null;
                mapPanel.repaint();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                mapPanel.requestFocusInWindow();
            }
        });
        
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(() -> mapPanel.requestFocusInWindow());
    }
    
    private void setupKeyBindings(JPanel panel) {
        bindKey(panel, "W", KeyEvent.VK_W, () -> { centerZ--; mapPanel.repaint(); });
        bindKey(panel, "S", KeyEvent.VK_S, () -> { centerZ++; mapPanel.repaint(); });
        bindKey(panel, "A", KeyEvent.VK_A, () -> { centerX--; mapPanel.repaint(); });
        bindKey(panel, "D", KeyEvent.VK_D, () -> { centerX++; mapPanel.repaint(); });
        bindKey(panel, "Q", KeyEvent.VK_Q, () -> { centerY++; mapPanel.repaint(); });
        bindKey(panel, "Z", KeyEvent.VK_Z, () -> { centerY--; mapPanel.repaint(); });
    }
    
    private void bindKey(JPanel panel, String name, int keyCode, Runnable action) {
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                mapPanel.requestFocusInWindow();
            }
        });
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
        blockColors.put("water", new Color(50, 100, 200, 80));
    }

    private Color applyDepthFade(Color baseColor, int depth, String blockId) {
        float fadeFactor = (float)Math.pow(DEPTH_FADE_FACTOR, depth);
        
        if (isWaterBlock(blockId)) {
            float blueIncreaseFactor = Math.min(1.0f + (depth * 0.1f), 2.0f);
            return new Color(
                (int)(baseColor.getRed() * 0.8f),
                (int)(baseColor.getGreen() * 0.9f),
                Math.min(255, (int)(baseColor.getBlue() * blueIncreaseFactor)),
                baseColor.getAlpha()
            );
        }
        
        return new Color(
            (int)(baseColor.getRed() * fadeFactor),
            (int)(baseColor.getGreen() * fadeFactor),
            (int)(baseColor.getBlue() * fadeFactor),
            baseColor.getAlpha()
        );
    }

    private String getBlockId(Block block) {
        return Registry.BLOCK.getId(block).getPath();
    }

    private void drawMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        if (seedChecker == null) {
            g2d.drawString("No seed loaded", 10, 20);
            return;
        }
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int viewWidth = mapPanel.getWidth() / blockSize;
        int viewHeight = mapPanel.getHeight() / blockSize;
        
        boolean[][] visibilityMap = new boolean[viewWidth][viewHeight];
        Block highlightedBlockInfo = null;
        
        int spawnScreenX = ((spawnPos.getX() - centerX) + viewWidth/2) * blockSize;
        int spawnScreenY = mapPanel.getHeight() - ((spawnPos.getY() - centerY) + viewHeight/2) * blockSize;
        boolean spawnVisible = false;
        int spawnDepth = 0;
        
        for (int z = 0; z < DEPTH_RANGE; z++) {
            for (int x = -viewWidth/2; x < viewWidth/2; x++) {
                for (int y = -viewHeight/2; y < viewHeight/2; y++) {
                    int worldX = centerX + x;
                    int worldY = centerY + y;
                    int worldZ = centerZ - z;
                    
                    int screenX = (x + viewWidth/2) * blockSize;
                    int screenY = mapPanel.getHeight() - ((y + viewHeight/2) * blockSize);
                    
                    int mapX = x + viewWidth/2;
                    int mapY = y + viewHeight/2;
                    
                    if (worldX == spawnPos.getX() && worldY == spawnPos.getY() && worldZ == spawnPos.getZ()) {
                        if (!visibilityMap[mapX][mapY]) {
                            spawnVisible = true;
                            spawnDepth = z;
                        }
                    }
                    
                    Block block = seedChecker.getBlock(worldX, worldY, worldZ);
                    String blockId = getBlockId(block);
                    
                    if (isAirBlock(blockId)) {
                        continue;
                    }
                    
                    if (z > 0 && visibilityMap[mapX][mapY] && !isWaterBlock(blockId)) {
                        continue;
                    }
                    
                    boolean isHighlighted = false;
                    if (highlightedBlock != null) {
                        Rectangle blockRect = new Rectangle(
                            screenX, 
                            screenY - blockSize, 
                            blockSize, 
                            blockSize
                        );
                        if (blockRect.contains(highlightedBlock)) {
                            if (!visibilityMap[mapX][mapY] || isWaterBlock(blockId)) {
                                isHighlighted = true;
                                highlightedBlockInfo = block;
                                highlightedBlockDepth = z;
                            }
                        }
                    }
                    
                    Color baseColor = blockColors.getOrDefault(blockId, 
                        hexToColor(BlockColorMapping.getColor(blockId)));
                    Color depthAdjustedColor = applyDepthFade(baseColor, z, blockId);
                    
                    if (isWaterBlock(blockId)) {
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                    } else {
                        g2d.setComposite(AlphaComposite.SrcOver);
                    }
                    
                    g2d.setColor(depthAdjustedColor);
                    g2d.fillRect(screenX, screenY - blockSize, blockSize, blockSize);
                    
                    if (blockSize >= 4) {
                        g2d.setColor(new Color(0, 0, 0, 32));
                        g2d.drawRect(screenX, screenY - blockSize, blockSize, blockSize);
                    }
                    
                    if (isHighlighted) {
                        g2d.setComposite(AlphaComposite.SrcOver);
                        g2d.setColor(new Color(255, 255, 0, 64));
                        g2d.fillRect(screenX, screenY - blockSize, blockSize, blockSize);
                        g2d.setColor(Color.YELLOW);
                        g2d.drawRect(screenX, screenY - blockSize, blockSize, blockSize);
                    }
                    
                    if (!isWaterBlock(blockId)) {
                        visibilityMap[mapX][mapY] = true;
                    }
                }
            }
        }
        
        if (spawnVisible) {
            float fadeFactor = (float)Math.pow(DEPTH_FADE_FACTOR, spawnDepth);
            g2d.setComposite(AlphaComposite.SrcOver);
            
            int markerSize = Math.max(blockSize, 8);
            
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(255, 0, 0, (int)(255 * fadeFactor)));
            
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

            String spawnText = "Spawn Point";
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(spawnText);
            
            g2d.setColor(new Color(0, 0, 0, (int)(180 * fadeFactor)));
            g2d.fillRect(
                spawnScreenX - textWidth/2 - 2,
                spawnScreenY - markerSize - metrics.getHeight() - 2,
                textWidth + 4,
                metrics.getHeight() + 4
            );
            
            g2d.setColor(new Color(255, 255, 255, (int)(255 * fadeFactor)));
            g2d.drawString(
                spawnText,
                spawnScreenX - textWidth/2,
                spawnScreenY - markerSize - metrics.getDescent() - 2
            );
        }
        
        g2d.setComposite(AlphaComposite.SrcOver);
        drawUIElements(g2d, highlightedBlockInfo);
    }

    private void drawUIElements(Graphics2D g2d, Block highlightedBlockInfo) {
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.drawLine(0, mapPanel.getHeight()/2, mapPanel.getWidth(), mapPanel.getHeight()/2);
        g2d.drawLine(mapPanel.getWidth()/2, 0, mapPanel.getWidth()/2, mapPanel.getHeight());
        
        String coords = String.format("X: %d, Y: %d, Z: %d (Spawn at X: %d, Y: %d, Z: %d)", 
            centerX, centerY, centerZ,
            spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillRect(5, 5, g2d.getFontMetrics().stringWidth(coords) + 10, 25);
        g2d.setColor(Color.BLACK);
        g2d.drawString(coords, 10, 20);
        
        if (highlightedBlock != null && highlightedBlockInfo != null) {
            int mouseX = highlightedBlock.x / blockSize - mapPanel.getWidth() / (2 * blockSize) + centerX;
            int mouseY = (mapPanel.getHeight() - highlightedBlock.y) / blockSize - mapPanel.getHeight() / (2 * blockSize) + centerY;
            int mouseZ = centerZ - highlightedBlockDepth;
            
            String blockInfo = String.format("Block: %s (x:%d, y:%d, z:%d, depth:%d)", 
                getBlockId(highlightedBlockInfo), mouseX, mouseY, mouseZ, highlightedBlockDepth);
            
            g2d.setColor(new Color(255, 255, 255, 220));
            int padding = 5;
            int textWidth = g2d.getFontMetrics().stringWidth(blockInfo);
            int textHeight = g2d.getFontMetrics().getHeight();
            g2d.fillRect(5, 30, textWidth + padding * 2, textHeight + padding);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(blockInfo, padding + 5, 45);
        }
    }
    
    private boolean isAirBlock(String blockId) {
        return blockId.contains("air");
    }
    
    private boolean isWaterBlock(String blockId) {
        return blockId.contains("water");
    }

    private Color hexToColor(String hexColor) {
        String hex = hexColor.replace("#", "");
        int rgb = Integer.parseInt(hex, 16);
        return new Color(
            (rgb >> 16) & 0xFF,
            (rgb >> 8) & 0xFF,
            rgb & 0xFF
        );
    }

    public void updateSeed(long seed) {
        SeedCheckerSettings.initialise();
        this.seedChecker = new SeedChecker(seed);
        this.spawnPos = seedChecker.getSpawnPos();
        seedInput.setText(String.valueOf(seed));
        mapPanel.repaint();
    }
}
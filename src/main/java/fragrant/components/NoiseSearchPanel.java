package fragrant.components;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.BufferedReader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileReader;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;

import fragrant.memory.SearchConditionStorage;
import fragrant.search.HeightSearchCondition;
import fragrant.search.NoiseSearchCondition;
import fragrant.settings.AppSettings;
import fragrant.MainUI;

import nl.kallestruik.noisesampler.NoiseSampler;
import nl.jellejurre.seedchecker.SeedChecker;
import nl.kallestruik.noisesampler.NoiseType;

public class NoiseSearchPanel extends JPanel {
    private final List<NoiseSearchCondition> searchConditions;
    private final List<Future<?>> searchTasks;
    private final MainUI mainWindow;
    private JTextField startSeedField;
    private JPanel conditionsPanel;
    private ExecutorService executorService;
    private volatile boolean isSearching = false;
    private final JButton startSearchButton;
    private final JButton stopSearchButton;
    private volatile long lastProcessedSeed = 0;
    private JSpinner threadCountSpinner;
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private JComboBox<String> seedRangeCombo;
    private JTextField fixedBitsField;
    private JTextField seedFileField;
    private JButton browseSeedFileButton;
    private final List<Long> seedsFromFile = new ArrayList<>();
    private CardLayout contentCardLayout;
    private JPanel contentPanel;
    private final List<HeightSearchCondition> heightConditions = new ArrayList<>();

    public NoiseSearchPanel(MainUI mainWindow) {
        this.mainWindow = mainWindow;
        this.searchConditions = new ArrayList<>();
        this.searchTasks = new ArrayList<>();

        setLayout(new BorderLayout());

        startSearchButton = new JButton("Start Search");
        stopSearchButton = new JButton("Stop Search");
        stopSearchButton.setEnabled(false);

        add(createRibbonPanel(), BorderLayout.NORTH);
        add(createConditionsPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        seedFileField.setEnabled(false);
        browseSeedFileButton.setEnabled(false);
        fixedBitsField.setEnabled(false);

        SearchConditionStorage.loadDefaultConditions(this).ifPresent(config -> {
            startSeedField.setText(String.valueOf(config.startSeed));

            if (config.searchMode != null) {
                seedRangeCombo.setSelectedItem(config.searchMode);
            }

            if (config.fixedBits != null) {
                fixedBitsField.setText(config.fixedBits);
            }
            if (config.seedFilePath != null) {
                seedFileField.setText(config.seedFilePath);
                File seedFile = new File(config.seedFilePath);
                if (seedFile.exists()) {
                    loadSeedsFromFile(seedFile);
                }
            }

            if (config.threadCount > 0) {
                threadCountSpinner.setValue(config.threadCount);
            }

            for (SearchConditionStorage.ConditionData conditionData : config.conditions) {
                NoiseSearchCondition condition = new NoiseSearchCondition(this);
                condition.setValues(
                        NoiseType.valueOf(conditionData.noiseType),
                        conditionData.minX, conditionData.maxX,
                        conditionData.minY, conditionData.maxY,
                        conditionData.minZ, conditionData.maxZ,
                        conditionData.threshold,
                        conditionData.thresholdConditionIndex,
                        conditionData.conditionTypeIndex
                );
                searchConditions.add(condition);
                conditionsPanel.add(condition);
            }

            if (config.heightConditions != null) {
                for (SearchConditionStorage.HeightConditionData heightData : config.heightConditions) {
                    HeightSearchCondition condition = new HeightSearchCondition(this);
                    condition.getMinXSpinner().setValue(heightData.minX);
                    condition.getMaxXSpinner().setValue(heightData.maxX);
                    condition.getMinZSpinner().setValue(heightData.minZ);
                    condition.getMaxZSpinner().setValue(heightData.maxZ);
                    condition.getMinHeightSpinner().setValue(heightData.minHeight);
                    condition.getMaxHeightSpinner().setValue(heightData.maxHeight);
                    heightConditions.add(condition);
                    conditionsPanel.add(condition);
                }
            }
            
            updateConditionsPanelSize();
            conditionsPanel.revalidate();
            conditionsPanel.repaint();
        });
    }

    private JPanel createRibbonPanel() {
        JPanel ribbonPanel = new JPanel();
        ribbonPanel.setLayout(new BoxLayout(ribbonPanel, BoxLayout.Y_AXIS));
        
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        
        JButton fileTab = createTabButton("Search Settings");
        JButton settingsTab = createTabButton("Advanced Settings");
        
        tabsPanel.add(fileTab);
        tabsPanel.add(settingsTab);
        
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setPreferredSize(new Dimension(0, 150));
        contentPanel.add(createSearchSettingsPanel(), "search");
        contentPanel.add(createAdvancedSettingsPanel(), "advanced");
        
        fileTab.addActionListener(e -> {
            contentCardLayout.show(contentPanel, "search");
            updateTabSelection(fileTab, settingsTab);
        });
        settingsTab.addActionListener(e -> {
            contentCardLayout.show(contentPanel, "advanced");
            updateTabSelection(settingsTab, fileTab);
        });
        
        fileTab.doClick();
        
        ribbonPanel.add(tabsPanel);
        ribbonPanel.add(contentPanel);
        
        return ribbonPanel;
    }    

    private JButton createTabButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(5, 15, 5, 15));
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        return button;
    }

    private void updateTabSelection(JButton selectedTab, JButton... otherTabs) {
        selectedTab.setFont(selectedTab.getFont().deriveFont(Font.BOLD));
        selectedTab.setBorderPainted(true);
        selectedTab.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, 
            UIManager.getColor("Component.focusColor")));
        
        for (JButton tab : otherTabs) {
            tab.setBorderPainted(false);
            tab.setFont(tab.getFont().deriveFont(Font.PLAIN));
        }
    }

    private JPanel createSearchSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(0, 150));
        
        JPanel searchModePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seedRangeCombo = new JComboBox<>(new String[] {
            "Full Range", "Fixed Lower 32 bits", 
            "Fixed Lower 48 bits", "Seeds from File"
        });
        
        seedRangeCombo.addActionListener(e -> {
            int selectedIndex = seedRangeCombo.getSelectedIndex();
            boolean isFullRange = selectedIndex == 0;
            boolean isFileMode = selectedIndex == 3;
            fixedBitsField.setEnabled(!isFullRange && !isFileMode);
            seedFileField.setEnabled(isFileMode);
            browseSeedFileButton.setEnabled(isFileMode);
        });
        
        searchModePanel.add(new JLabel("Search Mode:"));
        searchModePanel.add(seedRangeCombo);
        
        JPanel fixedBitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fixedBitsField = new JTextField("0", 20);
        fixedBitsPanel.add(new JLabel("Fixed Bits:"));
        fixedBitsPanel.add(fixedBitsField);
        
        JPanel seedFilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seedFileField = new JTextField(20);
        browseSeedFileButton = new JButton("Browse...");
        browseSeedFileButton.addActionListener(e -> browseForSeedFile());
        
        seedFilePanel.add(new JLabel("Seed File:"));
        seedFilePanel.add(seedFileField);
        seedFilePanel.add(browseSeedFileButton);
        
        panel.add(searchModePanel);
        panel.add(fixedBitsPanel);
        panel.add(seedFilePanel);
        
        return panel;
    }
    
    private JPanel createAdvancedSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(0, 150));
        
        JPanel startSeedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startSeedField = new JTextField("0", 20);
        startSeedPanel.add(new JLabel("Start Seed:"));
        startSeedPanel.add(startSeedField);
        
        JPanel threadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        threadCountSpinner = new JSpinner(new SpinnerNumberModel(
            MAX_THREADS / 2, 1, MAX_THREADS, 1
        ));
        threadPanel.add(new JLabel("Threads:"));
        threadPanel.add(threadCountSpinner);
        
        JPanel storagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveButton = new JButton("Save Settings");
        JButton loadButton = new JButton("Load Settings");
        
        saveButton.addActionListener(e -> saveCurrentSettings());
        loadButton.addActionListener(e -> loadSavedSettings());
        
        storagePanel.add(saveButton);
        storagePanel.add(loadButton);
        
        panel.add(startSeedPanel);
        panel.add(threadPanel);
        panel.add(storagePanel);
        
        return panel;
    }
    
    private JScrollPane createConditionsPanel() {
        conditionsPanel = new JPanel();
        conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(conditionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Conditions"));
        
        return scrollPane;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        JButton addConditionButton = new JButton("Add Noise Condition");
        JButton clearResultsButton = new JButton("Clear Results");
        JButton addHeightConditionButton = new JButton("Add Height Condition");

        addConditionButton.addActionListener(e -> addNoiseCondition());
        addHeightConditionButton.addActionListener(e -> addHeightCondition());
        startSearchButton.addActionListener(e -> startSearch());
        stopSearchButton.addActionListener(e -> stopSearch());
        clearResultsButton.addActionListener(e -> mainWindow.getResultPanel().clearResults());

        panel.add(addConditionButton);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(addHeightConditionButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(startSearchButton);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(stopSearchButton);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(clearResultsButton);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    private void browseForSeedFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            seedFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            loadSeedsFromFile(fileChooser.getSelectedFile());
        }
    }

    private void saveCurrentSettings() {
        long startSeed = 0;
        try {
            startSeed = Long.parseLong(startSeedField.getText());
        } catch (NumberFormatException ex) {
        }
        SearchConditionStorage.saveConditions(this, this, searchConditions, heightConditions, startSeed);
    }

    private void loadSavedSettings() {
        SearchConditionStorage.loadConditions(this).ifPresent(config -> {
            searchConditions.clear();
            heightConditions.clear();
            conditionsPanel.removeAll();

            startSeedField.setText(String.valueOf(config.startSeed));

            for (SearchConditionStorage.ConditionData conditionData : config.conditions) {
                NoiseSearchCondition condition = new NoiseSearchCondition(this);
                condition.setValues(
                        NoiseType.valueOf(conditionData.noiseType),
                        conditionData.minX, conditionData.maxX,
                        conditionData.minY, conditionData.maxY,
                        conditionData.minZ, conditionData.maxZ,
                        conditionData.threshold,
                        conditionData.thresholdConditionIndex,
                        conditionData.conditionTypeIndex
                );
                searchConditions.add(condition);
                conditionsPanel.add(condition);
            }

            if (config.heightConditions != null) {
                for (SearchConditionStorage.HeightConditionData heightData : config.heightConditions) {
                    HeightSearchCondition condition = new HeightSearchCondition(this);
                    condition.getMinXSpinner().setValue(heightData.minX);
                    condition.getMaxXSpinner().setValue(heightData.maxX);
                    condition.getMinZSpinner().setValue(heightData.minZ);
                    condition.getMaxZSpinner().setValue(heightData.maxZ);
                    condition.getMinHeightSpinner().setValue(heightData.minHeight);
                    condition.getMaxHeightSpinner().setValue(heightData.maxHeight);
                    heightConditions.add(condition);
                    conditionsPanel.add(condition);
                }
            }

            updateConditionsPanelSize();
            conditionsPanel.revalidate();
            conditionsPanel.repaint();
        });
    }

    private void loadSeedsFromFile(File file) {
        seedsFromFile.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    long seed = Long.parseLong(line.trim());
                    seedsFromFile.add(seed);
                } catch (NumberFormatException e) {
                }
            }
            JOptionPane.showMessageDialog(this,
                String.format("Loaded %d seeds from file", seedsFromFile.size()),
                "Load Complete",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load seeds from file: " + e.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startSearch() {
        mainWindow.getResultPanel().startProcessing();
        if (searchConditions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one noise condition.",
                    "No Conditions",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        try {
            final int searchMode = seedRangeCombo.getSelectedIndex();
    
            if (searchMode == 3 && seedsFromFile.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please load a seed file first.",
                        "No Seeds Loaded",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            String startSeedText = startSeedField.getText();
            if (!startSeedText.matches("-?\\d+")) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for the starting seed.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            final long startSeed = Long.parseLong(startSeedText);
    
            String fixedBitsText = fixedBitsField.getText();
            final long fixedBits;
            if (searchMode != 0) {
                if (fixedBitsText.isEmpty() || !fixedBitsText.matches("-?\\d+")) { 
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid number for the fixed bits.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                fixedBits = Long.parseLong(fixedBitsText);
            } else {
                fixedBits = 0;
            }
    
            System.out.println("Fixed Bits: " + fixedBits);
    
            final long mask;
            final long fixedBitsMask;
            final long stepSize;
    
            switch (searchMode) {
                case 1:
                    mask = 0xFFFFFFFF00000000L;
                    fixedBitsMask = 0x00000000FFFFFFFFL;
                    stepSize = 1L << 32;
                    break;
                case 2:
                    mask = 0xFFFF000000000000L;
                    fixedBitsMask = 0x0000FFFFFFFFFFFFL;
                    stepSize = 1L << 48;
                    break;
                default:
                    mask = 0xFFFFFFFFFFFFFFFFL;
                    fixedBitsMask = 0L;
                    stepSize = 1;
            }
    
            isSearching = true;
            startSearchButton.setEnabled(false);
            stopSearchButton.setEnabled(true);
            threadCountSpinner.setEnabled(false);
            mainWindow.startSearch();
    
            int threadCount = (Integer) threadCountSpinner.getValue();
            executorService = Executors.newFixedThreadPool(threadCount);
            searchTasks.clear();
    
            if (searchMode == 3) {
                int seedsPerThread = seedsFromFile.size() / threadCount;
                int remainingSeeds = seedsFromFile.size() % threadCount;
    
                for (int i = 0; i < threadCount; i++) {
                    final int startIndex = i * seedsPerThread + Math.min(i, remainingSeeds);
                    final int endIndex = (i + 1) * seedsPerThread + Math.min(i + 1, remainingSeeds);
    
                    Future<?> task = executorService.submit(() -> {
                        for (int j = startIndex; j < endIndex && !Thread.currentThread().isInterrupted() && isSearching; j++) {
                            long seed = seedsFromFile.get(j);
                            if (checkSeed(seed)) {
                                SwingUtilities.invokeLater(() ->
                                        mainWindow.getResultPanel().addSeed(seed));
                            }
                            lastProcessedSeed = seed;
                            mainWindow.incrementProcessedSeeds();
                        }
                    });
                    searchTasks.add(task);
                }
            } else {
                for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                    final int currentThreadIndex = threadIndex;
                    Future<?> task = executorService.submit(() -> {
                        long initialSeed = (startSeed + currentThreadIndex * stepSize) & mask;
                        if (searchMode != 0) {
                            initialSeed |= (fixedBits & fixedBitsMask);
                        }
    
                        long seed = initialSeed;
    
                        while (!Thread.currentThread().isInterrupted() && isSearching) {
                            if (checkSeed(seed)) {
                                final long foundSeed = seed;
                                SwingUtilities.invokeLater(() -> mainWindow.getResultPanel().addSeed(foundSeed));
                            }
                            lastProcessedSeed = seed;
    
                            seed = ((seed + (threadCount * stepSize)) & mask) |
                                    (fixedBits & fixedBitsMask);
    
                            mainWindow.incrementProcessedSeeds();
                        }
                    });
                    searchTasks.add(task);
                }
            }
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for the seeds.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (mainWindow.getResultPanel().getSeedListModel().size() >= AppSettings.getMaxSeeds()) {
            stopSearch();
            return;
        }
    }
    
    private void stopSearch() {
        isSearching = false;
        if (executorService != null) {
            executorService.shutdownNow();
            searchTasks.forEach(task -> task.cancel(true));
        }
        startSearchButton.setEnabled(true);
        stopSearchButton.setEnabled(false);
        threadCountSpinner.setEnabled(true);
        mainWindow.stopSearch();
        startSeedField.setText(String.valueOf(lastProcessedSeed + 1));
        mainWindow.getResultPanel().stopProcessing();
    }

    private void addNoiseCondition() {
        NoiseSearchCondition condition = new NoiseSearchCondition(this);
        searchConditions.add(condition);
        conditionsPanel.add(condition);
        updateConditionsPanelSize();
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    private void updateConditionsPanelSize() {
        int preferredHeight = Math.min(300, searchConditions.size() * 250);
        JScrollPane scrollPane = (JScrollPane) conditionsPanel.getParent().getParent();
        scrollPane.setPreferredSize(new Dimension(
                conditionsPanel.getPreferredSize().width,
                preferredHeight));
        conditionsPanel.revalidate();
    }

    private boolean checkSeed(long seed) {
        NoiseSampler sampler = new NoiseSampler(seed, nl.kallestruik.noisesampler.minecraft.Dimension.OVERWORLD);
        if (!searchConditions.stream().allMatch(condition -> condition.checkCondition(sampler))) {
            return false;
        }
        
        if (!heightConditions.isEmpty()) {
            SeedChecker checker = new SeedChecker(seed);
            if (!heightConditions.stream().allMatch(condition -> condition.checkCondition(checker))) {
                return false;
            }
        }
    
        SwingUtilities.invokeLater(() -> mainWindow.getResultPanel().addSeed(seed));
        return false;
    }
    
    public void removeCondition(NoiseSearchCondition condition) {
        searchConditions.remove(condition);
        conditionsPanel.remove(condition);
        updateConditionsPanelSize();
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    private void addHeightCondition() {
        HeightSearchCondition condition = new HeightSearchCondition(this);
        heightConditions.add(condition);
        conditionsPanel.add(condition);
        updateConditionsPanelSize();
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    public void removeHeightCondition(HeightSearchCondition condition) {
        heightConditions.remove(condition);
        conditionsPanel.remove(condition);
        updateConditionsPanelSize();
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    public JComboBox<String> getSeedRangeCombo() {
        return seedRangeCombo;
    }

    public JTextField getFixedBitsField() {
        return fixedBitsField;
    }

    public JTextField getSeedFileField() {
        return seedFileField;
    }

    public JSpinner getThreadCountSpinner() {
        return threadCountSpinner;
    }

    public List<NoiseSearchCondition> getSearchConditions() {
        return searchConditions;
    }

    public JTextField getStartSeedField() {
        return startSeedField;
    }

    public List<HeightSearchCondition> getHeightConditions() {
        return heightConditions;
    }
}
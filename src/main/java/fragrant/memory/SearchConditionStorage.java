package fragrant.memory;

import fragrant.search.HeightSearchCondition;
import fragrant.components.NoiseSearchPanel;
import fragrant.search.NoiseSearchCondition;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Component;
import java.nio.file.Path;
import javax.swing.*;
import java.util.*;
import java.io.*;

import com.google.gson.*;

public class SearchConditionStorage {
    private static final String FILE_EXTENSION = ".json";
    private static final String DEFAULT_CONFIG_FILE = ".noise_sampler_default_config.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static class ConditionData {
        public String noiseType;
        public int minX;
        public int maxX;
        public int minY;
        public int maxY;
        public int minZ;
        public int maxZ;
        public double threshold;
        public int thresholdConditionIndex;
        public int conditionTypeIndex;

        public static ConditionData fromCondition(NoiseSearchCondition condition) {
            ConditionData data = new ConditionData();
            data.noiseType = condition.getNoiseType().name();
            data.minX = condition.getMinX();
            data.maxX = condition.getMaxX();
            data.minY = condition.getMinY();
            data.maxY = condition.getMaxY();
            data.minZ = condition.getMinZ();
            data.maxZ = condition.getMaxZ();
            data.threshold = condition.getThreshold();
            data.thresholdConditionIndex = condition.getThresholdConditionIndex();
            data.conditionTypeIndex = condition.getConditionTypeIndex();
            return data;
        }
    }

    public static class HeightConditionData {
        public int minX;
        public int maxX;
        public int minZ;
        public int maxZ;
        public int minHeight;
        public int maxHeight;
        public String blockType;
    
        public HeightConditionData() {}
    
        public HeightConditionData(int minX, int maxX, int minZ, int maxZ, 
                                 int minHeight, int maxHeight, String blockType) {
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.blockType = blockType;
        }
    
        public static HeightConditionData fromCondition(HeightSearchCondition condition) {
            HeightConditionData data = new HeightConditionData();
            data.minX = (Integer) condition.getMinXSpinner().getValue();
            data.maxX = (Integer) condition.getMaxXSpinner().getValue();
            data.minZ = (Integer) condition.getMinZSpinner().getValue();
            data.maxZ = (Integer) condition.getMaxZSpinner().getValue();
            data.minHeight = (Integer) condition.getMinHeightSpinner().getValue();
            data.maxHeight = (Integer) condition.getMaxHeightSpinner().getValue();
            return data;
        }
    }    

    public static class SearchConfig {
        public long startSeed;
        public String searchMode;
        public String fixedBits;
        public String seedFilePath;
        public int threadCount;
        public List<ConditionData> conditions;
        public List<HeightConditionData> heightConditions;

        public SearchConfig() {
            conditions = new ArrayList<>();
            heightConditions = new ArrayList<>();
        }
    }

    public static void saveConditions(Component parent, NoiseSearchPanel panel,
                                      List<NoiseSearchCondition> conditions, long startSeed) {
        saveConditions(parent, panel, conditions, panel.getHeightConditions(), startSeed);
    }

    public static void saveConditions(Component parent, NoiseSearchPanel panel,
                                      List<NoiseSearchCondition> conditions,
                                      List<HeightSearchCondition> heightConditions,
                                      long startSeed) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a directory to save search conditions");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            File file = new File(selectedDirectory, "search_conditions.json");

            try {
                SearchConfig config = createSearchConfig(panel, conditions, heightConditions, startSeed);

                try (Writer writer = new FileWriter(file)) {
                    gson.toJson(config, writer);
                }

                JOptionPane.showMessageDialog(parent,
                        "Search conditions saved to: " + file.getAbsolutePath(),
                        "Save Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to save search conditions: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static SearchConfig createSearchConfig(NoiseSearchPanel panel,
                                                   List<NoiseSearchCondition> conditions,
                                                   List<HeightSearchCondition> heightConditions,
                                                   long startSeed) {
        SearchConfig config = new SearchConfig();
        config.startSeed = startSeed;
        config.conditions = conditions.stream()
                .map(ConditionData::fromCondition)
                .toList();

        config.heightConditions = heightConditions.stream()
                .map(HeightConditionData::fromCondition)
                .toList();

        config.searchMode = (String) panel.getSeedRangeCombo().getSelectedItem();
        config.fixedBits = panel.getFixedBitsField().getText();
        config.seedFilePath = panel.getSeedFileField().getText();
        config.threadCount = (Integer) panel.getThreadCountSpinner().getValue();

        return config;
    }

    public static Optional<SearchConfig> loadConditions(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION);
            }

            public String getDescription() {
                return "Search Conditions (*" + FILE_EXTENSION + ")";
            }
        });

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try (Reader reader = new FileReader(fileChooser.getSelectedFile())) {
                SearchConfig config = gson.fromJson(reader, SearchConfig.class);
                return Optional.of(config);

            } catch (IOException | JsonSyntaxException e) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to load search conditions: " + e.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        return Optional.empty();
    }

    public static void saveDefaultConditions(Component parent, NoiseSearchPanel panel,
                                             List<NoiseSearchCondition> conditions,
                                             List<HeightSearchCondition> heightConditions,
                                             long startSeed) {
        try {
            SearchConfig config = createSearchConfig(panel, conditions, heightConditions, startSeed);

            Path configDir = Paths.get(System.getProperty("user.home"), ".noise_sampler");
            Files.createDirectories(configDir);

            Path configPath = configDir.resolve(DEFAULT_CONFIG_FILE);

            System.out.println("Saving configuration with " +
                    config.conditions.size() + " noise conditions and " +
                    config.heightConditions.size() + " height conditions");

            try (Writer writer = new FileWriter(configPath.toFile())) {
                gson.toJson(config, writer);
                System.out.println("Default configuration saved successfully to: " + configPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Failed to save default configuration: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Optional<SearchConfig> loadDefaultConditions(Component parent) {
        try {
            Path configPath = Paths.get(System.getProperty("user.home"), ".noise_sampler", DEFAULT_CONFIG_FILE);
            if (!Files.exists(configPath)) {
                System.out.println("No default configuration found.");
                return Optional.empty();
            }

            try (Reader reader = new FileReader(configPath.toFile())) {
                SearchConfig config = gson.fromJson(reader, SearchConfig.class);
                System.out.println("Loaded configuration with " + config.conditions.size() +
                        " noise conditions and " +
                        (config.heightConditions != null ? config.heightConditions.size() : 0) +
                        " height conditions");
                return Optional.of(config);
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Failed to load default configuration: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }
}
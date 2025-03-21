package fragrant.memory;

import fragrant.search.HeightSearchCondition;
import fragrant.search.NoiseSearchCondition;
import fragrant.search.BiomeSearchCondition;
import fragrant.components.SearchPanel;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Component;
import java.nio.file.Path;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static class NoiseConditionData {
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

        public static NoiseConditionData fromCondition(NoiseSearchCondition condition) {
            NoiseConditionData data = new NoiseConditionData();
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

        public HeightConditionData() {}

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

    public static class BiomeConditionData {
        public String biome;
        public int minX;
        public int maxX;
        public int minZ;
        public int maxZ;
        public int conditionTypeIndex;
        
        public BiomeConditionData() {}

        public static BiomeConditionData fromCondition(BiomeSearchCondition condition) {
            BiomeConditionData data = new BiomeConditionData();
            data.biome = condition.getBiome().name();
            data.minX = condition.getMinX();
            data.maxX = condition.getMaxX();
            data.minZ = condition.getMinZ();
            data.maxZ = condition.getMaxZ();
            data.conditionTypeIndex = condition.getConditionTypeIndex();
            return data;
        }
    }

    public static class SearchConfig {
        public long startSeed;
        public String searchMode;
        public String fixedBits;
        public String seedFilePath;
        public int threadCount;
        public List<NoiseConditionData> noiseConditionData;
        public List<HeightConditionData> heightConditions;
        public List<BiomeConditionData> biomeConditions;

        public SearchConfig() {
            noiseConditionData = new ArrayList<>();
            heightConditions = new ArrayList<>();
            biomeConditions = new ArrayList<>();
        }
    }

    public static void saveConditions(Component parent, SearchPanel panel,
                                    List<NoiseSearchCondition> conditions,
                                    List<HeightSearchCondition> heightConditions,
                                    List<BiomeSearchCondition> biomeConditions,
                                    long startSeed) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a directory to save search conditions");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            File file = new File(selectedDirectory, "search_conditions.json");

            try {
                SearchConfig config = createSearchConfig(panel, conditions, heightConditions, biomeConditions, startSeed);

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

    private static SearchConfig createSearchConfig(SearchPanel panel,
                                                 List<NoiseSearchCondition> noiseConditionData,
                                                 List<HeightSearchCondition> heightConditions,
                                                 List<BiomeSearchCondition> biomeConditions,
                                                 long startSeed) {
        SearchConfig config = new SearchConfig();
        config.startSeed = startSeed;
        config.noiseConditionData = noiseConditionData.stream()
                .map(NoiseConditionData::fromCondition)
                .toList();

        config.heightConditions = heightConditions.stream()
                .map(HeightConditionData::fromCondition)
                .toList();

        config.biomeConditions = biomeConditions.stream()
                .map(BiomeConditionData::fromCondition)
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
            File selectedFile = fileChooser.getSelectedFile();
            try (Reader reader = new FileReader(selectedFile)) {
                SearchConfig config = gson.fromJson(reader, SearchConfig.class);

                if (config == null) {
                    throw new JsonSyntaxException("Failed to parse config file: empty or invalid content");
                }

                if (config.noiseConditionData == null) {
                    config.noiseConditionData = new ArrayList<>();
                }
                if (config.heightConditions == null) {
                    config.heightConditions = new ArrayList<>();
                }
                if (config.biomeConditions == null) {
                    config.biomeConditions = new ArrayList<>();
                }

                return Optional.of(config);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();

                try {
                    String timestamp = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    Path backupPath = selectedFile.toPath().resolveSibling(
                            selectedFile.getName() + ".backup." + timestamp);
                    Files.copy(selectedFile.toPath(), backupPath);
                    System.out.println("Corrupted file backed up to: " + backupPath);
                } catch (Exception backupEx) {
                    backupEx.printStackTrace();
                }

                JOptionPane.showMessageDialog(parent,
                        "Failed to load search conditions: " + e.getMessage() +
                                "\nA backup of the corrupt file has been created if possible.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        return Optional.empty();
    }

    public static void saveDefaultConditions(Component parent, SearchPanel panel,
                                           List<NoiseSearchCondition> noiseConditionData,
                                           List<HeightSearchCondition> heightConditions,
                                           List<BiomeSearchCondition> biomeConditions,
                                           long startSeed) {
        try {
            SearchConfig config = createSearchConfig(panel, noiseConditionData, heightConditions, biomeConditions, startSeed);

            Path configDir = Paths.get(System.getProperty("user.home"), ".noise_samplerGUI-1.5.0");
            Files.createDirectories(configDir);

            Path configPath = configDir.resolve(DEFAULT_CONFIG_FILE);

            System.out.println("Saving configuration with " +
                    config.noiseConditionData.size() + " noise conditions & " +
                    config.heightConditions.size() + " height conditions & " +
                    config.biomeConditions.size() + " biome conditions");

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
            Path configPath = Paths.get(System.getProperty("user.home"), ".noise_samplerGUI-1.5.0", DEFAULT_CONFIG_FILE);
            if (!Files.exists(configPath)) {
                System.out.println("No default configuration found.");
                return Optional.empty();
            }

            try (Reader reader = new FileReader(configPath.toFile())) {
                SearchConfig config = gson.fromJson(reader, SearchConfig.class);

                if (config == null) {
                    throw new JsonSyntaxException("Failed to parse config file: empty or invalid content");
                }
                if (config.noiseConditionData == null) {
                    config.noiseConditionData = new ArrayList<>();
                }
                if (config.heightConditions == null) {
                    config.heightConditions = new ArrayList<>();
                }
                if (config.biomeConditions == null) {
                    config.biomeConditions = new ArrayList<>();
                }

                System.out.println("Loaded configuration with " +
                        config.noiseConditionData.size() + " noise conditions & " +
                        config.heightConditions.size() + " height conditions & " +
                        config.biomeConditions.size() + " biome conditions");
                return Optional.of(config);
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();

            try {
                Path configPath = Paths.get(System.getProperty("user.home"), ".noise_samplerGUI-1.5.0", DEFAULT_CONFIG_FILE);
                if (Files.exists(configPath)) {
                    String timestamp = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    Path backupPath = configPath.resolveSibling(DEFAULT_CONFIG_FILE + ".backup." + timestamp);
                    Files.copy(configPath, backupPath);
                    System.out.println("Corrupted configuration file backed up to: " + backupPath);
                }
            } catch (Exception backupEx) {
                backupEx.printStackTrace();
            }

            JOptionPane.showMessageDialog(parent,
                    "Failed to load default configuration: " + e.getMessage() +
                            "\nA backup of the corrupt file has been created if possible.",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }
}
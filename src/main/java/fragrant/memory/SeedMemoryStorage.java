package fragrant.memory;

import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.nio.file.*;
import java.util.List;
import java.io.*;
import javax.swing.JOptionPane;
import java.awt.Component;

public class SeedMemoryStorage {
    private static final String STORAGE_FILE = "seed_memories.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new SeedMemory.LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new SeedMemory.LocalDateTimeDeserializer())
            .registerTypeAdapter(SeedMemory.class, new SeedMemory.SeedMemoryAdapter())
            .setPrettyPrinting()
            .create();

    public static void saveMemories(List<SeedMemory> memories) {
        saveMemories(memories, null);
    }

    public static void saveMemories(List<SeedMemory> memories, Component parent) {
        try {
            ensureStorageDirectory();
            String json = gson.toJson(memories);
            Files.write(getStorageFilePath(), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            if (parent != null) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to save seed memories: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static List<SeedMemory> loadMemories() {
        return loadMemories(null);
    }

    public static List<SeedMemory> loadMemories(Component parent) {
        try {
            if (!Files.exists(getStorageFilePath())) {
                return new ArrayList<>();
            }
            String json = new String(Files.readAllBytes(getStorageFilePath()));
            Type listType = new TypeToken<ArrayList<SeedMemory>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            if (parent != null) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to parse seed memories file: " + e.getMessage() +
                                "\nCreating a new empty list. Your previous data has been backed up.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
                backupCorruptFile();
            }
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            if (parent != null) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to load seed memories: " + e.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return new ArrayList<>();
        }
    }

    private static void backupCorruptFile() {
        try {
            Path originalPath = getStorageFilePath();
            if (Files.exists(originalPath)) {
                String timestamp = LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                Path backupPath = originalPath.resolveSibling(STORAGE_FILE + ".backup." + timestamp);
                Files.copy(originalPath, backupPath);
                System.out.println("Corrupted file backed up to: " + backupPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getStorageFilePath() {
        return Paths.get(System.getProperty("user.home"), ".noise_samplerGUI-1.5.0", STORAGE_FILE);
    }

    public static void ensureStorageDirectory() {
        try {
            Path directory = getStorageFilePath().getParent();
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
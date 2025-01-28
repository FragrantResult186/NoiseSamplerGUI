package fragrant.memory;

import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.nio.file.*;
import java.util.List;
import java.io.*;

public class SeedMemoryStorage {
    private static final String STORAGE_FILE = "seed_memories.json";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new SeedMemory.LocalDateTimeSerializer())
        .registerTypeAdapter(LocalDateTime.class, new SeedMemory.LocalDateTimeDeserializer())
        .setPrettyPrinting()
        .create();

    public static void saveMemories(List<SeedMemory> memories) {
        try {
            String json = gson.toJson(memories);
            Files.write(getStorageFilePath(), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SeedMemory> loadMemories() {
        try {
            if (!Files.exists(getStorageFilePath())) {
                return new ArrayList<>();
            }
            String json = new String(Files.readAllBytes(getStorageFilePath()));
            Type listType = new TypeToken<ArrayList<SeedMemory>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Path getStorageFilePath() {
        return Paths.get(System.getProperty("user.home"), ".noise_sampler", STORAGE_FILE);
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

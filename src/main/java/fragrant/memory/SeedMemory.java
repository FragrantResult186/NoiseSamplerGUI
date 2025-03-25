package fragrant.memory;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.lang.reflect.Type;
import com.google.gson.*;

public record SeedMemory(long seed, String description) {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static class SeedMemoryAdapter implements JsonSerializer<SeedMemory>, JsonDeserializer<SeedMemory> {
        @Override
        public JsonElement serialize(SeedMemory src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("seed", src.seed());
            jsonObject.addProperty("description", src.description());
            return jsonObject;
        }

        @Override
        public SeedMemory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            long seed = jsonObject.get("seed").getAsLong();
            String description = jsonObject.has("description") ?
                    jsonObject.get("description").getAsString() : "";
            return new SeedMemory(seed, description);
        }
    }

    @Override
    public String toString() {
        return String.format("%d%s",
                seed,
                description);
    }

    public static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATE_FORMAT));
        }
    }

    public static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), DATE_FORMAT);
        }
    }
}
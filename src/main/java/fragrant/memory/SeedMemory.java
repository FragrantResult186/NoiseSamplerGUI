package fragrant.memory;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.lang.reflect.Type;
import com.google.gson.*;

public class SeedMemory {
    private final long seed;
    private final String description;
    private final LocalDateTime foundTime;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public SeedMemory(long seed, String description) {
        this.seed = seed;
        this.description = description;
        this.foundTime = LocalDateTime.now();
    }

    public long getSeed() {
        return seed;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getFoundTime() {
        return foundTime;
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

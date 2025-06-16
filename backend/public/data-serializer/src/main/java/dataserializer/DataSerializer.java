package dataserializer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Optional;


/**
 * DataSerializer
 * Author: MyungJoo
 * Date: 2025-06-17
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSerializer {

    private static final ObjectMapper objectMapper = initialize();

    private static ObjectMapper initialize() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Object → JSON 문자열
     */
    public static Optional<String> serialize(Object object) {
        try {
            return Optional.of(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.warn("[serialize] Failed to serialize object. type={}, error={}",
                    object != null ? object.getClass().getSimpleName() : "null",
                    e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * JSON 문자열 → 객체
     */
    public static <T> Optional<T> deserialize(String json, Class<T> clazz) {
        try {
            return Optional.of(objectMapper.readValue(json, clazz));
        } catch (Exception e) {
            log.warn("[deserialize] Failed to parse JSON. clazz={}, error={}",
                    clazz.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Map 혹은 Object → DTO 변환
     */
    public static <T> Optional<T> convert(Object source, Class<T> targetType) {
        try {
            return Optional.of(objectMapper.convertValue(source, targetType));
        } catch (IllegalArgumentException e) {
            log.warn("[convert] Failed to convert. sourceType={}, targetType={}, error={}",
                    source != null ? source.getClass().getSimpleName() : "null",
                    targetType.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 직렬화된 JSON 문자열을 byte[]로 변환 (Kafka용)
     */
    public static Optional<byte[]> serializeToBytes(Object object) {
        return serialize(object).map(json -> json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * byte[] → 역직렬화 (Kafka용)
     */
    public static <T> Optional<T> deserializeFromBytes(byte[] data, Class<T> clazz) {
        if (data == null) return Optional.empty();
        String json = new String(data, StandardCharsets.UTF_8);
        return deserialize(json, clazz);
    }
}
package dataserializer;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * DataSerializer
 * Author: MyungJoo
 * Date: 2025-06-17
 */
class DataSerializerTest {

    static class TestDto {
        public String name;
        public int age;

        // 생성자 필요 시 추가
        public TestDto() {}
        public TestDto(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    void serialize_shouldReturnJsonString() {
        TestDto dto = new TestDto("Alice", 30);

        String json = DataSerializer.serialize(dto).orElse(null);

        assertThat(json).isNotNull();
        assertThat(json).contains("\"name\":\"Alice\"");
    }

    @Test
    void deserialize_shouldParseValidJson() {
        String json = "{\"name\":\"Bob\",\"age\":25}";

        TestDto dto = DataSerializer.deserialize(json, TestDto.class).orElse(null);

        assertThat(dto).isNotNull();
        assertThat(dto.name).isEqualTo("Bob");
        assertThat(dto.age).isEqualTo(25);
    }

    @Test
    void convert_shouldMapBetweenObjects() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Charlie");
        map.put("age", 40);

        TestDto dto = DataSerializer.convert(map, TestDto.class).orElse(null);

        assertThat(dto).isNotNull();
        assertThat(dto.name).isEqualTo("Charlie");
    }

    @Test
    void serializeToBytes_shouldReturnValidUtf8Bytes() {
        TestDto dto = new TestDto("Dana", 20);

        byte[] bytes = DataSerializer.serializeToBytes(dto).orElse(null);

        assertThat(bytes).isNotNull();
        String json = new String(bytes);
        assertThat(json).contains("\"name\":\"Dana\"");
    }

    @Test
    void deserializeFromBytes_shouldParseValidBytes() {
        String json = "{\"name\":\"Eve\",\"age\":28}";
        byte[] bytes = json.getBytes();

        TestDto dto = DataSerializer.deserializeFromBytes(bytes, TestDto.class).orElse(null);

        assertThat(dto).isNotNull();
        assertThat(dto.name).isEqualTo("Eve");
    }

    @Test
    void errorHandling_shouldReturnEmptyOptional() {
        String invalidJson = "{ name: 'MissingQuotes' }";

        var result = DataSerializer.deserialize(invalidJson, TestDto.class);

        assertThat(result).isEmpty();
    }
}
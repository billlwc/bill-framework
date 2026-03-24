package bill.framework.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private JSONUtil() {}

    /**
     * 1. 对象转 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * 2. JSON 字符串转对象
     */
    public static <T> T toObj(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }

    /**
     * 3. 对象转另一类型对象（直接内存转换，无需序列化为字符串）
     */
    public static <T> T toObj(Object obj, Class<T> clazz) {
        if (obj == null) return null;
        if (clazz.isInstance(obj)) return clazz.cast(obj);
        return MAPPER.convertValue(obj, clazz);
    }

    /**
     * 4. JSON 字符串转 List / Map 等复杂泛型结构
     */
    public static <T> T toObj(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) return null;
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to generic type", e);
        }
    }

    /**
     * 5. 对象转 JsonNode（直接内存转换）
     */
    public static JsonNode toJsonNode(Object obj) {
        if (obj == null) return null;
        try {
            return MAPPER.valueToTree(obj);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert object to JsonNode", e);
        }
    }

    /**
     * 6. JSON 字符串转 JsonNode
     */
    public static JsonNode toJsonNode(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON string to JsonNode", e);
        }
    }

    /**
     * 7. JSON 字符串转 List<T>
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return Collections.emptyList();
        try {
            return MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to List", e);
        }
    }

    /**
     * 8. 对象转 List<T>（先序列化再反序列化）
     */
    public static <T> List<T> toList(Object obj, Class<T> clazz) {
        return toList(toJson(obj), clazz);
    }

    /**
     * 9. JSON 字符串转 List<Map<String, Object>>
     */
    public static List<Map<String, Object>> toListMap(String json) {
        if (json == null || json.isEmpty()) return Collections.emptyList();
        try {
            return MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to List<Map>", e);
        }
    }

    /**
     * 10. JSON 字符串转 Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        if (json == null || json.isEmpty()) return Collections.emptyMap();
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to Map", e);
        }
    }

    /**
     * 11. 对象转 Map<String, Object>
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) return Collections.emptyMap();
        return MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 12. JSON 字符串转 ArrayNode
     */
    public static ArrayNode toArrayNode(String json) {
        JsonNode node = toJsonNode(json);
        if (node == null) return MAPPER.createArrayNode();
        if (!node.isArray()) {
            throw new RuntimeException("JSON is not an array");
        }
        return (ArrayNode) node;
    }

    /**
     * 13. 创建空 ArrayNode（方便手动构建 JSON 数组）
     */
    public static ArrayNode createArrayNode() {
        return MAPPER.createArrayNode();
    }

    /**
     * 14. 判断字符串是否为合法 JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) return false;
        try {
            MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 15. 获取内部 ObjectMapper（需要自定义配置时使用）
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

}

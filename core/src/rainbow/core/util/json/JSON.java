package rainbow.core.util.json;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import rainbow.core.util.Utils;

public class JSON {

	private static final ObjectMapper mapper;
	public static final TypeReference<LinkedHashMap<String, Object>> mapType = new TypeReference<LinkedHashMap<String, Object>>() {
	};

	static {
		mapper = JsonMapper.builder() //
				.addModule(new JavaTimeModule()) // 支持Java8的时间
				.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES) // 允许用单引号
				.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES) // 允许Key没有引号
				.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS) // 允许字符串中存在回车换行控制符
				.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS) //
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // 允许有未知属性，忽略不报错
				.build();
	}

	public static String toJSONString(Object obj) {
		return toJSONString(obj, false);
	}

	public static String toJSONString(Object obj, boolean format) {
		try {
			return format ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
					: mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static void toJSON(Object obj, Path file, boolean format) {
		try (Writer writer = Files.newBufferedWriter(file)) {
			toJSON(obj, writer, format);
		} catch (IOException e) {
			throw new RuntimeException(String.format("write json file %s failed", file.getFileName().toString()), e);
		}
	}

	public static void toJSON(Object obj, OutputStream os, boolean format) {
		try {
			if (format) {
				mapper.writerWithDefaultPrettyPrinter().writeValue(os, obj);
			} else {
				mapper.writeValue(os, obj);
			}
		} catch (IOException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static void toJSON(Object obj, Writer writer, boolean format) {
		try {
			if (format) {
				mapper.writerWithDefaultPrettyPrinter().writeValue(writer, obj);
			} else {
				mapper.writeValue(writer, obj);
			}
		} catch (IOException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static <T> T parseObject(Path file, Class<T> clazz) {
		if (!Files.isReadable(file))
			return null;
		try (InputStream is = Files.newInputStream(file)) {
			return parseObject(is, clazz);
		} catch (IOException e) {
			throw new RuntimeException(String.format("read json file %s failed", file.getFileName().toString()), e);
		}
	}

	public static <T> T parseObject(InputStream is, Class<T> clazz) {
		try {
			return mapper.readValue(is, clazz);
		} catch (IOException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static <T> T parseObject(String json, Class<T> clazz) {
		if (!Utils.hasContent(json))
			return null;
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static <T> T parseObject(String json, TypeReference<T> type) {
		if (!Utils.hasContent(json))
			return null;
		try {
			return mapper.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static <T> List<T> parseList(String json, Class<T> clazz) {
		if (!Utils.hasContent(json))
			return null;
		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
			return mapper.readValue(json, javaType);
		} catch (JsonProcessingException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static Map<String, Object> parseObject(Path file) {
		if (!Files.isReadable(file))
			return null;
		try (InputStream is = Files.newInputStream(file)) {
			return mapper.readValue(is, mapType);
		} catch (IOException e) {
			throw new RuntimeException(String.format("read json file %s failed", file.getFileName().toString()), e);
		}
	}

	public static Map<String, Object> parseObject(String json) {
		if (!Utils.hasContent(json))
			return null;
		try {
			return mapper.readValue(json, mapType);
		} catch (JsonProcessingException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static Map<String, Object> toMap(String json, Map<String, Type> typeMap) {
		Map<String, Object> result = new HashMap<>();
		try (JsonParser parser = mapper.getFactory().createParser(json)) {
			while (true) {
				JsonToken token = parser.nextToken();
				if (token.equals(JsonToken.END_OBJECT))
					break;
				if (token.equals(JsonToken.FIELD_NAME)) {
					String name = parser.currentName();
					Type type = checkNotNull(typeMap.get(name), "type of '{}' not exist", name);
					JavaType jt = mapper.constructType(type);
					parser.nextToken();
					result.put(name, mapper.readValue(parser, jt));
				}
			}
			return result;
		} catch (IOException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

}
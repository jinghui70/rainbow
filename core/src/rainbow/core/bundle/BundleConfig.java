package rainbow.core.bundle;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import rainbow.core.platform.Platform;
import rainbow.core.util.Utils;

/**
 * 
 * 配置加载器，用于各模块的配置读取，未来支持配置动态修改保存。
 * 
 * 配置信息存在位置可以是独立的配置文件，也可以寄生在主配置文件中。
 * 
 * 有独立的配置文件，就不去读取主配置文件。
 * 
 * 配置文件是.json文件，以.dev为后缀的是开发用的配置文件。开发时开发配置会优于标准配置读取
 * 
 * @author lijinghui
 *
 */
public class BundleConfig {

	private JSONObject root;

	private Path path;

	private Path bundleConfigPath;

	private boolean standalone = true;

	/**
	 * 给测试用的构造函数。测试通常用
	 * Paths.get(xx.class.getResource("config.json").toURI())的方式获得配置文件
	 * 
	 * @param bundleConfig 配置数据文件
	 * @param configPath   配置文件目录
	 */
	public BundleConfig(Path bundleConfig, Path configPath) {
		this.path = bundleConfig;
		root = loadConfigFile(path);
		this.bundleConfigPath = configPath;
	}

	public BundleConfig(String bundleId, boolean checkExist) {
		init(bundleId);
		if (checkExist)
			checkNotNull(root, "config file not found: {}", path.getFileName());
		if (root == null) {
			standalone = false;
			init("core");
			root = root.getJSONObject(bundleId);
		}
		bundleConfigPath = Platform.getHome().resolve("conf").resolve(bundleId);
	}

	private void init(String bundleId) {
		if (Platform.isDev()) {
			path = Platform.getHome().resolve("conf").resolve(bundleId + ".json.dev");
			root = loadConfigFile(path);
		}
		if (root == null) {
			path = Platform.getHome().resolve("conf").resolve(bundleId + ".json");
			root = loadConfigFile(path);
		}
	}

	public boolean isStandalone() {
		return standalone;
	}

	/**
	 * 获取bundle配置单项内容
	 * 
	 * @param bundleId
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return root == null ? null : root.getString(key);
	}

	/**
	 * 获取bundle配置单项内容，为空时返回默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getString(String key, String defaultValue) {
		String value = getString(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * 获取bundle配置单项内容,并转为整数。如果没有配置，默认返回0
	 * 
	 * @param bundleId
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return root == null ? 0 : root.getIntValue(key);
	}

	/**
	 * 获取bundle配置单项内容,并转为整数。如果没有配置，默认返回0
	 * 
	 * @param bundleId
	 * @param key
	 * @return
	 */
	public int getInt(String key, int defaultValue) {
		int value = getInt(key);
		return value == 0 ? defaultValue : value;
	}

	/**
	 * 获取bundle配置单项内容,并转为布尔
	 * 
	 * @param bundleId
	 * @param key
	 * @return
	 */
	public boolean getBool(String key) {
		if (root == null)
			return false;
		return Boolean.TRUE.equals(root.getBoolean(key));
	}

	/**
	 * 获取bundle配置单项内容,为空返回缺省值
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBool(String key, boolean defaultVal) {
		if (root == null)
			return defaultVal;
		Boolean val = root.getBoolean(key);
		return val == null ? defaultVal : val.booleanValue();
	}

	/**
	 * 获取bundle配置单项内容,并转为字符串列表
	 * 
	 * @param bundleId
	 * @param key
	 * @return
	 */
	public List<String> getList(String key) {
		if (root == null)
			return Collections.emptyList();
		List<String> result = root.getObject(key, TypeReference.LIST_STRING);
		return result == null ? Collections.emptyList() : result;
	}

	/**
	 * 获取bundle配置的对象
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	public <T> T getObject(String key, TypeReference<T> type) {
		if (root == null)
			return null;
		return root.getObject(key, type.getType());
	}

	/**
	 * 读取json配置文件（支持//注释）
	 * 
	 * @param path
	 * @return
	 */
	public static JSONObject loadConfigFile(Path path) {
		if (!Files.exists(path))
			return null;
		try {
			String text = Files.lines(path).map(String::trim).filter(s -> !s.startsWith("//"))
					.collect(Collectors.joining());
			return JSON.parseObject(text);
		} catch (JSONException je) {
			throw new RuntimeException("fail to parse json file:" + path.toString(), je);
		} catch (IOException e) {
			throw new RuntimeException("fail to read json file:" + path.toString(), e);
		}
	}

	/**
	 * 返回Bundle的配置目录
	 * 
	 * @return
	 */
	public Path getConfigPath() {
		return bundleConfigPath;
	}

	/**
	 * 返回Bundle的配置目录下的指定文件
	 * 
	 * @param fileName 文件名
	 * @return
	 */
	public Path getConfigFile(String fileName) {
		return getConfigPath().resolve(fileName);
	}

	/**
	 * 返回Bundle的配置目录下的指定文件，并读为一个字符串
	 * 
	 * @param fileName
	 * @return 文件内容
	 * @throws IOException
	 */
	public String getConfigFileAsString(String fileName) throws IOException {
		Path file = getConfigFile(fileName);
		return Utils.streamToString(Files.newInputStream(file));
	}

	/**
	 * 如果配置文件是一个json文件，直接解析为一个对象
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public final <T> T getConfigFile(final String fileName, Class<T> clazz) {
		Path file = getConfigFile(fileName);
		if (!Files.exists(file))
			return null;
		try {
			String text = Files.lines(file).map(String::trim).filter(s -> !s.startsWith("//"))
					.collect(Collectors.joining());
			return JSON.parseObject(text, clazz);
		} catch (JSONException je) {
			throw new RuntimeException("fail to parse json file:" + file.toString(), je);
		} catch (IOException e) {
			throw new RuntimeException("fail to read json file:" + file.toString(), e);
		}
	}

	/**
	 * 如果配置文件是一个json文件，直接解析为一个对象
	 * 
	 * @param fileName
	 * @param tr
	 * @return
	 * @throws IOException
	 */
	public <T> T getConfigFile(final String fileName, TypeReference<T> tr) {
		Path file = getConfigFile(fileName);
		if (!Files.exists(file))
			return null;
		try {
			String text = Files.lines(file).map(String::trim).filter(s -> !s.startsWith("//"))
					.collect(Collectors.joining());
			return JSON.parseObject(text, tr);
		} catch (JSONException je) {
			throw new RuntimeException("fail to parse json file:" + file.toString(), je);
		} catch (IOException e) {
			throw new RuntimeException("fail to read json file:" + file.toString(), e);
		}
	}

}

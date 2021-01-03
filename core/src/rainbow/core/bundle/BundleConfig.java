package rainbow.core.bundle;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import rainbow.core.platform.Platform;
import rainbow.core.util.json.JSON;

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

	private Map<String, Object> root;

	private Path configFile;

	private Path configPath;

	private boolean standalone = true;

	/**
	 * 给测试用的构造函数。测试通常用
	 * Paths.get(xx.class.getResource("config.json").toURI())的方式获得配置文件
	 * 
	 * @param bundleConfig 配置数据文件
	 * @param configPath   配置文件目录
	 */
	public BundleConfig(Path configFile, Path configPath) {
		this.configFile = configFile;
		this.configPath = configPath;
		root = JSON.parseObject(configFile);
	}

	@SuppressWarnings("unchecked")
	public BundleConfig(String bundleId, boolean checkExist) {
		init(bundleId);
		if (checkExist)
			checkNotNull(root, "config file not found: {}", configFile.getFileName());
		if (root == null) {
			standalone = false;
			init("core");
			Object obj = root.get(bundleId);
			if (obj != null) {
				if (obj instanceof Map)
					root = (Map<String, Object>) obj;
				else
					root = null;
			}
		}
		configPath = Platform.getHome().resolve("conf").resolve(bundleId);
	}

	private void init(String bundleId) {
		if (Platform.isDev()) {
			configFile = Platform.getHome().resolve("conf").resolve(bundleId + ".json.dev");
			root = JSON.parseObject(configFile);
		}
		if (root == null) {
			configFile = Platform.getHome().resolve("conf").resolve(bundleId + ".json");
			root = JSON.parseObject(configFile);
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
		return root == null ? null : (String) root.get(key);
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
		return root == null ? 0 : (Integer) root.get(key);
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
		return Boolean.TRUE.equals(root.get(key));
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
		Boolean val = (Boolean) root.get(key);
		return val == null ? defaultVal : val.booleanValue();
	}

	/**
	 * 返回Bundle的配置目录
	 * 
	 * @return
	 */
	public Path getConfigPath() {
		return configPath;
	}

	/**
	 * 返回Bundle的配置目录下的指定文件，开发模式下优先返回.dev后缀的配置文件
	 * 
	 * @param fileName 文件名
	 * @return
	 */
	public Path getConfigFile(String fileName) {
		Path file = null;
		if (Platform.isDev())
			file = getConfigPath().resolve(fileName + ".dev");
		if (Files.exists(file) == false)
			file = getConfigPath().resolve(fileName);
		return Files.exists(file) ? file : null;
	}

}

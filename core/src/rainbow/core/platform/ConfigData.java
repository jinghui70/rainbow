package rainbow.core.platform;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

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
public class ConfigData {

	private JSONObject root;

	private Path path;

	private boolean standalone = true;

	/**
	 * 给测试用的构造函数
	 * 
	 * @param root
	 */
	public ConfigData(JSONObject root) {
		this.root = root;
	}

	/**
	 * 给测试用的构造函数。测试通常用 Paths.get(xx.class.getResource("config.json").toURI())的方式获得配置文件
	 * 
	 * @param path
	 */
	public ConfigData(Path path) {
		this.path = path;
		root = Utils.loadConfigFile(path);
	}
	
	public ConfigData(String bundleId, boolean checkExist) {
		init(bundleId);
		if (checkExist)
			checkNotNull(root, "config file not found: {}", path.getFileName());
		if (root == null) {
			standalone = false;
			init("core");
			root = root.getJSONObject(bundleId);
		}
	}

	public ConfigData(String bundleId) {
		this(bundleId, false);
	}

	private void init(String bundleId) {
		if (Platform.isDev()) {
			path = Platform.getHome().resolve("conf").resolve(bundleId + ".json.dev");
			root = Utils.loadConfigFile(path);
		}
		if (root == null) {
			path = Platform.getHome().resolve("conf").resolve(bundleId + ".json");
			root = Utils.loadConfigFile(path);
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
		return "true".equals(getString(key));
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

}

package rainbow.db.database;

import java.util.Map;

/**
 * 数据源配置对象
 * 
 * @author lijinghui
 *
 */
public class DataSourceConfig {

	private String jdbcUrl;

	private String username;

	private String password;

	/**
	 * 加密器名字
	 */
	private String cipher;

	/**
	 * 用户名及密码是否已加密
	 */
	private boolean encrypted;

	/**
	 * 数据源连接池配置
	 */
	private Map<String, Object> property;

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public Map<String, Object> getProperty() {
		return property;
	}

	public void setProperty(Map<String, Object> property) {
		this.property = property;
	}

}

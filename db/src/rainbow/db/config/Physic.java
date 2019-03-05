package rainbow.db.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.MoreObjects;

/**
 * 系统配置的物理数据源
 * 
 * @author lijinghui
 * 
 */
public class Physic {

	@XmlAttribute
	private String id;

	private String driverClass;

	private String jdbcUrl;

	private String username;

	private String password;

	private List<Property> property;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

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

	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("driverClass", driverClass).add("url", jdbcUrl)
				.toString();
	}

}

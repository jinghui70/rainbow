package rainbow.db.dataSource;

import static rainbow.core.util.Preconditions.checkArgument;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import rainbow.core.util.Utils;

/**
 * Abstract base class for JDBC {@link javax.sql.DataSource} implementations
 * that operate on a JDBC {@link java.sql.Driver}.
 *
 */
public abstract class AbstractDriverBasedDataSource extends AbstractDataSource {

	private String url;

	private String username;

	private String password;

	private Properties connectionProperties;

	/**
	 * Set the JDBC URL to use for connecting through the Driver.
	 * 
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public void setUrl(String url) {
		checkArgument(Utils.hasContent(url), "Property 'url' must not be empty");
		this.url = url.trim();
	}

	/**
	 * Return the JDBC URL to use for connecting through the Driver.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Set the JDBC username to use for connecting through the Driver.
	 * 
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Return the JDBC username to use for connecting through the Driver.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Set the JDBC password to use for connecting through the Driver.
	 * 
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Return the JDBC password to use for connecting through the Driver.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Specify arbitrary connection properties as key/value pairs, to be passed to
	 * the Driver.
	 * <p>
	 * Can also contain "user" and "password" properties. However, any "username"
	 * and "password" bean properties specified on this DataSource will override the
	 * corresponding connection properties.
	 * 
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	/**
	 * Return the connection properties to be passed to the Driver, if any.
	 */
	public Properties getConnectionProperties() {
		return this.connectionProperties;
	}

	/**
	 * This implementation delegates to <code>getConnectionFromDriver</code>, using
	 * the default username and password of this DataSource.
	 * 
	 * @see #getConnectionFromDriver(String, String)
	 * @see #setUsername
	 * @see #setPassword
	 */
	public Connection getConnection() throws SQLException {
		return getConnectionFromDriver(getUsername(), getPassword());
	}

	/**
	 * This implementation delegates to <code>getConnectionFromDriver</code>, using
	 * the given username and password.
	 * 
	 * @see #getConnectionFromDriver(String, String)
	 */
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnectionFromDriver(username, password);
	}

	/**
	 * Build properties for the Driver, including the given username and password
	 * (if any), and obtain a corresponding Connection.
	 * 
	 * @param username the name of the user
	 * @param password the password to use
	 * @return the obtained Connection
	 * @throws SQLException in case of failure
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
		Properties props = new Properties(getConnectionProperties());
		if (username != null) {
			props.setProperty("user", username);
		}
		if (password != null) {
			props.setProperty("password", password);
		}
		return getConnectionFromDriver(props);
	}

	/**
	 * Obtain a Connection using the given properties.
	 * <p>
	 * Template method to be implemented by subclasses.
	 * 
	 * @param props the merged connection properties
	 * @return the obtained Connection
	 * @throws SQLException in case of failure
	 */
	protected abstract Connection getConnectionFromDriver(Properties props) throws SQLException;

}

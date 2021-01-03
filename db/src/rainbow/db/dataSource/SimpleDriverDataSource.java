package rainbow.db.dataSource;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple implementation of the standard JDBC {@link javax.sql.DataSource}
 * interface, configuring a plain old JDBC {@link java.sql.Driver} via bean
 * properties, and returning a new {@link java.sql.Connection} from every
 * <code>getConnection</code> call.
 *
 */
public class SimpleDriverDataSource extends AbstractDriverBasedDataSource {

	private Driver driver;

	/**
	 * Constructor for bean-style configuration.
	 */
	public SimpleDriverDataSource() {
	}

	/**
	 * Create a new DriverManagerDataSource with the given standard Driver
	 * parameters.
	 * 
	 * @param driver the JDBC Driver object
	 * @param url    the JDBC URL to use for accessing the DriverManager
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public SimpleDriverDataSource(Driver driver, String url) {
		setDriver(driver);
		setUrl(url);
	}

	/**
	 * Create a new DriverManagerDataSource with the given standard Driver
	 * parameters.
	 * 
	 * @param driver   the JDBC Driver object
	 * @param url      the JDBC URL to use for accessing the DriverManager
	 * @param username the JDBC username to use for accessing the DriverManager
	 * @param password the JDBC password to use for accessing the DriverManager
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public SimpleDriverDataSource(Driver driver, String url, String username, String password) {
		setDriver(driver);
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}

	/**
	 * Create a new DriverManagerDataSource with the given standard Driver
	 * parameters.
	 * 
	 * @param driver   the JDBC Driver object
	 * @param url      the JDBC URL to use for accessing the DriverManager
	 * @param conProps JDBC connection properties
	 * @see java.sql.Driver#connect(String, java.util.Properties)
	 */
	public SimpleDriverDataSource(Driver driver, String url, Properties conProps) {
		setDriver(driver);
		setUrl(url);
		setConnectionProperties(conProps);
	}

	/**
	 * Specify the JDBC Driver implementation class to use.
	 * <p>
	 * An instance of this Driver class will be created and held within the
	 * SimpleDriverDataSource.
	 * 
	 * @see #setDriver
	 */
	public void setDriverClass(Class<? extends Driver> driverClass) {
		try {
			this.driver = driverClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("create driver class faild:" + driverClass.getName());
		}
	}

	/**
	 * Specify the JDBC Driver instance to use.
	 * <p>
	 * This allows for passing in a shared, possibly pre-configured Driver instance.
	 * 
	 * @see #setDriverClass
	 */
	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	/**
	 * Return the JDBC Driver instance to use.
	 */
	public Driver getDriver() {
		return this.driver;
	}

	@Override
	protected Connection getConnectionFromDriver(Properties props) throws SQLException {
		Driver driver = getDriver();
		String url = getUrl();
		checkNotNull(driver, "Driver must not be null");
		return driver.connect(url, props);
	}

}

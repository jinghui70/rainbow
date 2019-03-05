package rainbow.db;

import java.util.Collection;

import rainbow.db.dao.Dao;

/**
 * 数据库Dao管理器
 * 
 * @author lijinghui
 * 
 */
public interface DaoManager {

	/**
	 * 返回系统配置的数据源对应的Dao对象
	 * 
	 * @param name
	 *            数据源名
	 * @return 系统配置的数据源对应的Dao对象，如果name为空，则返回缺省数据源
	 */
	public Dao getDao(String name);
	
	/**
	 * 返回系统配置的所有逻辑数据源名
	 * @return
	 */
	public Collection<String> getLogicSources();
}

package rainbow.db;

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
	 * @param name 数据源名
	 * @return 系统配置的数据源对应的Dao对象
	 */
	Dao getDao(String name);

}
